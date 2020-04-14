package com.ccic.test;

/**
 * @Author :hzs
 * @Date :Created in 17:55 2019/11/12
 * @Description :
 * Modified By   :
 * @Version ：
 **/
public class ThreadTest extends Thread {

    private final static int DEFAULT_VALUE = 100;
    private int maxValue = 0;
    private String threadName = "";

    public ThreadTest(String threadName) {
        this(threadName,DEFAULT_VALUE);
    }

    public ThreadTest(String threadName, int defaultValue)
    {
        this.maxValue = defaultValue;
        this.threadName = threadName;
    }

    @Override
    public void  run(){

        int i = 0;
        while (maxValue > i ){

            i ++;
            System.out.println("Thread:"+threadName + " : "+i );

        }

    }
}
