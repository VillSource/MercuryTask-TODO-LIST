package com.example.demo.flag;


import android.view.MenuItem;

public class SheetFlagSelector {
    public static MenuItem FILTER = null;
    public static int DEFAULT_COLOR=0;
    private static int color[] ;
    private static String name[] = new String[]{
            "Default","School","Work"
    };

    public static void setColor(int []c){color = c;}

    public static int getC(long n){
        return color[(int)n];
    }
    public static String getName(long i){
        return name[(int)i];
    }

}
