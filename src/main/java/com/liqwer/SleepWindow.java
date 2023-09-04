package com.liqwer;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 当没有键盘输入的时候，执行息屏脚本，
 */
public class SleepWindow {

    static int DEFAULT_COUNT = 20;
    static String batpath = "C:\\Users\\10y\\Desktop/锁屏并息屏.bat";

    private static int count = 0;

    static class KeyBoardHook implements Runnable {

        private WinUser.HHOOK hhk;

        // 钩子回调函数
        private WinUser.LowLevelKeyboardProc keyboardProc = new WinUser.LowLevelKeyboardProc() {
            @Override
            public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT event) {
                // 输出按键值和按键时间
                if (nCode >= 0) {
                    StartCountDown();
//                    System.out.println(event.vkCode);
                    // 按下F7退出
                    if (event.vkCode == 118) {
                        setHookOff();
                        System.exit(0);
                    }
                }

                return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
            }
        };

        public void run() {
            setHookOn();
        }

        // 安装钩子
        public void setHookOn() {
            WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
            hhk = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyboardProc, hMod, 0);

            int result;
            WinUser.MSG msg = new WinUser.MSG();
            while ((result = User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
                if (result == -1) {
                    setHookOff();
                    break;
                } else {
                    User32.INSTANCE.TranslateMessage(msg);
                    User32.INSTANCE.DispatchMessage(msg);
                }
            }
        }

        // 移除钩子并退出
        public void setHookOff() {
            User32.INSTANCE.UnhookWindowsHookEx(hhk);
        }

    }

    public static class MouseHook implements Runnable {
        private WinUser.HHOOK hhk;

        // 钩子回调函数
        private WinUser.LowLevelKeyboardProc mouseProc = new WinUser.LowLevelKeyboardProc() {

            @Override
            public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT event) {
                // 输出按键值和按键时间
                if (nCode >= 0) {
                    StartCountDown();
//                    if (wParam.intValue() == 512) {
//                        System.out.println("鼠标移动");
//                    }
//
//                    if (wParam.intValue() == 513) {
//                        System.out.println("鼠标左键按下");
//                    }
//
//                    if (wParam.intValue() == 514) {
//                        System.out.println("鼠标左键弹起");
//                    }
//
//                    if (wParam.intValue() == 516) {
//                        System.out.println("鼠标右键按下");
//                    }
//
//                    if (wParam.intValue() == 517) {
//                        System.out.println("鼠标右键弹起");
//                    }
                }

                return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
            }

        };

        public void run() {
            setHookOn();
        }

        // 安装钩子
        public void setHookOn() {
            WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
            hhk = User32.INSTANCE.SetWindowsHookEx(User32.WH_MOUSE_LL, mouseProc, hMod, 0);
            int result;
            WinUser.MSG msg = new WinUser.MSG();
            while ((result = User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
                if (result == -1) {
                    setHookOff();
                    break;
                } else {
                    User32.INSTANCE.TranslateMessage(msg);
                    User32.INSTANCE.DispatchMessage(msg);
                }
            }
        }

        // 移除钩子并退出
        public void setHookOff() {
            User32.INSTANCE.UnhookWindowsHookEx(hhk);
            System.exit(0);
        }

        public static void main(String[] args) {
            MouseHook kbhook = new MouseHook();
            new Thread(kbhook).start();

        }
    }

    public static void main(String[] args) {
        KeyBoardHook keyBoardHook = new KeyBoardHook();
        new Thread(keyBoardHook).start();
        MouseHook mouseHook = new MouseHook();
        new Thread(mouseHook).start();

        DEFAULT_COUNT = Integer.parseInt(args[0]);
        batpath = args[1];

        StartCountDown();

    }


    private static void StartCountDown() {
        synchronized (SleepWindow.class) {
            if (SleepWindow.count > 0) {// 如果正在计数，重置计数值 并返回
                SleepWindow.count = DEFAULT_COUNT;
                return;
            }
            // 如果不在计数，设置计数值 开始计数
            SleepWindow.count = DEFAULT_COUNT;
        }
        new Thread() {
            @Override
            public void run() {

                while (SleepWindow.count > 0) {

                    System.out.println("倒计时:" + count);
                    SleepWindow.count = --SleepWindow.count;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("执行脚本");
                String cmd = batpath;
                try {
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec(cmd);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}