# Скоупы бинов

1. [Какие существуют scope бинов (singleton, prototype, request, session, application, websocket)](#1-какие-существуют-scope-бинов-singleton-prototype-request-session-application-websocket)
2. [Как создается Prototype Bean](#2-как-создается-prototype-bean)
3. [Проблема внедрения Prototype в Singleton](#3-проблема-внедрения-prototype-в-singleton)
4. [Что такое @Lookup](#4-что-такое-lookup)
5. [Что такое @Lazy](#5-что-такое-lazy)

---

### Ответы

#### 1. Какие существуют scope бинов (singleton, prototype, request, session, application, websocket)
- **singleton**: (по умолчанию) один экземпляр на IoC-контейнер.
- **prototype**: новый экземпляр при каждом запросе бина.
- **request**: один экземпляр на один HTTP-запрос (только в Web-контексте).
- **session**: один экземпляр на одну HTTP-сессию (только в Web-контексте).
- **application**: один экземпляр на жизненный цикл `ServletContext`.
- **websocket**: один экземпляр на жизненный цикл WebSocket.

#### 2. Как создается Prototype Bean
Бин с областью видимости `prototype` создается каждый раз, когда он запрашивается из контейнера (через `getBean()`) или когда он внедряется в другой бин.

#### 3. Проблема внедрения Prototype в Singleton
Если `prototype`-бин внедряется в `singleton`-бин обычным способом (через поле или конструктор), то он внедрится **только один раз** при создании синглтона. В дальнейшем синглтон будет использовать один и тот же экземпляр прототипа.

#### 4. Что такое @Lookup
Аннотация, решающая проблему внедрения Prototype в Singleton. Она заставляет Spring переопределять метод и возвращать новый экземпляр бина при каждом вызове.
```java
@Component
public class SingletonBean {
    @Lookup
    public PrototypeBean getPrototypeBean() {
        return null; // Spring переопределит этот метод
    }
}
```

#### 5. Что такое @Lazy
Указывает, что бин должен быть инициализирован не при запуске приложения, а при первом обращении к нему. Может использоваться для ускорения старта или решения проблем циклических зависимостей.
