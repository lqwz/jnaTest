package com.liqwer;

/**
 * 阻塞输入（个人在win10 上测试，没有效果）
 */
public class BlockInput {
    public static void main(String[] args) throws InterruptedException {
        //阻塞鼠标键盘的输入
        User32.INSTANCE.BlockInput(true);
        Thread.sleep(20000);//间隔20秒
        //释放鼠标键盘
        User32.INSTANCE.BlockInput(false);
    }
}
