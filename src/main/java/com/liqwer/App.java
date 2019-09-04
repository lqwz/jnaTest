package com.liqwer;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        int hwnd = User32.INSTANCE.FindWindowA(null, null);
        User32.INSTANCE.SendMessageA(hwnd, 0x0112, 0xF170, 2);
    }
}
