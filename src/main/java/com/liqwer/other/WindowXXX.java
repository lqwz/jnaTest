package com.liqwer.other;

import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.util.ArrayList;
import java.util.List;

/***
 * windowXXX
 * @author liqw
 * @date 2019/7/19
 */
public class WindowXXX {

    public static void main(String[] args) {
        // 测试1 获取所有窗口
//        List<WinDef.HWND> allWindow = getAllWindow();
//        for (WinDef.HWND hwnd : allWindow) {
//            System.out.println(getWindowText(hwnd) + ":" + getWindowClass(hwnd) + ":" + getWindowRECT(hwnd));
//        }

        //测试2 获取当前窗口
//        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
//        System.out.println(getWindowText(hwnd) + ":" + getWindowClass(hwnd) + ":" + getWindowRECT(hwnd));

        //测试3 获取所有可见的窗口
        List<DesktopWindow> allWindows = WindowUtils.getAllWindows(true);
        System.out.println(allWindows);



    }

    public static List<WinDef.HWND> getAllWindow() {
        List<WinDef.HWND> hwnds = new ArrayList<>();
        User32.INSTANCE.EnumWindows((hwnd, data) -> {
            hwnds.add(hwnd);
            return true;
        }, new Pointer(0));
        return hwnds;
    }

    /**
     * 获取window 的标题
     *
     * @return
     */
    public static String getWindowText(WinDef.HWND hWnd) {
        int i = User32.INSTANCE.GetWindowTextLength(hWnd);
        char[] windowText = new char[i];
        User32.INSTANCE.GetWindowText(hWnd, windowText, i);
        return new String(windowText);
    }

    /**
     * 获取window 的class
     *
     * @return
     */
    public static String getWindowClass(WinDef.HWND hWnd) {
        int i = 100;
        char[] windowClass = new char[i];
        User32.INSTANCE.GetClassName(hWnd, windowClass, i);
        return new String(windowClass).trim();
    }

    /**
     * 获取window 的矩形
     *
     * @return
     */
    public static WinDef.RECT getWindowRECT(WinDef.HWND hWnd) {
        WinDef.RECT rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hWnd, rect);
        return rect;
    }

}
