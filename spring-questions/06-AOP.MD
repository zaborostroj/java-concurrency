# Spring AOP

1. [Что такое AOP](#1-что-такое-aop)
2. [Что такое Aspect](#2-что-такое-aspect)
3. [Что такое Advice](#3-что-такое-advice)
4. [Что такое Pointcut](#4-что-такое-pointcut)
5. [Что такое Join Point](#5-что-такое-join-point)
6. [Что такое Weaving](#6-что-такое-weaving)
7. [Какие бывают виды Advice (Before, After, After Returning, After Throwing, Around)](#7-какие-бывают-виды-advice-before-after-after-returning-after-throwing-around)
8. [Где применяется AOP в Spring (Transactional, Cacheable, Async, Security)](#8-где-применяется-aop-в-spring-transactional-cacheable-async-security)

---

### Ответы

#### 1. Что такое AOP
**AOP (Аспектно-ориентированное программирование)** — это парадигма, позволяющая выделить сквозную функциональность (логирование, транзакции, безопасность) в отдельные модули (аспекты), чтобы избежать дублирования кода в бизнес-логике.

#### 2. Что такое Aspect
**Aspect** — это модуль или класс, который реализует сквозную логику. В Spring это обычно класс, помеченный аннотацией `@Aspect`.

#### 3. Что такое Advice
**Advice** — это код (метод), который должен быть выполнен в определенной точке. Определяет "что" и "когда" делать.

#### 4. Что такое Pointcut
**Pointcut** — это набор правил (выражений), которые определяют, к каким методам или классам нужно применить Advice. Определяет "где" делать.

#### 5. Что такое Join Point
**Join Point** — это точка в выполнении программы, такая как вызов метода или обработка исключения. В Spring AOP Join Point всегда представляет собой вызов метода.

#### 6. Что такое Weaving
**Weaving (Внедрение)** — это процесс связывания аспектов с целевыми объектами для создания прокси-объектов. В Spring это происходит динамически в рантайме.

#### 7. Какие бывают виды Advice (Before, After, After Returning, After Throwing, Around)
- **@Before**: Выполняется до вызова метода.
- **@After**: Выполняется после завершения метода (независимо от результата).
- **@AfterReturning**: Выполняется только после успешного завершения метода.
- **@AfterThrowing**: Выполняется только если метод выбросил исключение.
- **@Around**: Самый мощный совет, который оборачивает вызов метода. Позволяет выполнить код до и после, а также решить, вызывать ли целевой метод вообще.

#### 8. Где применяется AOP в Spring (Transactional, Cacheable, Async, Security)
Spring активно использует AOP для реализации:
- `@Transactional` (управление транзакциями).
- `@Cacheable` (кеширование результатов).
- `@Async` (асинхронное выполнение).
- `@PreAuthorize` / `@Secured` (проверка прав доступа).

---

### Примеры кода

#### Пример 1: Аспект для логирования с разными типами Advice

```java
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut для всех методов в пакете service
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void serviceMethods() {
    }

    // Before: выполняется перед методом
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Before method: {}", joinPoint.getSignature().getName());
    }

    // After: выполняется после метода (всегда)
    @After("serviceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        logger.info("After method: {}", joinPoint.getSignature().getName());
    }

    // AfterReturning: выполняется только при успешном завершении
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method {} returned: {}",
                joinPoint.getSignature().getName(), result);
    }

    // AfterThrowing: выполняется только при исключении
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        logger.error("Method {} threw exception: {}",
                joinPoint.getSignature().getName(), exception.getMessage());
    }

    // Around: самый мощный, оборачивает метод
    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Around before: {}", joinPoint.getSignature().getName());

        try {
            Object result = joinPoint.proceed(); // вызов целевого метода
            logger.info("Around after: {} with result: {}",
                    joinPoint.getSignature().getName(), result);
            return result;
        } catch (Exception e) {
            logger.error("Around exception: {}", e.getMessage());
            throw e;
        }
    }
}
```

#### Пример 2: Аспект для измерения производительности

```java
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);

    @Around("@annotation(com.example.annotation.Monitored)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Method {} executed in {} ms",
                joinPoint.getSignature().getName(), executionTime);

        return result;
    }
}

// Кастомная аннотация для мониторинга
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Monitored {
}
```

#### Пример 3: Различные выражения Pointcut

```java
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PointcutExamplesAspect {

    // Все методы в определенном пакете
    @Pointcut("execution(* com.example.repository.*.*(..))")
    public void repositoryLayer() {
    }

    // Все методы с определенной аннотацией
    @Pointcut("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void transactionalMethods() {
    }

    // Все public методы
    @Pointcut("execution(public * *(..))")
    public void publicMethods() {
    }

    // Методы, начинающиеся с "get"
    @Pointcut("execution(* get*(..))")
    public void getterMethods() {
    }

    // Методы с конкретными параметрами
    @Pointcut("execution(* com.example.service.UserService.findById(Long))")
    public void findByIdMethod() {
    }

    // Комбинация pointcut'ов
    @Pointcut("repositoryLayer() && publicMethods()")
    public void publicRepositoryMethods() {
    }

    @Before("publicRepositoryMethods()")
    public void logRepositoryAccess() {
        System.out.println("Accessing repository layer");
    }
}
```

#### Пример 4: Использование в сервисе

```java
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // К этому методу будут применены аспекты
    public User findById(Long id) {
        return new User(id, "John Doe");
    }

    // Метод с кастомной аннотацией для мониторинга
    @Monitored
    public List<User> findAll() {
        // Время выполнения будет измерено PerformanceAspect
        return Arrays.asList(
                new User(1L, "John"),
                new User(2L, "Jane")
        );
    }
}
```

#### Конфигурация для включения AOP

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    // AOP включен
}
```

Или в Spring Boot добавить зависимость в `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
