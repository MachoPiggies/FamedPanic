package com.machopiggies.famedpanic.util;

import java.text.SimpleDateFormat;

public class MiscUtil {

    public static String getPlayerFace(String name) {
        return "https://api.hylaria.com/players/skull/" + name;
    }

    public static String getTimeAndDateFromEpoch(long seconds) {
        return new SimpleDateFormat("MM'/'dd'/'yyyy '('h:mm:ss a') [EST]'").format(seconds * 1000 - 7200000);
    }
    public static double round(double value, int places) {
        if (places >= 0) {
            long factor = (long) Math.pow(10, places);
            value = value * factor;
            long tmp = Math.round(value);
            return (double) tmp / factor;
        } else {
            throw new IllegalArgumentException("decimal places must be 0 or more");
        }
    }

}
