package com.ccic.test;

/**
 * @Author :hzs
 * @Date :Created in 15:33 2019/11/12
 * @Description : 子类
 * Modified By   :
 * @Version ：
 **/
public class StarDiagram  extends  Diagram{

        public StarDiagram(char c)
        {
            super(c);
        }
        @Override
        protected void print(int size)
        {
            for(int i = 0;i<size+2;i++)
            {
                System.out.print(c);
            }
            System.out.println();
        }
        @Override
        protected void printContent(String msg)
        {
            System.out.print("*");
            System.out.print(msg);
            System.out.println("*");
        }
    }


