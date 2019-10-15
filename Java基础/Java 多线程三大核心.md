## Java 多线程三大核心
### 原子性
Java 的原子性就和数据库事务的原子性差不多，一个操作中要么全部执行成功或者失败。

JMM 只是保证了基本的原子性，但类似于 i++ 之类的操作，看似是原子操作，其实里面涉及到:

    * 获取 i 的值。
    * 自增。
    * 再赋值给 i。
 
 这三步操作，所以想要实现 i++ 这样的原子操作就需要用到 synchronized 或者是 lock 进行加锁处理。
 
 如果是基础类的自增操作可以使用 AtomicInteger 这样的原子类来实现(其本质是利用了 CPU 级别的 的 CAS 指令来完成的)。
 
 其中用的最多的方法就是: incrementAndGet() 以原子的方式自增。 源码如下:
 
 `public final long incrementAndGet() {
          for (;;) {
              long current = get();
              long next = current + 1;
              if (compareAndSet(current, next))
                  return next;
          }
      }`
 
 首先是获得当前的值，然后自增 +1。接着则是最核心的 compareAndSet() 来进行原子更新。
 
 `public final boolean compareAndSet(long expect, long update) {
          return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
      }`
      
 