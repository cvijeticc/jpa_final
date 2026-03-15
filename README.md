# Educational Platform -- Spring Data JPA

A Spring Boot application that models an online educational platform, built to explore and demonstrate core JPA and Hibernate concepts in practice. The domain covers authors, courses, sections, lectures, and polymorphic learning resources.

---

## Motivation

This project was built as a focused study of Spring Data JPA internals. Rather than scaffolding a full REST API, the goal was to go deep on persistence-layer concerns: entity mapping strategies, query derivation, custom JPQL, the Specification API, and Hibernate's handling of inheritance. Each design decision in the codebase maps directly to a JPA concept worth understanding at the implementation level.

---

## Domain Model

The schema models a platform where authors create courses, courses contain ordered sections, sections contain lectures, and each lecture is linked to exactly one learning resource. Resources are polymorphic -- they can be a video, a text document, or a downloadable file.

```
Author *----* Course
               |
               | 1
               |
               * Section
                   |
                   | 1
                   |
                   * Lecture 1----1 Resource
                                      ^
                                      |
                              -----------------
                              |       |       |
                            Video   Text    File
```

### Relationship Summary

| Relationship          | Type         | Owner Side | JPA Annotation                 |
|-----------------------|--------------|------------|--------------------------------|
| Author -- Course      | Many-to-Many | Course     | @JoinTable                     |
| Course -- Section     | One-to-Many  | Section    | @ManyToOne + @JoinColumn       |
| Section -- Lecture    | One-to-Many  | Lecture    | @ManyToOne + @JoinColumn       |
| Lecture -- Resource   | One-to-One   | Lecture    | @OneToOne + @JoinColumn        |
| Resource inheritance  | TABLE_PER_CLASS | --      | @Inheritance(strategy=...)     |

---

## JPA Concepts Demonstrated

### Entity Inheritance -- TABLE_PER_CLASS

`Resource` is the base entity. `Video`, `Text`, and `File` extend it. The inheritance strategy is `TABLE_PER_CLASS`, meaning Hibernate creates a separate table for each concrete subclass with all inherited columns duplicated. This avoids null columns (unlike `SINGLE_TABLE`) and avoids joins on every query (unlike `JOINED`).

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Resource {
    @Id @GeneratedValue
    private Integer id;
    private String name;
    private int size;
    private String url;
}

@Entity
public class Video extends Resource {
    private int length;
}
```

The codebase includes commented-out annotations for `SINGLE_TABLE` with `@DiscriminatorColumn` / `@DiscriminatorValue` and `JOINED` with `@PrimaryKeyJoinColumn`, making it easy to switch strategies and observe how Hibernate generates different DDL and SQL for each approach.

### @MappedSuperclass -- BaseEntity

`BaseEntity` provides audit fields (`createdAt`, `lastModifiedAt`, `createdBy`, `lastModifiedBy`) to all entities that extend it. Unlike `@Entity` inheritance, `@MappedSuperclass` does not create a separate table -- the fields are simply included in each child table. The `@SuperBuilder` annotation from Lombok enables the builder pattern across the inheritance chain.

### Embeddable Types

The `Order` entity demonstrates two embeddable concepts:

- `@EmbeddedId` with `OrderId` (composite primary key consisting of `username` + `orderDate`), implementing `Serializable` as required by JPA spec
- `@Embedded` with `Address` (value object with `streetName`, `houseNumber`, `zipCode` mapped as columns in the owning table, not as a separate table)

### Spring Data Query Derivation

`AuthorRepository` demonstrates how Spring Data JPA generates SQL from method names at startup. Derived query methods used in this project:

```java
List<Author> findAllByFirstName(String fn);
List<Author> findAllByFirstNameIgnoreCase(String fn);
List<Author> findAllByFirstNameContainingIgnoreCase(String fn);   // LIKE '%value%'
List<Author> findAllByFirstNameStartsWithIgnoreCase(String fn);   // LIKE 'value%'
List<Author> findAllByFirstNameEndsWithIgnoreCase(String fn);     // LIKE '%value'
List<Author> findAllByFirstNameInIgnoreCase(List<String> names);  // IN (...)
```

### Named Queries

Defined at the entity level with `@NamedQueries`, compiled and validated at application startup (fail-fast):

```java
@NamedQuery(name = "Author.findByNamedQuery",
            query = "select a from Author a where a.age >= :age")
@NamedQuery(name = "Author.updateByNamedQuery",
            query = "update Author a set a.age = :age")
