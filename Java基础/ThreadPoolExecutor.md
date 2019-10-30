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
   * RUNNING 自然是运行状态，指可以接受任务执行队列里的任务
   * SHUTDOWN 指调用了 shutdown(）方法，不再接受新任务了，但是队列里的任务得执行完毕。
   * STOP 指调用了 shutdownNow()方法，不再接受新任务，同时抛弃阻塞队列里的所有任务并中断所有正在执行任务。
   * TIDYING 所有任务都执行完毕，在调用 shutdown()/shutdownNow() 中都会尝试更新为这个状态。
   * TERMINATED 终止状态，当执行 terminated() 后会更新为这个状态。
   
   用图表示为：
   ![](https://camo.githubusercontent.com/7aaf3ec3d5b3f0a76786b4ca30fac43083f13690/68747470733a2f2f7773342e73696e61696d672e636e2f6c617267652f303036744b665463677931667471326e786c7765356a333073703062613074732e6a7067)
   
   然后看看 execute() 方法是如何处理的：
   
   ![](https://camo.githubusercontent.com/a2477af411a2d9effded4361aa117549f5856af3/68747470733a2f2f7773312e73696e61696d672e636e2f6c617267652f303036744b6654636779316674713238337a6939316a33306b7930386d7767622e6a7067)
   
   1. 获取当前线程池的状态。
   2. 当前线程数量小于 coreSize 时创建一个新的线程运行。
   3. 如果当前线程处于运行状态，并且写入阻塞队列成功。
   4. 双重检查，再次获取线程池状态；如果线程池状态变了（非运行状态）就需要从阻塞队列移除任务，并尝试判断线程是否全部执行完毕。同时执行拒绝策略。
   5. 如果当前线程池为空就新创建一个线程并执行。
   6. 如果在第三步的判断为非运行状态，尝试新建线程，如果失败则执行拒绝策略。
   
   这里借助《聊聊并发》的一张图来描述这个流程：
   
   ![](https://camo.githubusercontent.com/bdcb761059ed70b56b106e0c77e79c86f56c7a05/68747470733a2f2f7773342e73696e61696d672e636e2f6c617267652f303036744b66546367793166747132767a757635726a333064773038357133692e6a7067)

   ### 如何配置线程
   流程聊完了再来看看上文提到了几个核心参数应该如何配置呢？
   
   有一点是肯定的，线程池肯定是不是越大越好。
   
   通常我们是需要根据这批任务执行的性质来确定的。
   * IO 密集型任务：由于线程并不是一直在运行，所以可以尽可能的多配置线程，比如 CPU 个数 * 2
   * CPU 密集型任务（大量复杂的运算）应当分配较少的线程，比如 CPU 个数相当的大小。
  
  当然这些都是经验值，最好的方式还是根据实际情况测试得出最佳配置。