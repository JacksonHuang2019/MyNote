## ReentrantLock 实现原理
使用 synchronized 来做同步处理时，锁的获取和释放都是隐式的，实现的原理是通过编译后加上不同的机器指令来实现。

而 ReentrantLock 就是一个普通的类，它是基于 AQS(AbstractQueuedSynchronizer)来实现的。

是一个重入锁：一个线程获得了锁之后仍然可以反复的加锁，不会出现自己阻塞自己的情况。

AQS 是 Java 并发包里实现锁、同步的一个重要的基础框架。
### 锁类型
ReentrantLock 分为公平锁和非公平锁，可以通过构造方法来指定具体类型：
`  

    //默认非公平锁
     public ReentrantLock() {
         sync = new NonfairSync();
     }
     
     //公平锁
     public ReentrantLock(boolean fair) {
         sync = fair ? new FairSync() : new NonfairSync();
     }`