# Spring MVC

1. [Как работает Spring MVC](#1-как-работает-spring-mvc)
2. [Что такое DispatcherServlet](#2-что-такое-dispatcherservlet)
3. [Жизненный цикл HTTP-запроса в Spring MVC](#3-жизненный-цикл-http-запроса-в-spring-mvc)
4. [Что такое HandlerMapping](#4-что-такое-handlermapping)
5. [Что такое HandlerAdapter](#5-что-такое-handleradapter)
6. [Что такое ViewResolver](#6-что-такое-viewresolver)
7. [Разница между @Controller и @RestController](#7-разница-между-controller-и-restcontroller)
8. [Что такое @RequestParam](#8-что-такое-requestparam)
9. [Что такое @PathVariable](#9-что-такое-pathvariable)
10. [Что такое @RequestBody](#10-что-такое-requestbody)
11. [Что такое ResponseEntity](#11-что-такое-responseentity)
12. [Обработка исключений в Spring MVC (@ExceptionHandler, @ControllerAdvice, @RestControllerAdvice)](#12-обработка-исключений-в-spring-mvc-exceptionhandler-controlleradvice-restcontrolleradvice)

---

### Ответы

#### 1. Как работает Spring MVC
Spring MVC реализует паттерн Model-View-Controller и построен вокруг центрального сервлета `DispatcherServlet`, который распределяет входящие запросы между обработчиками.

#### 2. Что такое DispatcherServlet
Это реализация паттерна "Front Controller". Он является центральным узлом, который получает все HTTP-запросы и координирует процесс их обработки, обращаясь к различным компонентам (HandlerMapping, ViewResolver и т.д.).

#### 3. Жизненный цикл HTTP-запроса в Spring MVC
1. Запрос поступает на `DispatcherServlet`.
2. `DispatcherServlet` находит подходящий контроллер через `HandlerMapping`.
3. Запрос передается в контроллер через `HandlerAdapter`.
4. Контроллер обрабатывает запрос и возвращает `ModelAndView` или данные.
5. Если возвращено имя View, `ViewResolver` находит нужный шаблон.
6. Данные объединяются с шаблоном, и результат (HTML или JSON) возвращается клиенту.

#### 4. Что такое HandlerMapping
Это интерфейс, отвечающий за поиск обработчика (обычно метода в контроллере), который соответствует конкретному URL входящего запроса.

#### 5. Что такое HandlerAdapter
Это адаптер, который позволяет `DispatcherServlet` вызывать методы-обработчики контроллеров. Он скрывает детали реализации вызова от основного сервлета.

#### 6. Что такое ViewResolver
Этот компонент преобразует строковые имена представлений (например, `"home"`) в реальные объекты представлений (например, JSP или HTML-файлы).

#### 7. Разница между @Controller и @RestController
- **@Controller**: Используется для классических веб-страниц. Методы обычно возвращают имя View.
- **@RestController**: Специализированная версия для REST API. Комбинирует `@Controller` и `@ResponseBody`. Методы возвращают данные (JSON/XML) напрямую в теле HTTP-ответа.

#### 8. Что такое @RequestParam
Аннотация для получения параметров из строки запроса (query parameters). Пример: `/users?name=Ivan` -> `@RequestParam String name`.

#### 9. Что такое @PathVariable
Аннотация для извлечения переменной прямо из пути URL. Пример: `/users/{id}` -> `@PathVariable Long id`.

#### 10. Что такое @RequestBody
Используется для чтения тела запроса и его автоматической десериализации (через `HttpMessageConverter`) в Java-объект.

#### 11. Что такое ResponseEntity
Это обертка для HTTP-ответа, позволяющая вручную настроить HTTP-статус, заголовки и тело ответа.

#### 12. Обработка исключений в Spring MVC (@ExceptionHandler, @ControllerAdvice, @RestControllerAdvice)
- **@ExceptionHandler**: Метод внутри контроллера для обработки исключений, возникших в этом же контроллере.
- **@ControllerAdvice**: Глобальный перехватчик исключений для всех контроллеров приложения.
- **@RestControllerAdvice**: Версия `@ControllerAdvice` для REST API, где каждый метод автоматически помечен `@ResponseBody`.
