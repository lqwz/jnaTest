package com.liqwer;

/**
 * 息屏
 */
public class CloseScreen {
    public static void main(String[] args) {
        int hwnd = User32.INSTANCE.FindWindowA(null, null);
        User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, 2);
    }
}
