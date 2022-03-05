package com.liqwer;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class KeyboardHook implements Runnable
{
	private WinUser.HHOOK hhk;
 
	// 钩子回调函数
	private WinUser.LowLevelKeyboardProc keyboardProc = new WinUser.LowLevelKeyboardProc()
	{
		@Override
		public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT event)
		{
			// 输出按键值和按键时间
			if (nCode >= 0)
			{
				System.out.println(event.vkCode);
				// 按下F7退出
				if (event.vkCode == 118)
				{
					KeyboardHook.this.setHookOff();
					System.exit(0);
				}
			}
			
			return User32.INSTANCE.CallNextHookEx(hhk, nCode, wParam, null);
		}
	};
 
	public void run()
	{
		setHookOn();
	}
 
	// 安装钩子
	public void setHookOn()
	{
		WinDef.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		hhk = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, keyboardProc, hMod, 0);
		
		int result;
		WinUser.MSG msg = new WinUser.MSG();
		while ((result = User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0)
		{
			if (result == -1)
			{
				setHookOff();
				break;
			} else
			{
				User32.INSTANCE.TranslateMessage(msg);
				User32.INSTANCE.DispatchMessage(msg);
			}
		}
	}
 
	// 移除钩子并退出
	public void setHookOff()
	{
		User32.INSTANCE.UnhookWindowsHookEx(hhk);
	}

	public static void main(String[] args)
	{
		KeyboardHook kbhook = new KeyboardHook();
		new Thread(kbhook).start();

	}
}