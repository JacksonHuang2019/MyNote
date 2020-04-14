package com.ccic;

/**
 * @Author :hzs
 * @Date :Created in 10:43 2019/11/12
 * @Description : 第一次多线程实现
 * Modified By   :
 * @Version ：
 **/
public class FirstThread {
    public static void main(String[] args) {

        new Thread(new Runnable()
        {
            public void run()
            {
                int i = 0;
                while(i<100)
                {
                    System.out.println(Thread.currentThread().getName()+":"+i++);
                }
            }
        }).start();
        int i = 100;
        while(i>0)
        {
            System.out.println(Thread.currentThread().getName()+":"+i--);
        }
    }
}
