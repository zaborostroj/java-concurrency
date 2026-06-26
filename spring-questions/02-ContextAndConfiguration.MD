# Context and configuration

1. [Что такое ApplicationContext](#1-что-такое-applicationcontext)
2. [Какие бывают виды контекстов Spring (ApplicationContext, WebApplicationContext)](#2-какие-бывают-виды-контекстов-spring-applicationcontext-webapplicationcontext)
3. [Какие бывают способы конфигурации Spring (XML, Java Config, Аннотации)](#3-какие-бывают-способы-конфигурации-spring-xml-java-config-аннотации)
4. [Что делает @Configuration](#4-что-делает-configuration)
5. [Что делает @Bean](#5-что-делает-bean)
6. [Разница между @Configuration и @Component](#6-разница-между-configuration-и-component)
7. [Почему @Configuration создает прокси](#7-почему-configuration-создает-прокси)
8. [Как получить значение из application.yaml](#8-как-получить-значение-из-applicationyaml)
9. [Что такое @Value](#9-что-такое-value)
10. [Что такое @ConfigurationProperties](#10-что-такое-configurationproperties)
11. [Разница между @Value и @ConfigurationProperties](#11-разница-между-value-и-configurationproperties)

---

### Ответы

#### 1. Что такое ApplicationContext
Это главный интерфейс в Spring-приложении, представляющий IoC-контейнер. Он отвечает за загрузку определений бинов, их создание, связывание и управление их жизненным циклом.

#### 2. Какие бывают виды контекстов Spring (ApplicationContext, WebApplicationContext)
- `AnnotationConfigApplicationContext`: для Java-конфигурации.
- `ClassPathXmlApplicationContext`: для XML из ресурсов.
- `WebApplicationContext`: специализированный контекст для веб-приложений.

#### 3. Какие бывают способы конфигурации Spring (XML, Java Config, Аннотации)
- **XML**: Описание бинов в `.xml` файлах (устаревший способ).
- **Аннотации**: Использование `@Component`, `@Service`, `@Autowired` (современный способ).
- **Java Config**: Использование классов с `@Configuration` и методов с `@Bean`.

#### 4. Что делает @Configuration
Указывает, что класс является источником определений бинов. Классы с `@Configuration` позволяют определять зависимости между бинами путем вызова других `@Bean`-методов в том же классе.

#### 5. Что делает @Bean
Аннотация над методом в `@Configuration` классе. Результат выполнения метода регистрируется как бин в контексте.

#### 6. Разница между @Configuration и @Component
- `@Component` — общий стереотип, используется для автоматического сканирования.
- `@Configuration` — специализированный тип `@Component`. Главное отличие в том, что `@Configuration` бины проксируются (CGLIB), чтобы вызовы `@Bean`-методов возвращали один и тот же экземпляр бина (соблюдение Singleton).

#### 7. Почему @Configuration создает прокси
Чтобы гарантировать, что при вызове одного `@Bean`-метода из другого `@Bean`-метода внутри того же класса будет возвращен существующий бин из контекста, а не создан новый объект.

#### 8. Как получить значение из application.yaml
С помощью аннотаций `@Value` или `@ConfigurationProperties`.

#### 9. Что такое @Value
Используется для внедрения простых значений, значений из свойств (`.properties`, `.yaml`) или результатов вычисления SpEL-выражений.
```java
@Value("${my.property}")
private String property;
```

#### 10. Что такое @ConfigurationProperties
Позволяет привязать иерархические свойства из файла конфигурации к типизированному Java-объекту. Поддерживает relaxed binding (разные стили написания имен).

#### 11. Разница между @Value и @ConfigurationProperties
- `@Value`: лучше для единичных значений, поддерживает SpEL, но не поддерживает relaxed binding.
- `@ConfigurationProperties`: лучше для групп свойств, поддерживает иерархию, relaxed binding и валидацию (`@Validated`).
