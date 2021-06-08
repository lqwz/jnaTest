package com.liqwer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sun.jna.platform.win32.WinUser.WM_CLOSE;

/**
 *
 */
public class CloseWindowInteval {
    public static void main(String[] args) throws InterruptedException {
        final List<String> windowToClose = new ArrayList<>();
        windowToClose.add("WinRAR@RarReminder"); // 压缩软件弹出的
        windowToClose.add("@SoPY_Status");// 搜狗拼音输入法  关闭之后不影响使用 也还行

        final List<String> tagNew = new ArrayList<>();
        List<String> tagOld = new ArrayList<>();

        WinUser.WNDENUMPROC callback = new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(final WinDef.HWND hWnd, Pointer var2) {
                if (User32.INSTANCE.IsWindowVisible(hWnd)) {
                    WinDef.RECT r = new WinDef.RECT();
                    User32.INSTANCE.GetWindowRect(hWnd, r);
                    if (r.left > -32000) {     // If it's not minimized
                        char[] buffer = new char[1024];
                        User32.INSTANCE.GetWindowText(hWnd, buffer, buffer.length);
                        String title = Native.toString(buffer);

                        User32.INSTANCE.GetClassName((hWnd), buffer, buffer.length);
                        String className = Native.toString(buffer);

                        final String tag = title + "@" + className+ "@" +  hWnd ;

                        tagNew.add(tag);
                    }
                }
                return true;
            }
        };

        for (; ; ) {
            User32.INSTANCE.EnumWindows(callback, new Pointer(0));
            List news = getDiff(tagNew, tagOld);


            if (news.size() > 0) {
                System.out.println("open " + news);

            }
            List removes = getDiff(tagOld, tagNew);
            if (removes.size() > 0) {
                System.out.println("close " + removes);
            }

            tagOld.removeAll(tagOld);
            tagOld.addAll(tagNew);
            tagNew.removeAll(tagNew);



            // 处理需要自动关闭的窗口
            for (Object o : news) {
                String tag = (String) o;
                for (String theTitle : windowToClose) {
                    if (tag.indexOf(theTitle) >= 0) {
                        System.out.println("auto close => " + tag);
                        String hwndHex = tag.split("@")[tag.split("@").length - 1].substring(2);
                        WinDef.HWND hWnd=new WinDef.HWND(new Pointer(Integer.parseInt(hwndHex,16)));
                        User32.INSTANCE.PostMessage(hWnd, WM_CLOSE, new WinDef.WPARAM(), new WinDef.LPARAM());
                    }
                }
            }

            TimeUnit.SECONDS.sleep(1);
        }
    }

    public static List getDiff(List<String> a, List<String> b) {
        List re = new ArrayList();
        re.addAll(a);
        re.removeAll(b);
        return re;
    }
}
