# Лабораторная работа №2: JPA (Hibernate/Spring Data) – Туристическое агентство

**Тема:** Туристическое агентство  
**Выполнил:** Студент группы [450504] [Толкач Доминик Геннадьевич]  
**GitHub:** [https://github.com/LeisuredRainbow/bsuir-labworks-java](https://github.com/LeisuredRainbow/bsuir-labworks-java)

## Описание проекта

Данный проект является продолжением лабораторной работы №1. Реализована полноценная модель данных для туристического агентства с использованием **PostgreSQL** и **JPA/Hibernate**. В проекте настроены связи между сущностями, продемонстрированы проблемы N+1 и транзакционности, а также реализованы CRUD-операции для всех сущностей.

Проект построен на **Spring Boot 4.0.3**, **Java 17**, **PostgreSQL**, **MapStruct**, **Lombok**. Код соответствует **Google Java Style** (Checkstyle) и проходит статический анализ **SonarCloud** (0 нарушений).

## Основные возможности

### Модель данных (5 сущностей)
- `Client` – клиент (ФИО, email, телефон)
- `Tour` – тур (название, страна, цена, длительность, описание, горящий тур)
- `Hotel` – отель (название, город, адрес, звёздность)
- `Guide` – гид (имя, фамилия, телефон, email, стаж)
- `Booking` – бронирование (связь клиента и тура, дата, статус)

### Связи между сущностями
- `@OneToMany` / `@ManyToOne` между `Client` и `Booking` (один клиент → много бронирований)
- `@OneToMany` / `@ManyToOne` между `Tour` и `Booking` (один тур → много бронирований)
- `@ManyToMany` между `Tour` и `Hotel` (многие ко многим, через таблицу `tour_hotel`)
- `@ManyToMany` между `Tour` и `Guide` (многие ко многим, через таблицу `tour_guide`)

### CRUD операции
Для каждой сущности реализованы REST-контроллеры с полным набором операций (GET, POST, PUT, DELETE) с использованием DTO (Request/Response) и мапперов MapStruct.

### Демонстрация проблем и их решений

#### 1. Проблема N+1 и её решение
- **N+1:** эндпоинт `GET /api/tours/country?country=...` загружает туры, а затем при маппинге догружает отели и гидов отдельными запросами (множество SQL).
- **Решение:** эндпоинты `GET /api/tours/price/max?maxPrice=...` и `GET /api/tours/price?price=...` используют `@EntityGraph`, загружая все связанные данные одним запросом.

#### 2. Транзакционность
Метод `createBooking` в `BookingService` умеет создавать бронирование либо для существующего клиента (`clientId`), либо для нового клиента (передавая `firstName`, `lastName`, `email`, `phone`).  
- **Без `@Transactional`** (аннотация закомментирована): при ошибке (например, неверный `tourId`) новый клиент сохраняется, а бронь – нет (частичное сохранение).  
- **С `@Transactional`**: при ошибке ни клиент, ни бронь не сохраняются (полный откат).

#### 3. Фильтрация по цене
- `GET /api/tours/price/min?minPrice=1000` – фильтрация по минимальной цене (проблема N+1).
- `GET /api/tours/price/max?maxPrice=1500` – фильтрация по максимальной цене (решение N+1 с `@EntityGraph`).

## Технологии
- Java 17
- Spring Boot 4.0.4
- Spring Web, Spring Data JPA
- PostgreSQL
- Lombok
- MapStruct
- Jakarta Bean Validation
- Maven
- Checkstyle (Google Java Style)

## Структура проекта

src/main/java/by/bsuir/labworks/

├── booking/

│ ├── controller/ # BookingController

│ ├── dto/ # BookingRequestDto, BookingResponseDto

│ ├── entity/ # Booking

│ ├── mapper/ # BookingMapper

│ ├── repository/ # BookingRepository

│ └── service/ # BookingService

├── client/ # аналогичная структура

├── tour/ # аналогичная структура

├── hotel/ # аналогичная структура

├── guide/ # аналогичная структура

└── LabworksApplication.java

## Запуск

### Требования
- PostgreSQL (установлен и запущен)
- JDK 17+
- Maven

### Настройка базы данных
1. Создайте базу данных `travel_agency` и пользователя `travel_user` с паролем (например, `travel_pass`).
2. Настройте переменные окружения (или укажите их в `application.properties`):
   ```bash
   export DB_USERNAME=travel_user
   export DB_PASSWORD=travel_pass
3. Убедитесь, что PostgreSQL запущен: sudo systemctl start postgresql

### Запуск приложения
./mvnw spring-boot:run

### Примеры запросов

### Клиенты (/api/clients)

### Создать клиента
POST http://localhost:8080/api/clients
Content-Type: application/json

{
  "firstName": "Иван",
  "lastName": "Петров",
  "email": "ivan@mail.com",
  "phone": "+375(29)123-45-67"
}

### Получить всех клиентов
GET http://localhost:8080/api/clients

### Туры (/api/tours)
### Создать тур с отелями и гидами
POST http://localhost:8080/api/tours
Content-Type: application/json

{
  "name": "Отдых в Италии",
  "country": "Италия",
  "durationDays": 7,
  "price": 1500.00,
  "hot": true,
  "description": "Рим, Флоренция, Венеция",
  "hotelIds": [1,2],
  "guideIds": [1]
}

### Фильтрация по стране (проблема N+1)
GET http://localhost:8080/api/tours/country?country=Япония

### Фильтрация по минимальной цене (проблема N+1)
GET http://localhost:8080/api/tours/price/min?minPrice=1000

### Фильтрация по максимальной цене (решение N+1)
GET http://localhost:8080/api/tours/price/max?maxPrice=2000

### Поиск по точной цене (решение N+1)
GET http://localhost:8080/api/tours/price?price=1500

### Отели (/api/hotels)
### Создать отель
POST http://localhost:8080/api/hotels
Content-Type: application/json

{
  "name": "Grand Hotel Roma",
  "city": "Рим",
  "address": "Via del Corso, 1",
  "stars": 5
}

### Гиды (/api/guides)
### Создать гида
POST http://localhost:8080/api/guides
Content-Type: application/json

{
  "firstName": "Мария",
  "lastName": "Росси",
  "phone": "+39012345678",
  "email": "maria@guide.it",
  "experienceYears": 10
}

### Бронирования (/api/bookings)
### Создать бронирование для существующего клиента
POST http://localhost:8080/api/bookings
Content-Type: application/json

{
  "clientId": 1,
  "tourId": 1,
  "bookingDate": "2026-08-15",
  "status": "CONFIRMED"
}

### Создать бронирование с новым клиентом (одна транзакция)
POST http://localhost:8080/api/bookings
Content-Type: application/json

{
  "firstName": "Люк",
  "lastName": "Скайуокер",
  "email": "luke@jedi.com",
  "phone": "+375(33)777-77-77",
  "tourId": 5,
  "bookingDate": "2026-09-01",
  "status": "PENDING"
}

### Получить все бронирования
GET http://localhost:8080/api/bookings

### Демонстрация транзакций
Чтобы показать частичное сохранение (без @Transactional):

1. В BookingService.createBooking закомментируйте аннотацию @Transactional.

2. Отправьте запрос на создание брони с новым клиентом и несуществующим tourId (например, 9999).

3. Проверьте БД: клиент сохранился, бронь – нет.

Для демонстрации полного отката верните аннотацию и повторите запрос – ни клиент, ни бронь не сохранятся.

### Проверка стиля кода
./mvnw checkstyle:check

### После исправления всех замечаний должно быть 0 ошибок.

### Статический анализ
``https://sonarcloud.io/summary/new_code?id=LeisuredRainbow_bsuir-labworks-java&branch=main```
### SonarCloud – 0 нарушений.
### Автор
Студент группы [450504] [Толкач Доминик Геннадьевич]