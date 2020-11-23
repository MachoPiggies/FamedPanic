package com.machopiggies.famedpanic.util;

import com.machopiggies.famedpanic.Core;
import org.bukkit.Bukkit;

public class Logger {
    public static void log(String string) {
        Bukkit.getLogger().info("[" + Core.getPlugin().getDescription().getName() + " v" + Core.getPlugin().getDescription().getVersion() + "] " + string);
    }

    public static void warn(String string) {
        Bukkit.getLogger().warning("[" + Core.getPlugin().getDescription().getName() + " v" + Core.getPlugin().getDescription().getVersion() + "] " + string);
    }

    public static void severe(String string) {
        Bukkit.getLogger().severe("[" + Core.getPlugin().getDescription().getName() + " v" + Core.getPlugin().getDescription().getVersion() + "] " + string);
    }

    public static void debug(String string) {
        if (Config.isDebugMode()) {
            Bukkit.getLogger().info("[" + Core.getPlugin().getDescription().getName() + " v" + Core.getPlugin().getDescription().getVersion() + " DEBUG] " + string);
        }
    }
}
