## synchronized 关键字原理
众所周知 synchronized 关键字是解决并发问题常用解决方案，有以下三种使用方式:<br>
  * 同步普通方法，锁的是当前对象。
  * 同步静态方法，锁的是当前 Class 对象。
  * 同步块，锁的是 () 中的对象。
  
 实现原理： JVM 是通过进入、退出对象监视器( Monitor )来实现对方法、同步块的同步的。
 
 具体实现是在编译之后在同步方法调用前加入一个 monitor.enter 指令，在退出方法和异常处插入 monitor.exit 的指令。
 
 其本质就是对一个对象监视器( Monitor )进行获取，而这个获取过程具有排他性从而达到了同一时刻只能一个线程访问的目的。
 
 而对于没有获取到锁的线程将会阻塞到方法入口处，直到获取锁的线程 monitor.exit 之后才能尝试继续获取锁。
 
 流程图如下:
 ![](https://camo.githubusercontent.com/2755b62baffab9f16d90a8d2d101b2fa18b0873b/68747470733a2f2f7773322e73696e61696d672e636e2f6c617267652f303036744e6337396c7931666e3237666b6c30376a6a333165383068796e306e2e6a7067)
  
  通过一段代码来演示:
  
  
  `    public static void main(String[] args) {
           synchronized (Synchronize.class){
               System.out.println("Synchronize");
           }
       }`
  
  使用 javap -c Synchronize 可以查看编译之后的具体信息。
  
  `public class com.crossoverjie.synchronize.Synchronize {
     public com.crossoverjie.synchronize.Synchronize();
       Code:
          0: aload_0
          1: invokespecial #1                  // Method java/lang/Object."<init>":()V
          4: return
   
     public static void main(java.lang.String[]);
       Code:
          0: ldc           #2                  // class com/crossoverjie/synchronize/Synchronize
          2: dup
          3: astore_1
          **4: monitorenter**
          5: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
          8: ldc           #4                  // String Synchronize
         10: invokevirtual #5                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         13: aload_1
         **14: monitorexit**
         15: goto          23
         18: astore_2
         19: aload_1
         20: monitorexit
         21: aload_2
         22: athrow
         23: return
       Exception table:
          from    to  target type
              5    15    18   any
             18    21    18   any
   }`
   
 可以看到在同步块的入口和出口分别有 monitorenter,monitorexit 指令。

 ### 锁优化
 synchronized 很多都称之为重量锁，JDK1.6 中对 synchronized 进行了各种优化，为了能减少获取和释放锁带来的消耗引入了偏向锁和轻量锁。
 #### 轻量锁
 当代码进入同步块时，如果同步对象为无锁状态时，当前线程会在栈帧中创建一个锁记录(Lock Record)区域，同时将锁对象的对象头中 Mark Word 拷贝到锁记录中，再尝试使用 CAS 将 Mark Word 更新为指向锁记录的指针。
 
 如果更新<b>成功</b>，当前线程就获得了锁。
 
 如果更新<b>失败</b> JVM 会先检查锁对象的 Mark Word 是否指向当前线程的锁记录。
 
 如果是则说明当前线程拥有锁对象的锁，可以直接进入同步块。
 
 不是则说明有其他线程抢占了锁，如果存在多个线程同时竞争一把锁，<b>轻量锁就会膨胀为重量锁</b>。