```

### Custom @Query with @Modifying

The repository also includes inline JPQL for cases where derived methods become unreadable:

```java
@Modifying @Transactional
@Query("update Author a set a.age = :age where a.id = :id")
int updateAuthor(int age, int id);
```

The `int` return type represents the number of affected rows, which is useful for verifying that the update actually modified a record.

### Specification API (Criteria API Wrapper)

`AuthorSpecification` provides type-safe, composable query predicates:

```java
public static Specification<Author> hasAge(int age) {
    return (root, query, builder) -> {
        if (age < 0) return null;   // null predicate is ignored by Spring
        return builder.equal(root.get("age"), age);
    };
}
```

Specifications are combined in the application runner using `Specification.where(...).or(...)`, demonstrating how complex queries can be built dynamically at runtime without string concatenation.

### Lombok Integration

All entities use `@Data`, `@SuperBuilder`, `@NoArgsConstructor`, and `@AllArgsConstructor`. The `@ToString(exclude = "courses")` on `Author` prevents infinite recursion in bidirectional relationships. `@EqualsAndHashCode(callSuper = true)` ensures parent fields are included in equality checks.

---

## Project Structure

```
src/main/java/com/example/jpa_final/
|-- models/
|   |-- BaseEntity.java            @MappedSuperclass with audit fields
|   |-- Author.java                @NamedQueries, @ManyToMany, @CreationTimestamp
|   |-- Course.java                @JoinTable owner of Author relationship
|   |-- Section.java               Ordered sections within a course
|   |-- Lecture.java               @OneToOne with Resource
|   |-- Resource.java              @Inheritance(TABLE_PER_CLASS) base
|   |-- Video.java                 Extends Resource (adds length)
|   |-- Text.java                  Extends Resource (adds content)
|   |-- File.java                  Extends Resource (adds type)
|   |-- embedded/
|       |-- Address.java           @Embeddable value object
|       |-- Order.java             @EmbeddedId composite key example
|       |-- OrderId.java           Composite key (Serializable)
|-- repositories/
|   |-- AuthorRepository.java      Derived queries, @Query, @NamedQuery, Specifications
|   |-- VideoRepository.java       Basic JpaRepository
|-- specification/
|   |-- AuthorSpecification.java   Criteria API predicates
|-- JpaFinalApplication.java       CommandLineRunner with usage examples
```

---

## Tech Stack

| Component       | Version  |
|-----------------|----------|
| Java            | 17       |
| Spring Boot     | 4.0.2    |
| Spring Data JPA | 4.x      |
| Hibernate       | 7.x      |
| PostgreSQL      | 15+      |
| MySQL           | 8+ (alternate profile) |
| Lombok          | 1.18+    |
| JavaFaker       | 1.0.2    |
| Maven           | 3.9+     |

---

## Running the Project

### Prerequisites

- Java 17+
- Maven 3.9+
- PostgreSQL running on port 5432 with a database named `data_jpa`

### Setup

```bash
# Clone the repository
git clone https://github.com/<username>/jpa-educational-platform.git
cd jpa-educational-platform

# Create the database (PostgreSQL)
psql -U postgres -c "CREATE DATABASE data_jpa;"

# Run with default PostgreSQL profile
./mvnw spring-boot:run

# Or run with MySQL profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

Hibernate is configured with `ddl-auto: update`, so tables are created automatically on first run. SQL logging is enabled (`show-sql: true`) to observe the generated queries.

### Seed Data

The `CommandLineRunner` bean uses JavaFaker to generate 50 author records with randomized names, ages, and emails. Uncomment `repository.save(author)` in `JpaFinalApplication.java` to populate the database on startup.

---

## Design Decisions

**Why TABLE_PER_CLASS for resources?** Each resource type (Video, Text, File) has distinct fields. `SINGLE_TABLE` would create many nullable columns. `JOINED` adds a join on every query. `TABLE_PER_CLASS` gives each type its own clean table while preserving polymorphic queries through `UNION ALL`.

**Why @MappedSuperclass instead of @Entity for BaseEntity?** Audit fields like `createdAt` and `createdBy` are cross-cutting concerns, not a domain concept. There is no reason to query "all base entities" -- they only exist as part of concrete entities. `@MappedSuperclass` avoids an unnecessary table and inheritance overhead.

**Why Specifications over derived queries for complex filters?** Derived method names become unreadable beyond two or three conditions. Specifications are composable at runtime, making them suitable for dynamic filtering in search or list endpoints.

---

## License

This project was built for educational purposes as part of coursework at the Faculty of Organizational Sciences, University of Belgrade.
