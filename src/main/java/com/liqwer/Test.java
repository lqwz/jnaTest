package com.liqwer;

public class Test {
    public static void main(String[] args) {
        String img = "C:\\Users\\Public\\Pictures\\Sample Pictures\\八仙花.jpg";

        boolean b = User32.INSTANCE.SystemParametersInfoA(20, 0, img, 3);
        System.out.println(b);
    }
}
