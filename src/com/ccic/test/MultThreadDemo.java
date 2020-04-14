package com.ccic.test;

/**
 * @Author :hzs
 * @Date :Created in 18:08 2019/11/12
 * @Description :
 * Modified By   :
 * @Version ：
 **/
public class MultThreadDemo {
    public static void main(String[] args) {
        ThreadTest t1 = new ThreadTest("t1");
        ThreadTest t2 = new ThreadTest("t2",200);
        t1.start();
        t2.start();
    }
}
