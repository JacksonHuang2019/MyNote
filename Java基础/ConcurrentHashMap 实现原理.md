## ConcurrentHashMap 实现原理
由于 HashMap 是一个线程不安全的容器，主要体现在容量大于总量*负载因子发生扩容时会出现环形链表从而导致死循环。

因此需要支持线程安全的并发容器 ConcurrentHashMap 。
### JDK1.7 实现
#### 数据结构
如图所示，是由 Segment 数组、HashEntry 数组组成，和 HashMap 一样，仍然是数组加链表组成。

ConcurrentHashMap 采用了分段锁技术，其中 Segment 继承于 ReentrantLock。不会像 HashTable 那样不管是 put 还是 get 操作都需要做同步处理，理论上 ConcurrentHashMap 支持 CurrencyLevel (Segment 数组数量)的线程并发。每当一个线程占用锁访问一个 Segment 时，不会影响到其他的 Segment。

##### get 方法
ConcurrentHashMap 的 get 方法是非常高效的，因为整个过程都不需要加锁。

只需要将 Key 通过 Hash 之后定位到具体的 Segment ，再通过一次 Hash 定位到具体的元素上。由于 HashEntry 中的 value 属性是用 volatile 关键词修饰的，保证了内存可见性，所以每次获取时都是最新值([volatile 相关知识点](https://github.com/JacksonHuang2019/MyNote/blob/master/Java%E5%9F%BA%E7%A1%80/Java%20%E5%A4%9A%E7%BA%BF%E7%A8%8B%E4%B8%89%E5%A4%A7%E6%A0%B8%E5%BF%83.md))。
##### put 方法
内部 HashEntry 类 ：

    static final class HashEntry<K,V> {
        final int hash;
        final K key;
        volatile V value;
        volatile HashEntry<K,V> next;

        HashEntry(int hash, K key, V value, HashEntry<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }