# Spring Boot

1. [Разница между Spring и Spring Boot](#1-разница-между-spring-и-spring-boot)
2. [Что делает @SpringBootApplication (@SpringBootConfiguration, @EnableAutoConfiguration, @ComponentScan)](#2-что-делает-springbootapplication-springbootconfiguration-enableautoconfiguration-componentscan)
3. [Что такое AutoConfiguration](#3-что-такое-autoconfiguration)
4. [Как работает AutoConfiguration](#4-как-работает-autoconfiguration)
5. [Что такое Starter](#5-что-такое-starter)
6. [Какие популярные Starter'ы существуют](#6-какие-популярные-starterы-существуют)
7. [Что такое Spring Factories](#7-что-такое-spring-factories)
8. [Что такое AutoConfiguration.imports](#8-что-такое-autoconfigurationimports)

---

### Ответы

#### 1. Разница между Spring и Spring Boot
**Spring Framework** — это основа, предоставляющая IoC, DI и другие базовые модули. Требует детальной настройки (XML или Java-конфигурация).
**Spring Boot** — это надстройка над Spring, реализующая концепцию "Convention over Configuration". Он включает в себя встроенный сервер (Tomcat/Jetty), управление зависимостями через стартеры и механизм автоконфигурации.

#### 2. Что делает @SpringBootApplication (@SpringBootConfiguration, @EnableAutoConfiguration, @ComponentScan)
Это мета-аннотация, объединяющая три другие:
- **@SpringBootConfiguration**: Маркирует класс как конфигурационный.
- **@EnableAutoConfiguration**: Указывает Spring Boot автоматически настраивать контекст на основе зависимостей в classpath.
- **@ComponentScan**: Запускает сканирование пакета, в котором находится класс, для поиска бинов (@Component, @Service и др.).

#### 3. Что такое AutoConfiguration
Это процесс автоматического создания бинов и настройки инфраструктуры (например, источника данных, сервлетов) на основе обнаруженных в проекте библиотек.

#### 4. Как работает AutoConfiguration
Механизм опирается на условные аннотации (`Conditional Annotations`):
- `@ConditionalOnClass`: Если класс присутствует в classpath.
- `@ConditionalOnMissingBean`: Если бин еще не создан пользователем.
- `@ConditionalOnProperty`: Если установлена определенная настройка в `application.properties`.

#### 5. Что такое Starter
**Starter** — это специальная зависимость (Maven/Gradle), которая агрегирует в себе набор библиотек, необходимых для определенного функционала (например, для работы с БД или создания Web-приложения).

#### 6. Какие популярные Starter'ы существуют
- `spring-boot-starter-web`: Для создания REST/Web приложений.
- `spring-boot-starter-data-jpa`: Для работы с БД через JPA/Hibernate.
- `spring-boot-starter-security`: Для защиты приложения.
- `spring-boot-starter-test`: Набор библиотек для тестирования.
- `spring-boot-starter-actuator`: Инструменты мониторинга.

#### 7. Что такое Spring Factories
Это файл `META-INF/spring.factories`, используемый Spring Boot (до версии 3.0) для поиска и загрузки классов автоконфигураций и слушателей событий.

#### 8. Что такое AutoConfiguration.imports
Это файл в директории `META-INF/spring/`, который в современных версиях Spring Boot (3.0+) заменил `spring.factories` для регистрации списков автоконфигураций. В нем просто перечисляются полные имена классов конфигураций по одному в строке.
