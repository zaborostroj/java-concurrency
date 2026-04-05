package org.example.concurrency.lru_cache;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LruCache<K, V> {
    private final ConcurrentHashMap<K, Node<K, V>> cache;
    Node<K, V> head;
    Node<K, V> tail;

    private final ReentrantLock mutex;
    private final int capacity;
    private int size;

    public LruCache(int capacity) {
        this.cache = new ConcurrentHashMap<>(capacity);
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;

        this.mutex = new ReentrantLock();
        this.capacity = capacity;
        this.size = 0;
    }

    public V get(K key) {
        try {
            mutex.lockInterruptibly();

            var node = cache.get(key);
            if (node == null) {
                return null;
            }
            if (node.prev != head) {
                cutNode(node);
                insertAfterHead(node);
            }

            return node.getValue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            mutex.unlock();
        }
    }

    public void put(K key, V value) {
        try {
            mutex.lockInterruptibly();
            if (cache.containsKey(key)) {
                Node<K, V> nodeForUpdate = cache.get(key);
                nodeForUpdate.setValue(value);

                if (nodeForUpdate.prev == head) {
                    return;
                }
                cutNode(nodeForUpdate);
                insertAfterHead(nodeForUpdate);
            } else {
                Node<K, V> newNode = new Node<>(key, value);
                cache.put(key, newNode);

                if (size + 1 > capacity) {
                    evictLRU();
                } else {
                    size++;
                }

                insertAfterHead(newNode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.unlock();
        }
    }

    public void remove(K key) {
        try {
            mutex.lockInterruptibly();

            if (!cache.containsKey(key)) {
                return;
            }

            var nodeForRemove = cache.get(key);
            cache.remove(key);

            cutNode(nodeForRemove);
            this.size--;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.unlock();
        }
    }

    private void insertAfterHead(Node<K, V> nodeToMove) {
        (head.next).prev = nodeToMove;
        nodeToMove.next = head.next;
        nodeToMove.prev = head;
        head.next = nodeToMove;
    }

    private void cutNode(Node<K, V> nodeForCut) {
        (nodeForCut.next).prev = nodeForCut.prev;
        (nodeForCut.prev).next = nodeForCut.next;
    }

    private void evictLRU() {
        var lruNode = tail.prev;
        cutNode(lruNode);
        cache.remove(lruNode.getKey());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @EqualsAndHashCode
    static class Node<KEY, VALUE> {
        private Node<KEY, VALUE> prev;
        private Node<KEY, VALUE> next;
        private KEY key;
        private VALUE value;

        public Node(KEY key, VALUE value) {
            this.key = key;
            this.value = value;
        }
    }
}