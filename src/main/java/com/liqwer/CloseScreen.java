package com.liqwer;

/**
 * 息屏
 */
public class CloseScreen {
    public static void main(String[] args) {
        int hwnd = User32Test.INSTANCE.FindWindowA(null, null);
        User32Test.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, 2);
    }
}
