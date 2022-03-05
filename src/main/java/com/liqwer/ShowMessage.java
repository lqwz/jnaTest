package com.liqwer;

public class ShowMessage {
    public static void main(String[] args) {
        int hwnd = User32Test.INSTANCE.FindWindowA(null, null);
        //设置编码，防止乱码
        System.setProperty("jna.encoding", "GBK");
        //调用消息对话框
        int i = User32Test.INSTANCE.MessageBoxA(hwnd, "你好吗", 0, 3);
        System.out.println(i);
    }
}
