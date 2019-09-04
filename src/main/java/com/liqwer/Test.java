package com.liqwer;

public class Test {
    public static void main(String[] args) throws Exception {
        int hwnd = User32.INSTANCE.FindWindowA(null, null);
        System.setProperty("jna.encoding", "GBK");//设置编码，防止乱码
        User32.INSTANCE.MessageBoxA(hwnd, "看我闪瞎你的狗眼", 0, 0);//调用消息对话框
        int i = 0;
        while (true) {
            //阻塞鼠标键盘的输入
            User32.INSTANCE.BlockInput(true);
            //关闭显示器
            User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, 2);
            Thread.sleep(2000);//间隔2秒
            //打开显示器
            User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, -1);
            Thread.sleep(2000);//间隔2秒
            i++;
            if (i > 10) {
                break;
            }
        }
        //释放鼠标键盘
        User32.INSTANCE.BlockInput(false);
    }
}
