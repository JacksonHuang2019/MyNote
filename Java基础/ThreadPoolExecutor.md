## 前言
平时接触过多线程开发的童鞋应该都或多或少了解过线程池，之前发布的《阿里巴巴 Java 手册》里也有一条：

![](https://camo.githubusercontent.com/4fe5fbafcc85c791f8e173b2c0ff992c0b31bafa/68747470733a2f2f7773322e73696e61696d672e636e2f6c617267652f303036744b665463677931667470786633783165706a33306c6130337330746c2e6a7067)

可见线程池的重要性。

简单来说使用线程池有以下几个目的：
* 线程是稀缺资源，不能频繁的创建。
* 解耦作用；线程的创建于执行完全分开，方便维护。
* 应当将其放入一个池子中，可以给其他任务进行复用。
## 线程池原理
谈到线程池就会想到池化技术，其中最核心的思想就是把宝贵的资源放到一个池子中；每次使用都从里面获取，用完之后又放回池子供其他人使用，有点吃大锅饭的意思。

那在 Java 中又是如何实现的呢？

在 JDK 1.5 之后推出了相关的 api，常见的创建线程池方式有以下几种：
* Executors.newCachedThreadPool()：无限线程池。
* Executors.newFixedThreadPool(nThreads)：创建固定大小的线程池。
* Executors.newSingleThreadExecutor()：创建单个线程的线程池。

其实看这三种方式创建的源码就会发现：

    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }

实际上还是利用 ThreadPoolExecutor 类实现的。

所以我们重点来看下 ThreadPoolExecutor 是怎么玩的。

首先是创建线程的 api：
    
        ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)
        
     这几个核心参数的作用：
     
     * corePoolSize 为线程池的基本大小。
     * maximumPoolSize 为线程池最大线程大小。
     * keepAliveTime 和 unit 则是线程空闲后的存活时间。
     * workQueue 用于存放任务的阻塞队列。
     * handler 当队列和最大线程池都满了之后的饱和策略。
    
 了解了这几个参数再来看看实际的运用。
 
 通常我们都是使用:
  
    threadPool.execute(new Job());
 
 这样的方式来提交一个任务到线程池中，所以核心的逻辑就是 execute() 函数了。
 
 在具体分析之前先了解下线程池中所定义的状态，这些状态都和线程的执行密切相关：
 
![](https://camo.githubusercontent.com/3217f32a3a8f10bf9b6f5076c7e1d44a81ad19a9/68747470733a2f2f7773332e73696e61696d672e636e2f6c617267652f303036744b665463677931667471316b73357179776a33306a6e303369337a612e6a7067)
   