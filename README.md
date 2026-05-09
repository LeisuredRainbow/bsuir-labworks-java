# Travel Agency API

**Тема:** Туристическое агентство  
**Выполнил:** Студент группы [450504] [Толкач Доминик Геннадьевич]  
**GitHub:** [https://github.com/LeisuredRainbow/bsuir-labworks-java](https://github.com/LeisuredRainbow/bsuir-labworks-java)

---

## Описание проекта

RESTful API для управления туристическим агентством. Позволяет работать с клиентами, гидами, отелями, турами и бронированиями. Реализована многоуровневая архитектура, валидация, глобальная обработка ошибок, логирование, документирование API (Swagger/OpenAPI), кэширование запросов, batch-операции и асинхронное выполнение бизнес-операций.

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
- JUnit 5, Mockito
- Maven
- Checkstyle (Google Java Style)
- SonarCloud (0 нарушений)

---

## Архитектура

Проект построен по многослойной архитектуре:

Controller → Service → Repository → Database

- **Контроллеры** – принимают HTTP-запросы и возвращают ответы.
- **Сервисы** – содержат бизнес-логику.
- **Репозитории** – работают с базой данных через Spring Data JPA.
- **DTO + MapStruct** – отделяют внутренние сущности от API-ответов.

---

## Реализованные возможности

### 1. Basic REST service
- CRUD-эндпоинты для всех сущностей.
- `GET` с `@RequestParam` и `@PathVariable`.
- DTO и MapStruct-мапперы.
- Код соответствует Google Java Style (Checkstyle 0 ошибок).

### 2. JPA (Hibernate/Spring Data)
- Модель из 5 сущностей: `Client`, `Guide`, `Hotel`, `Tour`, `Booking`.
- Связи OneToMany (`Client → Bookings`, `Tour → Bookings`) и ManyToMany (`Tour ↔ Hotel`, `Tour ↔ Guide`).
- Настроены `CascadeType` и `FetchType`, решена проблема **N+1** через `JOIN FETCH` / `@EntityGraph`.
- Метод `createBooking()` сохраняет несколько связанных сущностей (клиента и бронирование) в одной транзакции.
- Продемонстрировано **частичное сохранение без `@Transactional`** и **полный откат с `@Transactional`** при bulk-операциях.

### 3. Data caching
- In-memory кэш `BookingSearchCache` на основе `HashMap` для результатов поиска бронирований по фамилии клиента.
- Ключ `BookingSearchKey` включает все параметры запроса (корректные `equals()` и `hashCode()`).
- Инвалидация кэша при создании, обновлении и удалении бронирований.

### 4. Error logging/handling
- Глобальный обработчик ошибок `GlobalExceptionHandler` (`@ControllerAdvice`).
- Единый формат ответа: `ErrorResponseDto` с полями `timestamp`, `status`, `error`, `message`, `path`, `validationErrors`.
- Валидация входных данных через `@Valid` и аннотации Jakarta Bean Validation.
- Логирование в консоль и файл `logs/travel-agency.log` с ротацией (10 KB, архивация `.gz`, история 30 дней, максимум 1 GB).
- AOP-аспект `ServiceExecutionTimeLoggingAspect` для логирования времени выполнения сервисных методов.
- Swagger/OpenAPI с описанием эндпоинтов и DTO.

### 5. Batch data processing & Testing
- Транзакционная bulk-операция: `POST /api/bookings/bulk` (всё или ничего).
- Нетранзакционная bulk-операция: `POST /api/bookings/bulk/non-transactional` (частичное сохранение с детализацией ошибок через `PartialBulkOperationException`).
- Активное использование **Stream API** и `Optional` в сервисном слое.
- Unit-тесты (`BookingServiceTest`, `ClientServiceTest`, `GuideServiceTest`, `HotelServiceTest`, `TourServiceTest`) с Mockito.

### 6. Concurrency
- **Асинхронное подтверждение бронирования**:
  - `POST /async/confirm?bookingId={id}` – запускает операцию, возвращает `taskId`.
  - `GET /async/confirm/{taskId}` – статус задачи (`PENDING`, `RUNNING`, `SUCCESS`, `FAILED`).
  - `GET /async/confirm/metrics` – метрики (`submitted`, `running`, `succeeded`, `failed`).
  - Асинхронный воркер `@Async` с настраиваемой задержкой (`app.async.confirmation-delay-ms`).
- **Потокобезопасные атомарные счётчики** (`AtomicLong`) для метрик.
- **Демонстрация race condition**:
  - `GET /concurrency/race-demo?threads=64&incrementsPerThread=10000` – показывает расхождение несинхронизированного счётчика и корректность `synchronized` и `AtomicInteger`.

### 7. Client (SPA)
- **React-приложение** (TypeScript, Vite, TanStack Router, TanStack Query, Tailwind CSS).
- Отдельный репозиторий: [travel-agency-frontend](https://github.com/LeisuredRainbow/travel-agency-frontend).
- Реализованы **CRUD-операции** для всех сущностей, отображение связей **OneToMany** и **ManyToMany**, фильтрация туров.
- Развёрнут на Render как **Static Site**.

### 8. Deploy
- **Dockerfile** с многоэтапной сборкой (maven + jre) и healthcheck.
- **Docker Compose** (приложение + PostgreSQL) с использованием переменных окружения.
- Размещение на бесплатном PaaS **[Render.com](https://render.com)**.
- **CI/CD** (GitHub Actions): автоматические сборка, тестирование, анализ SonarCloud.
- Автоматический деплой при пуше в ветку `main`.
- Консольное логирование на проде (`LOGGING_CONFIG=classpath:logback-render.xml`).

---

## Запуск приложения

### Продакшен-окружение

Приложение развёрнуто на [Render.com](https://render.com).
- **Swagger UI:** [https://travel-agency-api-ddxk.onrender.com](https://travel-agency-api-ddxk.onrender.com)
- **Health check:** [https://travel-agency-api-ddxk.onrender.com/actuator/health](https://travel-agency-api-ddxk.onrender.com/actuator/health)

### Требования
- PostgreSQL (установлен и запущен)
- JDK 21+
- Maven

### Настройка базы данных
1. Создайте базу `travel_agency` и пользователя, например:
   ```sql
   CREATE USER travel_user WITH PASSWORD 'travel_pass';
   CREATE DATABASE travel_agency OWNER travel_user;
2. Установите переменные окружения:
   ```
   export DB_USERNAME=travel_user
   export DB_PASSWORD=travel_pass

### Запуск приложения

  ```
  ./mvnw spring-boot:run

  После запуска:

    Swagger UI: http://localhost:8080/swagger-ui.html

    Логи: logs/travel-agency.log
   ``` 

### Примеры запросов для демонстрации

### Базовые операции

  ```
  # Получить все отели
  curl http://localhost:8080/api/hotels

  # Получить отель по адресу
  curl http://localhost:8080/api/hotels/by-address?address=Minsk,Nezavisimosti%201

  # Создать бронирование
  curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"clientId":1,"tourId":2,"bookingDate":"2026-12-25","status":"PENDING"}'
  ```

### Ошибки и валидация

  ```
  # 400 – неверный тип параметра
  curl "http://localhost:8080/api/hotels/abc"

  # 404 – несуществующий ресурс
  curl "http://localhost:8080/api/hotels/9999"

  # 400 – пустое имя клиента
  curl -X POST http://localhost:8080/api/clients \
  -H "Content-Type: application/json" \
  -d '{"firstName":"","lastName":"Ivanov","email":"test@mail.com"}'
  ```
  
### Bulk-операции

  ```
  # Транзакционная (всё или ничего)
  curl -X POST http://localhost:8080/api/bookings/bulk \
  -H "Content-Type: application/json" \
  -d '[{"clientId":1,"tourId":2,"bookingDate":"2026-12-25","status":"PENDING"},{"clientId":999,"tourId":2,"bookingDate":"2026-12-26","status":"PENDING"}]'

  # Нетранзакционная (частичное сохранение)
  curl -X POST http://localhost:8080/api/bookings/bulk/non-transactional \
  -H "Content-Type: application/json" \
  -d '[{"clientId":1,"tourId":2,"bookingDate":"2026-12-25","status":"PENDING"},{"clientId":999,"tourId":2,"bookingDate":"2026-12-26","status":"PENDING"}]'
  ```

### Асинхронное подтверждение бронирования

  ```
  # Запустить подтверждение (замените bookingId на существующий)
  curl -X POST "http://localhost:8080/async/confirm?bookingId=7"

  # Проверить статус задачи (подставьте полученный taskId)
  curl "http://localhost:8080/async/confirm/90fb0f52-53e4-4dd0-bce8-67c20db4f4ce"

  # Получить метрики
  curl "http://localhost:8080/async/confirm/metrics"
  ```

Демонстрация race conditio

  ```
  # Запустить демонстрацию гонки потоков (50+ потоков)
  curl "http://localhost:8080/concurrency/race-demo?threads=64&incrementsPerThread=10000"
  ```

### Сборка и проверка качества

### Checkstyle

  ```
  ./mvnw checkstyle:check
  ```

### Компиляция и тесты

  ```
  ./mvnw clean compile
  ./mvnw test
  ```

### Статический анализ (SonarCloud)

  ```
  https://sonarcloud.io/summary/new_code?id=LeisuredRainbow_bsuir-labworks-java&branch=main– 0 нарушений по всем метрикам.
  ```

### Структура проекта (основные пакеты)

src/main/java/by/bsuir/labworks/

├── aspect/          – Аспекты (логирование времени)

├── cache/           – Кэш поиска бронирований

├── config/          – Конфигурация (OpenAPI, AsyncProperties, WebConfig)

├── controllers/     – REST-контроллеры

├── dto/             – DTO для запросов/ответов

├── entity/          – JPA-сущности

├── exception/       – Глобальный обработчик и исключения

├── mapper/          – MapStruct-мапперы

├── projection/      – Проекции для нативных запросов

├── repository/      – Репозитории Spring Data JPA

└── service/         – Бизнес-логика

### Автор 

Студент группы [450504] [Толкач Доминик Геннадьевич]
GitHub: https://github.com/LeisuredRainbow/bsuir-labworks-java