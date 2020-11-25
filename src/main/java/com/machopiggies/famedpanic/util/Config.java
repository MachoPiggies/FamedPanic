package com.machopiggies.famedpanic.util;

import com.google.gson.Gson;
import com.machopiggies.famedpanic.Core;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static FileConfiguration getConfig() {
        return Core.getPlugin().getConfig();
    }

    public static YamlConfiguration getMessagesConfig() {
        return authConfig;
    }

    private static YamlConfiguration authConfig;
    public static Auth auth = null;
    private static File dJson = null;
    private static boolean bungee = false;
    private static boolean safemode = false;

    public static boolean isSafemode() {
        return safemode;
    }

    public static void setSafemode(boolean safemode) {
        Config.safemode = safemode;
    }

    public static boolean isBungee() {
        return bungee;
    }

    public static File getdJson() {
        return dJson;
    }

    public static void intialize() {
        Core.getPlugin().saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        Message.buildConfig();
        bungee = Config.getConfig().getBoolean("settings.bungee", false);
        File auth = new File(Core.getPlugin().getDataFolder(), "auth.yml");
        if (!auth.exists()) {
            try {
                Logger.debug("Unable to find '" + auth.getName() + "', creating a new one!");
                if (!auth.createNewFile()) {
                    Logger.severe("There was an unknown error whilst trying to create '" + auth.getName() + "'. If the file has been created in " + auth.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
                } else {
                    Logger.debug("Created '" + auth.getName() + "' successfully!");
                }
            } catch (IOException e) {
                Logger.severe("An error occurred whilst trying to create '" + auth.getName() + "'. If restarting your server does not fix this, please contact the plugin developer via DM with the below stack trace!");
                e.printStackTrace();
            }
            authConfig = YamlConfiguration.loadConfiguration(auth);
            authConfig.set("discord.enabled", false);
            authConfig.set("discord.webhookURL", "");
            authConfig.set("discord.embed.useEmbed", true);
            authConfig.set("discord.embed.message", "{%PLAYER%} has activated panic mode{%SERVER%}! Please see to this immediately.\n\n\n");
            authConfig.set("discord.embed.colorHEX", 13828351);
            authConfig.set("discord.embed.image", "");
            authConfig.set("discord.embed.website", "");
            authConfig.set("slack.enabled", false);
            authConfig.set("slack.webhookURL", "");
            authConfig.set("slack.useBlock", true);

            try {
                authConfig.save(auth);
            } catch (IOException e) {
                Logger.severe("An error occurred whilst trying to save '" + auth.getName() + "' after creation which means it has not loaded properly. If deleting the file and restarting your server does not fix this, please contact the plugin developer via DM!");
                e.printStackTrace();
            }
        } else {
            authConfig = YamlConfiguration.loadConfiguration(auth);
        }

        Config.auth = new Auth(
                authConfig.getString("discord.webhookURL", ""),
                authConfig.getString("slack.webhookURL", "")
        );

        File rfile = new File(Core.getPlugin().getDataFolder(), "embeds");
        if (!rfile.exists()) {
            Logger.debug("Unable to find '" + rfile.getName() + "', creating a new one!");
            if (!rfile.mkdirs()) {
                Logger.severe("There was an unknown error whilst trying to create '" + rfile.getName() + "'. If the file has been created in " + rfile.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
            } else {
                Logger.debug("Created '" + rfile.getName() + "' successfully!");
            }
        }

        File dJson = new File(rfile, "discord.json");
        if (!dJson.exists()) {
            Logger.debug("Unable to find '" + dJson.getName() + "', creating a new one!");
            try {
                if (!dJson.createNewFile()) {
                    Logger.severe("There was an unknown error whilst trying to create '" + dJson.getName() + "'. If the file has been created in " + dJson.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
                } else {
                    Logger.debug("Created '" + dJson.getName() + "' successfully!");
                }

                OutputStream outputStream = new FileOutputStream(dJson);
                IOUtils.copy(Core.getPlugin().getResource("discordEmbed.json"), outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Config.dJson = dJson;
    }

    public static boolean isDebugMode() {
        return true;
    }

    public static class Auth {
        public String discord;
        public String slack;

        public Auth(String discord, String slack) {
            this.discord = discord;
            this.slack = slack;
        }
    }
}
