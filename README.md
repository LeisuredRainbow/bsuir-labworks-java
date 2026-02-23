# Лабораторная работа №1: Basic REST service (Туристическое агентство)

**Тема:** Туристическое агентство  
**Сущность:** Тур (`Tour`)  
**Выполнил:** Студент группы [450504] [Толкач Доминик Геннадьевич]  
**GitHub:** [https://github.com/LeisuredRainbow/bsuir-labworks-java](https://github.com/LeisuredRainbow/bsuir-labworks-java)

## Описание проекта
Данный проект представляет собой REST API для туристического агентства. Реализован базовый CRUD для сущности `Tour` (согласно требованиям лабораторной работы №1). Проект построен на Spring Boot с использованием Maven, JPA (H2), Lombok.

## Функциональность
- **Получение списка всех туров:** `GET /api/tours`
- **Получение тура по ID:** `GET /api/tours/{id}`
- **Фильтрация туров по стране:** `GET /api/tours?country={country}`
- **Создание нового тура:** `POST /api/tours` (с телом JSON)

## Технологии
- Java 17
- Spring Boot 4.0.3
- Spring Web, Spring Data JPA
- H2 Database (in-memory)
- Lombok
- Maven
- Checkstyle (Google Java Style)

## Структура проекта
src/main/java/by/bsuir/labworks/

├── controller/ # REST контроллеры (TourController)

├── service/ # бизнес-логика (TourService)

├── repository/ # JPA репозитории (TourRepository)

├── model/ # сущности JPA (Tour)

├── dto/ # DTO (TourDto)

├── mapper/ # мапперы (TourMapper)

└── LabworksApplication.java

## Запуск
1. Клонировать репозиторий:  
   `git clone https://github.com/LeisuredRainbow/bsuir-labworks-java.git`
2. Перейти в папку проекта: `cd bsuir-labworks-java`
3. Запустить: `./mvnw spring-boot:run`
4. Приложение будет доступно по адресу `http://localhost:8080`

## Примеры запросов
Файл `requests.http` содержит тестовые запросы. Ниже приведены примеры для ручного тестирования.

### GET-запросы
```http
### Получить все туры
GET http://localhost:8080/api/tours

### Получить туры по стране (Италия)
GET http://localhost:8080/api/tours?country=Италия

### Получить тур по ID
GET http://localhost:8080/api/tours/1

```
### POST-запрос (создание тура)
```http
### Создать новый тур
POST http://localhost:8080/api/tours
Content-Type: application/json

{
    "name": "Отдых в Турции",
    "country": "Турция",
    "durationDays": 7,
    "price": 500.00,
    "hot": true
}
```
После успешного создания в ответе придёт JSON с присвоенным id. Затем GET-запросы будут возвращать созданные туры.

Проверка стиля кода
```http
./mvnw checkstyle:check
```
(после исправления замечаний должно быть 0 ошибок).

Были использованы gitignore и SonarCloud:

```http(https://sonarcloud.io/summary/new_code?id=LeisuredRainbow_bsuir-labworks-java&branch=main)```

SonarCloud = 0.