package com.machopiggies.famedpanic.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class PacketManager {

    public enum NMSType {
        CRAFTBUKKIT,
        MINECRAFT
    }

    public static void sendTitle(Player player, String jsonTitleString, String jsonSubtitleString, int fadeInTime, int showTime, int fadeOutTime) {
        try {
            if(jsonTitleString != null) {
                Object titleEnum = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, jsonTitleString);
                Constructor<?> titleConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getClassNMS("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = titleConstructor.newInstance(titleEnum, chatSerializer, fadeInTime, showTime, fadeOutTime);
                sendPacket(player, packet);
            }

            if(jsonSubtitleString != null) {
                Object subtitleEnum = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object ChatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, jsonSubtitleString);
                Constructor<?> subtitleConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getConstructor(Objects.requireNonNull(getClassNMS("PacketPlayOutTitle")).getDeclaredClasses()[0], getClassNMS("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = subtitleConstructor.newInstance(subtitleEnum, ChatSerializer, fadeInTime, showTime, fadeOutTime);
                sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendActionbarMessage(Player player, String message) {
        try {
            if(message != null) {
                Object chatSerializer = Objects.requireNonNull(getClassNMS("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, message);
                Constructor<?> actionbarConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutChat")).getConstructor(getClassNMS("IChatBaseComponent"), byte.class);
                Object packet = actionbarConstructor.newInstance(chatSerializer, (byte) 2);
                sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendActionbarMessageWithICBC(Player player, Object message) {
        try {
            if(message != null) {
                Constructor<?> actionbarConstructor = Objects.requireNonNull(getClassNMS("PacketPlayOutChat")).getConstructor(getClassNMS("IChatBaseComponent"), byte.class);
                Object packet = actionbarConstructor.newInstance(Objects.requireNonNull(getClassNMS("IChatBaseComponent")).cast(message), (byte) 2);
                sendPacket(player, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getClassNMS("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getClassNMS(String methodName) {
        return getClassNMS(methodName, NMSType.MINECRAFT);
    }

    public static Class<?> getClassNMS(String methodName, NMSType type, String... extraArgs) {
        StringBuilder extra = new StringBuilder();
        for (String arg : extraArgs) {
            extra.append(arg).append(".");
        }
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        switch (type) {
            case CRAFTBUKKIT:
                try {
                    return Class.forName("org.bukkit.craftbukkit." + version + "." + extra + methodName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            case MINECRAFT:
                try {
                    return Class.forName("net.minecraft.server." + version + "." + extra + methodName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            default:
                return null;
        }
    }
}
