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