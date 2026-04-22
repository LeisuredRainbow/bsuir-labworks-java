# Лабораторная работа №3: Data Caching – Туристическое агентство

**Тема:** Туристическое агентство  
**Выполнил:** Студент группы [450504] [Толкач Доминик Геннадьевич]  
**GitHub:** [https://github.com/LeisuredRainbow/bsuir-labworks-java](https://github.com/LeisuredRainbow/bsuir-labworks-java)

## Описание проекта

Данный проект расширяет лабораторные работы №1 и №2 добавлением **кэширования данных** с использованием in‑memory индекса на основе `HashMap`. Реализованы сложные поисковые запросы с фильтрацией по вложенной сущности (`Client`), пагинацией и сортировкой. Продемонстрирована работа кэша, его инвалидация при изменении данных, а также устранение проблемы N+1 для JPQL и native‑запросов.

Проект построен на **Spring Boot 4.0.5**, **Java 17**, **PostgreSQL**, **MapStruct**, **Lombok**. Код соответствует **Google Java Style** (Checkstyle) и проходит статический анализ **SonarCloud** (0 нарушений).

## Основные возможности (новое в лабораторной работе №3)

### 1. Сложные GET-запросы с фильтрацией по вложенной сущности
- Поиск бронирований **по фамилии клиента** (`lastName`).
- **JPQL-запрос** с `JOIN FETCH` для предотвращения проблемы N+1.
- **Native SQL-запрос** с проекцией в `BookingNativeProjection`, также оптимизированный (один запрос без ленивых подгрузок).
- **Пагинация и сортировка** через `Pageable` (параметры `page`, `size`, `sort`).

### 2. In‑memory кэш (индекс)
- Компонент `BookingSearchCache` хранит результаты поиска в `HashMap`.
- **Составной ключ** `BookingSearchKey` включает:
  - `lastName` (фамилия клиента)
  - `page` (номер страницы)
  - `size` (размер страницы)
  - `sort` (строка сортировки)
- Корректно переопределены `equals()` и `hashCode()` для работы с `HashMap`.
- Потокобезопасность обеспечена блоками `synchronized`.

### 3. Инвалидация кэша
- Кэш полностью очищается при любом изменении данных, влияющих на результаты поиска:
  - создание, обновление, удаление бронирования
  - (опционально) обновление/удаление клиента
- Вызов `invalidateAll()` гарантирует актуальность данных.

### 4. Устранение проблемы N+1
- **JPQL-запрос** использует `JOIN FETCH` для загрузки клиента и тура в одном запросе.
- **Native-запрос** возвращает проекцию `BookingNativeProjection`, содержащую все необходимые поля, что исключает дополнительные запросы за связями.
- Для всех GET-эндпоинтов туров (`/api/tours`) добавлены `@EntityGraph`, устраняющие N+1 при загрузке отелей и гидов.

## Технологии
- Java 17
- Spring Boot 4.0.5
- Spring Web, Spring Data JPA
- PostgreSQL
- Lombok
- MapStruct
- Jakarta Bean Validation
- Maven
- Checkstyle (Google Java Style)

## Структура проекта (новые/изменённые файлы)

src/main/java/by/bsuir/labworks/

├── cache/

│ ├── BookingSearchKey.java # составной ключ кэша

│ └── BookingSearchCache.java # in‑memory кэш на HashMap

├── config/

│ └── WebConfig.java # настройка сериализации Page

├── projection/

│ └── BookingNativeProjection.java # проекция для 

native-запроса

├── controllers/

│ └── BookingController.java # добавлены эндпоинты поиска

├── service/

│ ├── BookingService.java # логика кэширования и инвалидации

├── repository/

│ ├── BookingRepository.java # JPQL и native методы с пагинацией

│ └── TourRepository.java # добавлены @EntityGraph

└── (остальные файлы без изменений)

## Запуск

### Требования
- PostgreSQL (установлен и запущен)
- JDK 17+
- Maven

### Настройка базы данных
1. Создайте базу данных `travel_agency` и пользователя `travel_user` с паролем.
2. Настройте переменные окружения (или укажите их в `application.properties`):
   ```bash
   export DB_USERNAME=travel_user
   export DB_PASSWORD=travel_pass
3. Убедитесь, что PostgreSQL запущен: sudo systemctl start postgresql

### Запуск приложения

  ```bash
  ./mvnw spring-boot:run

### Примеры запросов (новые эндпоинты)

### Поиск бронирований по фамилии клиента (JPQL)

  GET /api/bookings/search/by-client-last-name/jpql?lastName=Вейдер&page=0&size=5&sort=bookingDate,desc

  Ответ:

  ```bash
  {
    "content": [ ... ],
    "pageable": { ... },
    "totalElements": 42,
    "totalPages": 9,
    "last": false,
    "first": true,
    ...
  }

### Поиск бронирований по фамилии клиента (Native)

  GET /api/bookings/search/by-client-last-name/native?lastName=Вейдер&page=0&size=5

### Демонстрация кэширования и инвалидации

    Первый вызов поиска → в логах видны SQL-запросы.

    Повторный вызов с теми же параметрами → SQL-запросов нет (данные из кэша).

    Обновление бронирования (например, PUT /api/bookings/{id}) → кэш очищается.

    Снова поиск → SQL-запросы снова появляются (кэш пуст, данные загружены из БД).

### Проверка стиля кода

  ```bash
  ./mvnw checkstyle:check

После исправления всех замечаний должно быть 0 ошибок.

### Статический анализ

SonarCloud – 0 нарушений (https://sonarcloud.io/summary/new_code?id=LeisuredRainbow_bsuir-labworks-java&branch=main)

### Автор

Студент группы [450504] [Толкач Доминик Геннадьевич]