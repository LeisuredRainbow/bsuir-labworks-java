# Лабораторная работа №4: Error Logging/Handling – Туристическое агентство

**Тема:** Туристическое агентство  
**Выполнил:** Студент группы [450504] [Толкач Доминик Геннадьевич]  
**GitHub:** [https://github.com/LeisuredRainbow/bsuir-labworks-java](https://github.com/LeisuredRainbow/bsuir-labworks-java)

## Описание

Данная лабораторная работа расширяет функциональность предыдущих работ добавлением **глобальной обработки ошибок**, **валидации входных данных**, **логирования с ротацией**, **AOP для логирования времени выполнения сервисных методов** и **документации API через Swagger/OpenAPI**.

Все сообщения об ошибках и логи переведены на **английский язык**.

---

## Реализованные возможности

### 1. Глобальная обработка ошибок (GlobalExceptionHandler)
- Перехватывает и обрабатывает:
  - `NoSuchElementException`, `EmptyResultDataAccessException` → **404 Not Found**
  - `IllegalArgumentException`, `IllegalStateException` → **400 Bad Request**
  - `MethodArgumentNotValidException`, `BindException`, `ConstraintViolationException` → **400 Bad Request** с деталями валидации
  - `DataIntegrityViolationException` → **409 Conflict**
  - `MethodArgumentTypeMismatchException` → **400 Bad Request**
  - `HttpMessageNotReadableException` (некорректный JSON) → **400 Bad Request**
  - `Exception` → **500 Internal Server Error** (страховка)
- Единый формат ответа: `ErrorResponseDto` (timestamp, status, error, message, path, validationErrors)

### 2. Валидация входных данных
- Используются аннотации Jakarta Validation (`@NotNull`, `@NotBlank`, `@Email`, `@Positive`, `@Future`, `@Size`, `@Min`, `@Max`).
- В контроллерах – `@Valid` для `@RequestBody` и `@Validated` для параметров `@RequestParam`/`@PathVariable`.

### 3. Логирование (Logback)
- Конфигурация: `logback-spring.xml`
- Вывод в консоль и в файл `logs/travel-agency.log`
- **Ротация**: по размеру (10 KB), архивация `.gz`, история 30 дней, максимальный размер всех архивов 1 GB.
- Уровни:
  - `by.bsuir.labworks` – `DEBUG`
  - `org.hibernate.SQL` – `INFO`
  - `org.hibernate.orm.jdbc.bind` – `WARN`
  - `root` – `INFO`

### 4. AOP (логирование времени выполнения сервисных методов)
- Аспект `ServiceExecutionTimeLoggingAspect`
- Применяется ко всем методам пакета `by.bsuir.labworks.service..*`
- Логирует время выполнения в milliseconds (формат `X.XXX ms`)

### 5. Документация API (Swagger/OpenAPI)
- Конфигурация: `OpenApiConfig` (заголовок, версия, описание)
- Контроллеры аннотированы `@Tag`, методы – `@Operation`, параметры – `@Parameter`
- DTO содержат `@Schema` с примерами и описаниями
- Доступно по адресу: `http://localhost:8080/swagger-ui.html`

### 6. Строгий JSON
- В `application.properties` включено `spring.jackson.deserialization.fail-on-unknown-properties=true`
- Любое неизвестное поле в JSON вызывает **400 Bad Request**

---

## Технологии
- Java 21 (совместима с Java 17)
- Spring Boot 4.0.6
- Spring Web, Spring Data JPA, Spring AOP
- PostgreSQL
- Lombok, MapStruct
- Jakarta Bean Validation
- Logback (ротация)
- Swagger/OpenAPI (springdoc-openapi-starter-webmvc-ui)
- Maven
- Checkstyle (Google Java Style)
- SonarCloud (0 нарушений)

---

## Запуск

### Требования
- PostgreSQL (установлен и запущен)
- JDK 21+
- Maven

### Настройка базы данных
1. Создайте базу `travel_agency` и пользователя `travel_user` с паролем.
2. Установите переменные окружения:
   ```bash
   export DB_USERNAME=travel_user
   export DB_PASSWORD=travel_pass
3. Убедитесь, что PostgreSQL запущен.

### Запуск приложения

'''bash
./mvnw spring-boot:run

После запуска:

Swagger UI: http://localhost:8080/swagger-ui.html

Логи пишутся в logs/travel-agency.log

### Примеры запросов для демонстрации

1. 400 Bad Request (неверный тип параметра)
'''bash
curl -X GET "http://localhost:8080/api/hotels/abc"
Ответ: 400, Invalid value for parameter 'id': abc

2. 400 Bad Request (пустое имя клиента)
'''bash
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"","lastName":"Ivanov","email":"test@mail.com"}'
Ответ: 400, validationErrors содержит firstName: First name is required

3. 404 Not Found (несуществующий отель)
'''bash
curl -X GET http://localhost:8080/api/hotels/9999
Ответ: 404, Hotel not found with id: 9999

4. 409 Conflict (дубликат email клиента)
'''bash
# Сначала создать клиента
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"conflict@test.com"}'
# Повторить тот же email
curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Jane","lastName":"Roe","email":"conflict@test.com"}'
Ответ: 409, нарушение уникальности

5. 400 Bad Request (некорректный JSON – лишняя запятая)
'''bash
curl -X POST http://localhost:8080/api/hotels \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","address":"Address","stars":5,}'
Ответ: 400, Malformed JSON request. Please check your request body syntax.

6. Демонстрация AOP (логирование времени)
'''bash
curl "http://localhost:8080/api/bookings/search/by-client-last-name/jpql?lastName=Vader&page=0&size=5"
В консоли/файле лога появится строка:
BookingService.searchBookingsByClientLastNameJpql(..) executed in 12.345 ms

7. Swagger UI
Откройте в браузере: http://localhost:8080/swagger-ui.html

### Сборка и проверка качества
Checkstyle
'''bash
./mvnw checkstyle:check

Должно быть 0 ошибок.

### Компиляция и тесты

'''bash
./mvnw clean compile
./mvnw test

### Статический анализ (SonarCloud)

https://sonarcloud.io/summary/new_code?id=LeisuredRainbow_bsuir-labworks-java&branch=main
– 0 нарушений по всем метрикам.

### Структура проекта (новые/изменённые файлы для лабораторной №4)

src/main/java/by/bsuir/labworks/

├── exception/

│   ├── ErrorResponseDto.java

│   └── GlobalExceptionHandler.java

├── aspect/

│   └── ServiceExecutionTimeLoggingAspect.java

├── config/

│   └── OpenApiConfig.java

├── controllers/

│   ├── BookingController.java

│   ├── ClientController.java

│   ├── GuideController.java

│   ├── HotelController.java

│   └── TourController.java

├── dto/

│   ├── BookingRequestDto.java

│   ├── ClientRequestDto.java

│   ├── GuideRequestDto.java

│   ├── HotelRequestDto.java

│   ├── TourRequestDto.java

│   └── ... (остальные DTO)

├── service/

│   ├── BookingService.java

│   ├── ClientService.java

│   ├── GuideService.java

│   ├── HotelService.java

│   └── TourService.java

└── resources/

    ├── logback-spring.xml

    └── application.properties

### Автор

Студент группы [450504] [Толкач Доминик Геннадьевич]