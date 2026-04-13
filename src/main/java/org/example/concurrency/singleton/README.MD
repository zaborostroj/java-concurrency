### Что вообще значит thread-safe Singleton

Singleton — класс, у которого:
* ровно один экземпляр
* глобальная точка доступа
* создание должно быть без race condition
Проблема: если два потока одновременно проверят instance == null, они могут создать два объекта.

### ⚠️ Наивный Singleton (и почему он сломан)

Типичный алгоритм:
* Проверить: instance == null
* Если да → создать объект
* Вернуть instance

В многопоточке:
* Thread A проверил → null
* Thread B проверил → null
* Оба создают объект → Singleton сломан

### ✅ Основные способы сделать Singleton thread-safe

#### 1. Eager initialization (самый простой и безопасный)
Идея: создать instance сразу при загрузке класса.

Почему работает: ClassLoader гарантирует thread-safe инициализацию

Плюсы:
* Очень просто
* 100% thread-safe
* Нет synchronized

Минусы:
* Объект создаётся даже если никогда не используется
* Плохо для heavy объектов

#### 2. Lazy initialization + synchronized

Идея: создавать объект при первом обращении, но защитить synchronized.

Алгоритм:
1. Метод getInstance помечен synchronized
2. Только один поток может войти
3. Первый создаёт instance
4. Остальные получают готовый

Плюсы:
* Lazy loading
* Просто объяснить на интервью

Минусы:
* synchronized на каждом вызове → overhead
* Не scalable

#### 3. Double-Checked Locking (DCL) + volatile (классический senior ответ)

Идея: Сначала проверить без lock, потом с lock.

Алгоритм:
1. Проверить instance == null (без synchronized)
2. Если null → зайти в synchronized блок
3. Проверить ещё раз
4. Если всё ещё null → создать объект

Почему две проверки:
* Чтобы не блокировать каждый вызов
* Lock нужен только при первом создании

Почему нужен volatile:
* Потому что JVM может сделать reordering:
* allocate memory
* assign reference
* call constructor

Без volatile поток может увидеть полуинициализированный объект.

#### 4. Initialization-on-demand holder idiom (лучший практический способ)

Идея: использовать статический inner class.

Алгоритм:

1. Singleton не создаётся сразу
2. Внутренний static класс содержит instance
3. JVM загружает этот класс только при первом обращении

Почему thread-safe:

ClassLoader гарантирует atomic initialization

Плюсы:

* Lazy
* Без synchronized
* Без volatile
* Очень elegant

#### 5. Enum Singleton (любимый у Effective Java)

Идея: singleton как enum с одним элементом.

Плюсы:

* Thread-safe by spec
* Защищён от reflection
* Защищён от serialization

Минус:

* Не lazy (по сути eager)
* Не всегда подходит архитектурно

#### Важные нюансы

❓ Почему DCL был сломан до Java 5

* Потому что JMM не гарантировал visibility и ordering до JSR-133. С Java 5 volatile исправили.

❓ Почему synchronized на `getInstance` плохо

* Блокирует даже после создания instance
* Убивает scalability

❓ Как сломать Singleton

* Reflection
* Serialization / deserialization
* ClassLoader duplication

❓Как реализован singleton в Spring

* В Spring singleton реализован не через static instance, а через IoC контейнер. Контейнер создаёт один экземпляр бина и хранит его во внутреннем кеше. Когда другой компонент запрашивает бин, контейнер возвращает уже созданный экземпляр. Singleton действует в рамках одного ApplicationContext, поэтому при нескольких контекстах может существовать несколько экземпляров.

| Способ         | Lazy | Perf | Complexity |
|----------------|------|------|------------|
| Eager          | -    | +++  | +          |
| Synchronized   | +    | +    | ++         |
| DCL + volatile | +    | +++  | +++        |
| Holder class   | +    | ++++ | ++         |
| Enum           | -    | ++++ | +          |

---

[Назад к оглавлению](../../../../../../../README.MD)
