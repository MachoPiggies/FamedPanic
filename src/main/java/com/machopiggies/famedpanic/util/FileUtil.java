package com.machopiggies.famedpanic.util;

import com.google.gson.JsonObject;
import com.machopiggies.famedpanic.Core;
import org.apache.commons.io.IOUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Map;

public class FileUtil {

    public static File getFolder(String name, File parent) {
        File file = new File(parent, name);
        if (!file.exists()) {
            Logger.debug("Unable to find '" + file.getName() + "', creating a new one!");
            if (!file.mkdirs()) {
                Logger.severe("There was an unknown error whilst trying to create '" + file.getName() + "'. If the file has been created in " + file.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
            } else {
                Logger.debug("Created '" + file.getName() + "' successfully!");
            }
        }
        return file;
    }

    public static File getYamlFile(String name, File parent, Map<String, Object> defaults) {
        if (name.toLowerCase().endsWith(".yml")) {
            File file = new File(parent, name);
            if (!file.exists()) {
                try {
                    Logger.debug("Unable to find '" + file.getName() + "', creating a new one!");
                    if (!file.createNewFile()) {
                        Logger.severe("There was an unknown error whilst trying to create '" + file.getName() + "'. If the file has been created in " + file.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
                    } else {
                        Logger.debug("Created '" + file.getName() + "' successfully!");
                    }
                } catch (IOException e) {
                    Logger.severe("An error occurred whilst trying to create '" + file.getName() + "'. If restarting your server does not fix this, please contact the plugin developer via DM with the below stack trace!");
                    e.printStackTrace();
                }

                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                for (Map.Entry<String, Object> entry : defaults.entrySet()) {
                    yml.set(entry.getKey(), entry.getValue());
                }

                try {
                    yml.save(file);
                } catch (IOException e) {
                    Logger.severe("An error occurred whilst trying to save '" + file.getName() + "' after creation which means it has not loaded properly. If deleting the file and restarting your server does not fix this, please contact the plugin developer via DM!");
                    e.printStackTrace();
                }
            } else {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                for (Map.Entry<String, Object> entry : defaults.entrySet()) {
                    if (yml.get(entry.getKey()) == null) {
                        yml.set(entry.getKey(), entry.getValue());
                    }
                }

                try {
                    yml.save(file);
                } catch (IOException e) {
                    Logger.severe("An error occurred whilst trying to save '" + file.getName() + "' after creation which means it has not loaded properly. If deleting the file and restarting your server does not fix this, please contact the plugin developer via DM!");
                    e.printStackTrace();
                }
            }
            return file;
        } else {
            throw new IllegalStateException("file must be a yml");
        }
    }

    public static File getJsonFile(String name, File parent, InputStream resource) {
        if (name.toLowerCase().endsWith(".json")) {
            File file = new File(parent, name);
            if (!file.exists()) {
                try {
                    Logger.debug("Unable to find '" + file.getName() + "', creating a new one!");
                    if (!file.createNewFile()) {
                        Logger.severe("There was an unknown error whilst trying to create '" + file.getName() + "'. If the file has been created in " + file.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
                    } else {
                        Logger.debug("Created '" + file.getName() + "' successfully!");
                    }

                    OutputStream outputStream = new FileOutputStream(file);
                    IOUtils.copy(resource, outputStream);
                } catch (IOException e) {
                    Logger.severe("An error occurred whilst trying to create '" + file.getName() + "'. If restarting your server does not fix this, please contact the plugin developer via DM with the below stack trace!");
                    e.printStackTrace();
                }
            }
            return file;
        } else {
            throw new IllegalStateException("file must be a json");
        }
    }

    public static File getIndiscriminatoryFile(String name, File parent) {
        File file = new File(parent, name);
        if (!file.exists()) {
            try {
                Logger.debug("Unable to find '" + file.getName() + "', creating a new one!");
                if (!file.createNewFile()) {
                    Logger.severe("There was an unknown error whilst trying to create '" + file.getName() + "'. If the file has been created in " + file.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
                } else {
                    Logger.debug("Created '" + file.getName() + "' successfully!");
                }
            } catch (IOException e) {
                Logger.severe("An error occurred whilst trying to create '" + file.getName() + "'. If restarting your server does not fix this, please contact the plugin developer via DM with the below stack trace!");
                e.printStackTrace();
            }
        }
        return file;
    }
}
