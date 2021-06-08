package com.liqwer;

import java.util.Timer;
import java.util.TimerTask;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.StdCallLibrary;

public class EditDesktop {

    public static void main(String[] args) {
        String wallpaper = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "Wallpaper");
        System.out.println(wallpaper);
        //
//        String img = "C:\\Users\\lqw\\AppData\\Roaming\\Microsoft\\Windows\\Themes\\TranscodedWallpaper.jpg";
        String img = "E:/无标题.bmp";
        change(img);
//        MyUser32.INSTANCE.SystemParametersInfoA(20, 0, img, 0);
    }
    public static void main1(String[] args) {

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            private int count = 0;
            private int tot = 0;
            String img = "E:/AAAprojectDoc/img/";

            @Override
            public void run() {
                this.count++;
                this.tot++;
                img = "E:/AAAprojectDoc/img/" + count + ".jpg";

                change(img);

                if (count == 11) count = 0;
                if (tot == 12) {
                    System.out.println("定时器停止了,img=" + img);
                    timer.cancel();// 停止定时器
                }
            }
        };
        timer.schedule(task, 0, 3000);// 1秒一次       
    }


    private interface MyUser32 extends StdCallLibrary {

        MyUser32 INSTANCE = (MyUser32) Native.loadLibrary("user32", MyUser32.class);

        boolean SystemParametersInfoA(int uiAction, int uiParam, String fnm, int fWinIni);

    }


    public static void change(String img) {

        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "Wallpaper", img);
        //WallpaperStyle = 10 (Fill), 6 (Fit), 2 (Stretch), 0 (Tile), 0 (Center)
        //For windows XP, change to 0
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "WallpaperStyle", "10"); //fill
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "TileWallpaper", "0");   // no tiling

        // refresh the desktop using User32.SystemParametersInfo(), so avoiding an OS reboot
        //        刷新桌面，并将更改通知给其他程序。

        int SPI_SETDESKWALLPAPER = 0x14;
        int SPIF_UPDATEINIFILE = 0x01;
        int SPIF_SENDWININICHANGE = 0x02;

        // User32.System
        boolean result = MyUser32.INSTANCE.SystemParametersInfoA(SPI_SETDESKWALLPAPER, 0,
                img, SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE);
        System.out.println(result);
    }


}