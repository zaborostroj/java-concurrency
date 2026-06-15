# Что такое bounded queue?
Bounded queue (ограниченная очередь) — это структура данных, реализующая принцип FIFO (First In, First Out), но с фиксированным максимальным размером (ёмкостью). В отличие от unbounded queue (неограниченной очереди, например, LinkedList или стандартная Queue в Java без лимита), в bounded queue:

* Производители (producers) не могут добавить элемент, если очередь полная (размер == capacity). В типичной блокирующей реализации они блокируется (ждут), пока не освободится место.
* Потребители (consumers) не могут извлечь элемент, если очередь пустая (размер == 0). Они тоже блокируется, пока не появится элемент.
* Это предотвращает бесконтрольный рост памяти (OOM) в многопоточных сценариях, таких как producer-consumer проблема.
* Операции: produce(item) (добавление с блокировкой при полноте), consume() (извлечение с блокировкой при пустоте), иногда size(), isEmpty(), isFull() для проверки.
* Bounded queue часто используется в многопоточных приложениях: буферизация сообщений, task queues в пулах потоков, сетевые протоколы и т.д.

# Варианты реализации
Реализации делятся на блокирующие (thread-safe с ожиданием) и неблокирующие (с немедленным возвратом ошибки). Основные подходы для блокирующих (самые практичные для задач):

## Циклический буфер (circular buffer) на массиве + wait/notify (synchronized):
* Фиксированный массив размера capacity.
* Два указателя: head (начало для consume), tail (конец для produce).
* При produce: если не full, добавить по tail, сдвинуть tail (с зацикливанием modulo capacity), notifyAll() для ожидающих потребителей.
* При consume: если не empty, взять по head, сдвинуть head, notifyAll() для производителей.
* Синхронизация: весь класс synchronized или методы.
* Плюсы: просто, эффективно по памяти. 
* Минусы: notifyAll() будит всех (thundering herd).

## ReentrantLock + Condition:
* Аналогично circular buffer.
* Один ReentrantLock, две Condition: notEmpty (для потребителей), notFull (для производителей).
* Produce: lock, while(full) await(notFull), добавить, signal(notEmpty), unlock.
* Consume: lock, while(empty) await(notEmpty), извлечь, signal(notFull), unlock.
* Плюсы: точное пробуждение нужных потоков (fairness опционально), современный подход. 
* Минусы: чуть сложнее synchronized.

## Семафоры (Semaphore):
* Три семафора: mutex (binary=1 для взаимного исключения), items (count=0, инкремент при produce), slots (count=capacity, декремент при produce).
* Produce: acquire(slots), acquire(mutex), добавить в очередь (не-circular, например Deque), release(mutex), release(items).
* Consume: acquire(items), acquire(mutex), извлечь, release(mutex), release(slots).
* Очередь внутри: ArrayDeque или аналог.
* Плюсы: классика для producer-consumer, легко понять. 
* Минусы: отдельная блокировка для доступа к очереди.

## Lock-free (без блокировок, на атомарных операциях):
* CAS (Compare-And-Swap) на атомарных ссылках/массивах для head/tail.
* ABA-проблема решается тегами или поколениями.
* Produce/Consume: spin-loop с CAS до успеха, или возвращает false при неудаче (неблокирующий вариант).
* Плюсы: высокая производительность в SMP. 
* Минусы: очень сложно реализовать правильно, спиннинг жрёт CPU.

## Готовые реализации (для изучения/использования):
* ArrayBlockingQueue (Java stdlib): на массиве, ReentrantLock+Condition, фиксированный размер при создании.
* LinkedBlockingQueue с capacity (но по умолчанию unbounded).
* Disruptor (high-perf ring buffer с seq-номерами).

---

[Назад к оглавлению](../../../../../../../README.MD)
