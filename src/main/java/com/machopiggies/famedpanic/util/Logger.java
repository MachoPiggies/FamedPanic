package com.machopiggies.famedpanic.util;

import com.machopiggies.famedpanic.Core;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static File createErrorLog(Exception e, String type) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));

        if (type == null) {
            type = "Unknown";
        }

        type = type.replace(" ", "_");

        String excep = writer.toString();
        File folder = FileUtil.getFolder("errors", Core.getPlugin().getDataFolder());
        File file = FileUtil.getIndiscriminatoryFile(new SimpleDateFormat("'error-'d.MM.yy'-'H.m.s.S'-" + type + "'").format(new Date()) + ".txt", folder);

        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);

            double[] tps = MiscUtil.serverTPS();
            Runtime runtime = Runtime.getRuntime();

            pw.println("---- FamedPanic Error Report (" + type + ") ----");
            pw.println("");
            pw.println("Time: " + new SimpleDateFormat("d/MM/yy h:m a").format(new Date()));
            pw.println("Description: " + (e.getMessage() != null ? e.getMessage() : "Unspecified"));
            pw.println("");
            pw.println(excep);
            pw.println("---- Server Details ----");
            pw.println("");
            pw.println("# SERVER #");
            pw.println("TPS Avg (1 min, 10 min, 15 min): " + (tps != null && tps.length >= 1 ? MiscUtil.round(tps[0], 1) : -1) + ", " + (tps != null && tps.length >= 2 ? MiscUtil.round(tps[1], 1) : -1) + ", " + (tps != null && tps.length >= 3 ? MiscUtil.round(tps[2], 1) : -1));
            pw.println("Memory Used: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1048576) + "MB");
            pw.println("Memory Max: " + (runtime.maxMemory() / 1048576) + "MB");
            pw.println("Memory Total: " + (runtime.totalMemory() / 1048576) + "MB");
            pw.println("");
            pw.println("# API #");
            pw.println("Server Version: " + Bukkit.getServer().getVersion());
            pw.println("Bukkit Version: " + Bukkit.getServer().getBukkitVersion());
            pw.println("API Present: " + (Core.getApi() != null ? "Yes" : "No"));
            pw.println("");
            pw.println("# PLUGINS - Light #");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                pw.println("- " + plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + (plugin.isEnabled() ? "" : " [DISABLED]"));
            }
            pw.println("");
            pw.println("# PLUGINS - In Depth #");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                pw.println("");
                pw.println("Name: " + plugin.getDescription().getName());
                pw.println("Full Name: " + plugin.getDescription().getFullName());
                pw.println("Description: " + plugin.getDescription().getDescription());
                pw.println("Version: " + plugin.getDescription().getVersion());
                pw.println("Prefix: " + plugin.getDescription().getPrefix());
                pw.println("Authors: " + plugin.getDescription().getAuthors());
                pw.println("Dependencies: " + plugin.getDescription().getDepend());
                pw.println("Soft-Dependendies: " +plugin.getDescription().getSoftDepend());
                pw.println("Load Time: " + plugin.getDescription().getLoad().name());
                pw.println("Load Before: " + plugin.getDescription().getLoadBefore());
                pw.println("Website: " + plugin.getDescription().getWebsite());
            }

            pw.flush();
            pw.close();
        } catch (IOException e1) {
            Logger.severe("Another error occurred whilst trying to create the error log, contact the plugin developer with the following stacktraces instead:");
            e.printStackTrace();
            e1.printStackTrace();
        }
        return file;
    }
}
