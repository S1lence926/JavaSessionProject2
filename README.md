# 📚 Library System — Система управления библиотекой

Java-консольное приложение с полноценным CRUD, ООП и SQLite.

---

## Архитектура

```
library/
├── Main.java                   ← Точка входа, сборка зависимостей
├── db/
│   └── DatabaseConnection.java ← Singleton-подключение к SQLite, инициализация схемы
├── model/
│   ├── BaseEntity.java         ← Абстрактный базовый класс (id, equals, hashCode)
│   ├── Author.java
│   ├── Category.java
│   ├── Book.java
│   ├── Member.java
│   └── Loan.java
├── repository/
│   ├── CrudRepository.java     ← Generic-интерфейс CRUD
│   ├── AuthorRepository.java
│   ├── CategoryRepository.java
│   ├── BookRepository.java
│   ├── MemberRepository.java
│   └── LoanRepository.java
├── service/
│   ├── AuthorService.java
│   ├── CategoryService.java
│   ├── BookService.java
│   ├── MemberService.java
│   └── LoanService.java
├── exception/
│   └── LibraryException.java   ← Доменное исключение
└── ui/
    └── ConsoleUI.java          ← Консольный интерфейс
```

---

## Сущности и связи

```
Category ──┐
           ├── Book ──── Author
           │
Member ────└── Loan
```

| Сущность   | Описание                      |
|------------|-------------------------------|
| `Author`   | Автор книги (имя, страна)     |
| `Category` | Жанр / тематика               |
| `Book`     | Книга (название, ISBN, год, экземпляры) |
| `Member`   | Читатель (имя, email, телефон)|
| `Loan`     | Выдача книги читателю         |

---

## ООП-принципы

| Принцип          | Где применён |
|------------------|--------------|
| Инкапсуляция     | Все поля `private`/`protected`, доступ через геттеры/сеттеры |
| Наследование     | `Author`, `Book`, `Member`, `Loan`, `Category` → `BaseEntity` |
| Абстракция       | `BaseEntity` — абстрактный класс с `abstract toString()` |
| Полиморфизм      | `toString()` переопределён во всех моделях по-своему |
| Интерфейс        | `CrudRepository<T, ID>` — единый контракт для всех репозиториев |

---

## Требования

- Java 17+
- Maven 3.8+
- Интернет для первого скачивания зависимости `sqlite-jdbc`

---

## Сборка и запуск

```bash
# Сборка fat-jar (включает SQLite-драйвер)
mvn package -q

# Запуск
java -jar target/library-system.jar
```

База данных `library.db` создаётся автоматически в директории запуска.

---

## Функциональность

### Книги
- Список всех книг (с JOIN: автор, категория)
- Поиск по названию (LIKE)
- Фильтр по автору
- Добавить / редактировать / удалить

### Авторы
- Список, поиск, добавить / редактировать / удалить

### Категории
- Список, добавить / переименовать / удалить

### Читатели
- Список, поиск, добавить / редактировать / удалить

### Выдача/Возврат
- Выдать книгу (уменьшает счётчик экземпляров)
- Принять возврат (увеличивает счётчик)
- Активные выдачи / история читателя

### Статистика
- Количество книг, авторов, читателей, выдач
