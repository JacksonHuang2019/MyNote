package com.ccic.duilie;

/**
 * @Author :hzs
 * @Date :Created in 9:10 2019/11/8
 * @Description : 自定义了一个类：ArrayQueue
 * Modified By   :
 * @Version ：
 **/
public class ArrayQueue {
    // 队列数量
    private int count;

    // 最终的数据存储
    private Object[] items;

    // 队列满时的阻塞锁
    private  Object full = new Object();
    // 队列空时的阻塞锁
    private  Object empty = new Object();

    // 写入数据时的下标
    private int putIndex;

    // 获取数据时的下标
    private  int getIndex;

    public ArrayQueue(int size) {
        items = new Object[size];
    }
}
