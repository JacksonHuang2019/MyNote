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

默认一般使用非公平锁，它的效率和吞吐量都比公平锁高的多(后面会分析具体原因)。
### 获取锁
通常的使用方式如下:
`   
    
    private ReentrantLock lock = new ReentrantLock();
    public void run() {
        lock.lock();
        try {
            //do bussiness
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }`
    
#### 公平锁获取锁
首先看下获取锁的过程：

    public void lock() {
        sync.lock();
    }

可以看到是使用sync的方法，而这个方法是一个抽象方法，具体是由其子类(FairSync)来实现的，以下是公平锁的实现:

    final void lock() {
            acquire(1);
    }
        
    //AbstractQueuedSynchronizer 中的 acquire()
    public final void acquire(int arg) {
    if (!tryAcquire(arg) &&
        acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
        selfInterrupt();
    }

第一步是尝试获取锁(tryAcquire(arg)),这个也是由其子类实现：
    
    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0){
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
            }
        return false;
        }
    }
 
首先会判断 AQS 中的 state 是否等于0，0表示目前没有其他线程获得锁，当前线程就可以尝试获取锁。 

