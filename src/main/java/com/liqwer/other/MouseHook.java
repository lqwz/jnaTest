package com.liqwer.other;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;

import java.util.concurrent.TimeUnit;


public class MouseHook implements Runnable {
   private WinUser.HHOOK hhk;
   public interface LowLevelMouseProc extends WinUser.HOOKPROC {
      LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT lParam);
   }
   @Structure.FieldOrder({"pt","hwnd","dwExtraInfo","wHitTestCode"})
   public static class MOUSEHOOKSTRUCT extends Structure {
      public static class ByReference extends MouseHook.MOUSEHOOKSTRUCT implements
              Structure.ByReference {
      };

      public User32.POINT pt;
      public User32.ULONG_PTR dwExtraInfo;
      public WinDef.HWND hwnd;
      public int wHitTestCode;
   }
   // 钩子回调函数
   private LowLevelMouseProc mouseProc = new LowLevelMouseProc() {

      @Override
      public LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT lParam) {

         // 输出按键值和按键时间
         if (nCode >= 0) {
            if (wParam.intValue() == 512)
               System.out.println(lParam.pt.x+":"+lParam.pt.y);
            if (wParam.intValue() == 513)
               System.out.println("按下左键");
            if (wParam.intValue() == 514)
               System.out.println("左键释放");
            if (wParam.intValue() == 516)
               System.out.println("按下右键");
            if (wParam.intValue() == 517)
               System.out.println("右键释放");

            return new LRESULT(1); // 加了这行 拦截鼠标事件  鼠标会无响应
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

      HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
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

   public static void main(String[] args) throws InterruptedException {
      MouseHook kbhook = new MouseHook();
      new Thread(kbhook).start();


//      60s 自动结束，防止屏蔽了按键不能手动结束的情况
      TimeUnit.SECONDS.sleep(60);
       System.exit(0);
   }
}