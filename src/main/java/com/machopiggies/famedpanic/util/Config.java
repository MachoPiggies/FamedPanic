package com.machopiggies.famedpanic.util;

import com.machopiggies.famedpanic.Core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private static YamlConfiguration authConfig;

    public static FileConfiguration getConfig() {
        return Core.getPlugin().getConfig();
    }

    public static YamlConfiguration getMessagesConfig() {
        return authConfig;
    }

    public static void intialize() {
        Core.getPlugin().saveDefaultConfig();
        getConfig().options().copyDefaults(true);

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
            authConfig.set("discord.useEmbed", true);
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
    }

    public static boolean isDebugMode() {
        return true;
    }
}
