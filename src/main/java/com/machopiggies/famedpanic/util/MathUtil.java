package com.machopiggies.famedpanic.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtil {

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
