package com.lifeifanzs.memorableintent.Utils;

public  class ThemeUtils {

    public static String getColor(String ThemeColor) {
        switch (ThemeColor) {
            case "WHITE":
                return "#FFFFFF";
            case "BLACK":
                return "#363636";
            case "BLUE":
                return "#c3e8f8";
            case "GREEN":
                return "#d7f2e6";
            case "PINK":
                return "#FEDAFE";
            default:
                return "#DCDCDC";
        }
    }
}
