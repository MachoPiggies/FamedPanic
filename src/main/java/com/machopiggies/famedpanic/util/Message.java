package com.machopiggies.famedpanic.util;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.commands.CommandManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Message {

    private static boolean initial = true;

    private static FileConfiguration yml;
    private static FileConfiguration yml1;

    public static Messages msgs;
    public static ServerInfo serverInfo;

    public static void buildConfig() {

        File rfile = new File(Core.getPlugin().getDataFolder(), "design");
        if (!rfile.exists()) {
            Logger.debug("Unable to find '" + rfile.getName() + "', creating a new one!");
            if (!rfile.mkdirs()) {
                Logger.severe("There was an unknown error whilst trying to create '" + rfile.getName() + "'. If the file has been created in " + rfile.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
            } else {
                Logger.debug("Created '" + rfile.getName() + "' successfully!");
            }
        }

        Messages md = new Messages();
        File messages = new File(rfile, "messages.yml");
        if (!messages.exists()) {
            try {
                Logger.debug("Unable to find '" + messages.getName() + "', creating a new one!");
                if (!messages.createNewFile()) {
                    Logger.severe("There was an unknown error whilst trying to create '" + messages.getName() + "'. If the file has been created in " + messages.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
                } else {
                    Logger.debug("Created '" + messages.getName() + "' successfully!");
                }
            } catch (IOException e) {
                Logger.severe("An error occurred whilst trying to create '" + messages.getName() + "'. If restarting your server does not fix this, please contact the plugin developer via DM with the below stack trace!");
                e.printStackTrace();
            }
            yml = YamlConfiguration.loadConfiguration(messages);

            yml.set("general.prefix", md.prefix);
            yml.set("general.permission-denied", md.noPermission);
            yml.set("general.safemode.toggle-on", md.setSafemodeOn);
            yml.set("general.safemode.toggle-off", md.setSafemodeOff);
            yml.set("general.safemode.on", md.safemodeOn);
            yml.set("general.safemode.off", md.safemodeOff);
            yml.set("alerts.announce", md.announce);
            yml.set("alerts.enabled", md.enabled);
            yml.set("alerts.disabled", md.disabled);
            yml.set("alerts.staff-disabled", md.staffDisabled);
            yml.set("alerts.forced-out", md.forcedOut);
            yml.set("protections.interact.no-open", md.noOpen);
            yml.set("protections.interact.no-drop", md.noDrop);
            yml.set("protections.interact.misc", md.noMisc);
            yml.set("protections.interact.worldinteract.no-block-break", md.noBlockBreak);
            yml.set("protections.interact.worldinteract.no-block-place", md.noBlockPlace);
            yml.set("protections.interact.worldinteract.no-vehicle-use", md.noVehicleUse);
            yml.set("protections.combat.no-damager", md.noDamager);
            yml.set("protections.combat.no-damagee", md.noDamagee);
            yml.set("protections.no-command", md.noCommands);
            yml.set("protections.no-chat", md.noChat);

            try {
                yml.save(messages);
            } catch (IOException e) {
                Logger.severe("An error occurred whilst trying to save '" + messages.getName() + "' after creation which means it has not loaded properly. If deleting the file and restarting your server does not fix this, please contact the plugin developer via DM!");
                e.printStackTrace();
            }
        } else {
            yml = YamlConfiguration.loadConfiguration(messages);
        }

        msgs = new Messages(
                yml.getString("general.prefix", md.prefix),
                yml.getString("general.permission-denied", md.noPermission),
                yml.getString("general.safemode.toggle-on", md.setSafemodeOn),
                yml.getString("general.safemode.toggle-off", md.setSafemodeOff),
                yml.getString("general.safemode.on", md.safemodeOn),
                yml.getString("general.safemode.off", md.safemodeOff),
                yml.getString("alerts.announce", md.announce),
                yml.getString("alerts.enabled", md.enabled),
                yml.getString("alerts.disabled", md.disabled),
                yml.getString("alerts.staff-disabled", md.staffDisabled),
                yml.getString("alerts.forced-out", md.forcedOut),
                yml.getString("protections.interact.no-open", md.noOpen),
                yml.getString("protections.interact.no-drop", md.noDrop),
                yml.getString("protections.interact.misc", md.noMisc),
                yml.getString("protections.interact.worldinteract.no-block-break", md.noBlockBreak),
                yml.getString("protections.interact.worldinteract.no-block-place", md.noBlockPlace),
                yml.getString("protections.interact.worldinteract.no-vehicle-use", md.noVehicleUse),
                yml.getString("protections.combat.no-damager", md.noDamager),
                yml.getString("protections.combat.no-damagee", md.noDamagee),
                yml.getString("protections.no-command", md.noCommands),
                yml.getString("protections.no-chat", md.noChat)
        );

        if (!initial) {
            for (PluginCommand key : CommandManager.getExecutors().keySet()) {
                key.setPermissionMessage(format(Message.msgs.noPermission));
            }
        }

        if (initial) {
            initial = false;
        }

        File serverData = new File(rfile, "server-data.yml");
        if (!serverData.exists()) {
            try {
                Logger.debug("Unable to find '" + serverData.getName() + "', creating a new one!");
                if (!serverData.createNewFile()) {
                    Logger.severe("There was an unknown error whilst trying to create '" + serverData.getName() + "'. If the file has been created in " + serverData.getParentFile().getPath() + ", you can ignore this. If it hasn't and restarting your server does not fix this, please contact the plugin developer via DM.");
                } else {
                    Logger.debug("Created '" + serverData.getName() + "' successfully!");
                }
            } catch (IOException e) {
                Logger.severe("An error occurred whilst trying to create '" + serverData.getName() + "'. If restarting your server does not fix this, please contact the plugin developer via DM with the below stack trace!");
                e.printStackTrace();
            }
            yml1 = YamlConfiguration.loadConfiguration(serverData);
            yml1.set("server.name", "Minecraft Server");
            yml1.set("server.image", "https://static.planetminecraft.com/files/resource_media/screenshot/1606/photo9868183_lrg.jpg");

            try {
                yml1.save(messages);
            } catch (IOException e) {
                Logger.severe("An error occurred whilst trying to save '" + serverData.getName() + "' after creation which means it has not loaded properly. If deleting the file and restarting your server does not fix this, please contact the plugin developer via DM!");
                e.printStackTrace();
            }
        } else {
            yml1 = YamlConfiguration.loadConfiguration(serverData);
        }

        serverInfo = new ServerInfo(
                yml1.getString("server.name", "Minecraft Server"),
                yml1.getString("server.image", "https://static.planetminecraft.com/files/resource_media/screenshot/1606/photo9868183_lrg.jpg"),
                yml1.getString("bungee.transferCommand", "/server")
        );
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(format(message));
    }

    public static String format(String message) {
        return format(message, null);
    }

    public static String format(String message, Map<String, String> placeholders) {
        message = message.replace("{%PREFIX%}", msgs.prefix);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static FileConfiguration getYml() {
        return yml;
    }

    public static class ServerInfo {
        public String name = "Minecraft Server";
        public String image = "https://static.planetminecraft.com/files/resource_media/screenshot/1606/photo9868183_lrg.jpg";
        public String transferCommand = "/server";

        private boolean readonly = false;

        public ServerInfo(String name, String image, String transferCommand) {
            this.name = name;
            this.image = image;
            this.transferCommand = transferCommand;
        }

        public ServerInfo() {
            readonly = true;
        }
    }

    public static class Messages {
        public String prefix = "&f[&cPanic&f] &r";
        public String noPermission = "{%PREFIX%}&cYou do not have permission to do this!";
        public String setSafemodeOn = "{%PREFIX%}&7Safemode turned &aon&7!";
        public String setSafemodeOff = "{%PREFIX%}&7Safemode turned &coff&7!";
        public String safemodeOn = "{%PREFIX%}&7Safemode is turned &aon&7!";
        public String safemodeOff = "{%PREFIX%}&7Safemode is turned &coff&7!";
        public String announce = "&bAA &c&lPANIC > &6{%PLAYER%} &ehas activated panic mode! Please investigate immediately.";
        public String enabled = "{%PREFIX%}&7You have entered panic mode! Staff have been alerted and will be with you momentarily.";
        public String disabled = "{%PREFIX%}&7You have left panic mode.";
        public String staffDisabled = "{%PREFIX%}&7A staff member has turned off your panic mode!";
        public String forcedOut = "{%PREFIX%}&7The plugin is reloading/disabling, you have been forced out of panic mode!";

        public String noOpen = "{%PREFIX%}&7You cannot open containers whilst in panic mode!";
        public String noDrop = "{%PREFIX%}&7You cannot drop items whilst in panic mode!";
        public String noMisc = "{%PREFIX%}&7You cannot interact with this whilst in panic mode!";
        public String noBlockBreak = "{%PREFIX%}&7You cannot break blocks whilst in panic mode!";
        public String noBlockPlace = "{%PREFIX%}&7You cannot place blocks whilst in panic mode!";
        public String noVehicleUse = "{%PREFIX%}&7You cannot interact with this vehicle whilst in panic mode!";
        public String noDamager = "{%PREFIX%}&7You cannot damage players whilst in panic mode!";
        public String noDamagee = "{%PREFIX%}&c{%PLAYER_NAME%} &7is in panic mode, you cannot attack them!";
        public String noCommands = "{%PREFIX%}&7You cannot use that command whilst in panic mode!";
        public String noChat = "{%PREFIX%}&7You cannot chat whilst in panic mode!";

        private boolean readonly = false;

        public Messages(String prefix, String noPermission, String setSafemodeOn,
                        String setSafemodeOff, String safemodeOn, String safemodeOff,
                        String announce, String enabled, String disabled, String staffDisabled,
                        String forcedOut, String noOpen, String noDrop, String noMisc,
                        String noBlockBreak, String noBlockPlace, String noVehicleUse,
                        String noDamager, String noDamagee, String noCommands,
                        String noChat) {
            this.prefix = prefix;
            this.noPermission = noPermission;
            this.setSafemodeOn = setSafemodeOn;
            this.setSafemodeOff = setSafemodeOff;
            this.safemodeOn = safemodeOn;
            this.safemodeOff = safemodeOff;
            this.announce = announce;
            this.enabled = enabled;
            this.disabled = disabled;
            this.staffDisabled = staffDisabled;
            this.forcedOut = forcedOut;
            this.noOpen = noOpen;
            this.noDrop = noDrop;
            this.noMisc = noMisc;
            this.noBlockBreak = noBlockBreak;
            this.noBlockPlace = noBlockPlace;
            this.noVehicleUse = noVehicleUse;
            this.noDamager = noDamager;
            this.noDamagee = noDamagee;
            this.noCommands = noCommands;
            this.noChat = noChat;
        }

        public Messages() {
            readonly = true;
        }

        public String getMessage(String msg, Map<String, String> placeholders) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace(entry.getKey(), entry.getValue());
            }
            return msg;
        }
    }
}
