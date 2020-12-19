package com.machopiggies.famedpanic.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Objects;

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

    public static double[] serverTPS() {
        try {
            Object server = Objects.requireNonNull(PacketManager.getClassNMS("MinecraftServer")).getMethod("getServer").invoke(null);
            return (double[]) server.getClass().getField("recentTps").get(server);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            File file = Logger.createErrorLog(e, "nested nms error");
            Logger.severe("An error occurred whilst trying to distinguish a packet. Please contact the plugin developer with the following log. [Created error log at " + file.getPath() + "]");
        }
        return null;
    }
}
