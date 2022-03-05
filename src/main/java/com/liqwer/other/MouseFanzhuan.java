package com.liqwer.other;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * 最终功能是开发出来了。鼠标逆时针90度进行移动，可一个把鼠标横着放在胸前了。但是总是不适应。还是正常着操作舒服。
 */
public class MouseFanzhuan implements Runnable{
    private WinUser.HHOOK hhk;

    static WinDef.POINT p = new WinDef.POINT();
    Robot robot = new Robot();
    static int screenHeight;
    static int screenWidth;


    public MouseFanzhuan() throws AWTException {
    }

    public interface LowLevelMouseProc extends WinUser.HOOKPROC {
        WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, MOUSEHOOKSTRUCT lParam) throws AWTException;
    }

    @Structure.FieldOrder({"pt", "hwnd", "dwExtraInfo", "wHitTestCode"})
    public static class MOUSEHOOKSTRUCT extends Structure {
        public static class ByReference extends MOUSEHOOKSTRUCT implements Structure.ByReference {
        }

        public User32.POINT pt;
        public User32.ULONG_PTR dwExtraInfo;
        public WinDef.HWND hwnd;
        public int wHitTestCode;
    }


    // 钩子回调函数
    private LowLevelMouseProc mouseProc = new LowLevelMouseProc() {

        @Override
        public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, MOUSEHOOKSTRUCT lParam) {

            // 输出按键值和按键时间
            if (nCode >= 0) {
                if (wParam.intValue() == 512) {
//                    System.out.println(lParam.pt.x+":"+lParam.pt.y);
//                    int movex = p.x - lParam.pt.x;
//                    int movey = p.y - lParam.pt.y;

                    //通过robot正常移动
//                    if (p.x != lParam.pt.x || p.y != lParam.pt.y) {// 防止递归触发事件
////                      User32.INSTANCE.GetCursorPos(p);// 这个值获取和实际移动的值有细微差异，从而导致上边递归判断失效。
//                        p.x = lParam.pt.x;
//                        p.y = lParam.pt.y;//这里必须先赋值,放到移动前面
//                        robot.mouseMove(lParam.pt.x, lParam.pt.y);// 后移动，因为移动会循环触发事件，导致后赋值有延迟
////                      System.out.println("移动道理:"+p.x+":"+p.y);
//                    }
                    //通过robot 翻转90度 移动
                    if (p.x != lParam.pt.x || p.y != lParam.pt.y) {// 防止递归触发事件
//                      User32.INSTANCE.GetCursorPos(p);// 这个值获取和实际移动的值有细微差异，从而导致上边递归判断失效。
//                        System.out.println("x偏移量"+(lParam.pt.x-p.x));
//                        System.out.println("y偏移量"+(lParam.pt.y-p.y));

                        int x = p.x;// 这里自己之前犯了错，没有用中间变量，导致计算不对
                        int y = p.y;

                        p.x += lParam.pt.y-y;//这里必须先赋值,放到移动前面
                        p.y -=lParam.pt.x-x ;

                        // 进行手动修正，防止移动超出屏幕范围。
                        // 如果是正常的移动，系统会自动修正鼠标保证在屏幕范围中
                        // 但是我们x 和y 轴 偏移量做了对换，所以要手动进行修正，
                        if (p.x < 0) {
                            p.x = 0;
                        }
                        if (p.x > screenWidth) {
                            p.x = screenWidth;
                        }
                        if (p.y < 0) {
                            p.y = 0;
                        }
                        if (p.y > screenHeight) {
                            p.y = screenHeight;
                        }
                        robot.mouseMove(p.x,p.y);// 后移动，因为移动会循环触发事件，导致后赋值有延迟
//                      System.out.println("移动道理:"+p.x+":"+p.y);

                        return new WinDef.LRESULT(1); // 加了这行 拦截事件
                    }
//                  System.out.println("最终的移动"+lParam.pt.x+":"+lParam.pt.y);
//                    return new WinDef.LRESULT(1); // 加了这行 拦截事件
                } else if (wParam.intValue() == 513) {
                    System.out.println("按下左键");
                } else if (wParam.intValue() == 514) {
                    System.out.println("左键释放");
                } else if (wParam.intValue() == 516) {
                    System.out.println("按下右键");
                } else if (wParam.intValue() == 517) {
                    System.out.println("右键释放");
                }
//                return new WinDef.LRESULT(1); // 加了这行 拦截事件
            }
            return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
        }

    };

    public void run() {
        setHookOn();
    }

    // 安装钩子
    public void setHookOn() {
        System.out.println("Hook On!");

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
        System.out.println("Hook Off!");
        User32.INSTANCE.UnhookWindowsHookEx(hhk);
        System.exit(0);
    }

    public static void main(String[] args) throws InterruptedException, AWTException {
        MouseFanzhuan kbhook = new MouseFanzhuan();
        new Thread(kbhook).start();


        User32.INSTANCE.GetCursorPos(p);
        System.out.println("11:"+p.x+":"+p.y);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        screenHeight = (int) d.getHeight();
        screenWidth = (int) d.getWidth();

        // 测试robot移动是否触发事件
//        TimeUnit.SECONDS.sleep(1);
//        new Robot().mouseMove(100, 100);
//        TimeUnit.SECONDS.sleep(1);
//        new Robot().mouseMove(100, 1000);

//      60s 自动结束，防止屏蔽了按键不能手动结束的情况
        TimeUnit.SECONDS.sleep(600000);
        System.exit(0);
    }

    public static void main1(String[] args) throws AWTException {
        Robot robot = new Robot();
        //设置Robot产生一个动作后的休眠时间,否则执行过快
        robot.setAutoDelay(1000);

        //获取屏幕分辨率
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println(d);


        //移动鼠标
        robot.mouseMove(500, 500);
    }
}
