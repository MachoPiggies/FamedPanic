package com.machopiggies.famedpanic.util;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.gui.StainedMaterialColor;
import jdk.javadoc.internal.doclets.formats.html.AllClassesIndexWriter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static FileConfiguration getConfig() {
        return Core.getPlugin().getConfig();
    }

    public static Auth auth = null;
    public static AuthPrefs authPrefs = null;
    public static Settings settings;
    private static File dEJson = null;
    private static File dLJson = null;
    private static File sEJson = null;
    private static File sLJson = null;
    private static boolean safemode;

    public static boolean isSafemode() {
        return safemode;
    }

    public static void setSafemode(boolean safemode) {
        Config.safemode = safemode;
        getConfig().set("safemode", safemode);
        Core.getPlugin().saveConfig();
    }

    public static File getDEJson() {
        return dEJson;
    }
    public static File getDLJson() {
        return dLJson;
    }

    public static File getSEJson() {
        return sEJson;
    }
    public static File getSLJson() {
        return sLJson;
    }

    public static void initialize() {
        Core.getPlugin().saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        Message.buildConfig();

        safemode = getConfig().getBoolean("safemode", false);

        settings = new Settings(
                getConfig().getBoolean("settings.bungee", false),
                getConfig().getBoolean("settings.showTitle", true),
                getConfig().getBoolean("settings.savePanicking", false),
                getConfig().getInt("settings.defaultCooldown", -1),
                getConfig().getBoolean("settings.allowStaffTeleportPI", true),
                getConfig().getBoolean("settings.guis.enabled", true),
                getConfig().getBoolean("settings.guis.useBorder", true),
                StainedMaterialColor.valueOf(getConfig().getString("settings.guis.borderColor", "light gray").toUpperCase().replace(" ", "_")),
                ChatColor.valueOf(getConfig().getString("settings.guis.titleColor", "red").toUpperCase().replace(" ", "_")),
                ChatColor.valueOf(getConfig().getString("settings.guis.defaultColor", "gray").toUpperCase().replace(" ", "_")),
                getConfig().getBoolean("settings.usePanicInspector.enabled", false),
                getConfig().getString("settings.usePanicInspector.vanishCommand", "vanish"),
                getConfig().getString("settings.usePanicInspector.unvanishCommand", "unvanish"),
                getConfig().getInt("settings.usePanicInspector.secondsUntilRemoval", 10),
                getConfig().getBoolean("settings.usePanicInspector.alertTarget", true)
        );

        Map<String, Object> defaults = new HashMap<>();
        defaults.put("discord.enabled", false);
        defaults.put("discord.webhookURL", "");
        defaults.put("discord.embed.useEmbed", true);
        defaults.put("discord.embed.embedAltEnter", "@everyone {%PLAYER_NAME%} has activated panic mode! Please see to this immediately.");
        defaults.put("discord.embed.embedAltLeave", "{%PLAYER_NAME%} has deactivated panic mode!");
        defaults.put("discord.embed.color", "13828351");
        defaults.put("slack.enabled", false);
        defaults.put("slack.webhookURL", "");
        defaults.put("slack.block.useBlock", true);
        defaults.put("slack.block.blockAltEnter", "@channel {%PLAYER_NAME%} has activated panic mode! Please see to this immediately.");
        defaults.put("slack.block.blockAltLeave", "{%PLAYER_NAME%} has deactivated panic mode!");
        File auth = FileUtil.getYamlFile("auth.yml", Core.getPlugin().getDataFolder(), defaults);
        YamlConfiguration authConfig = YamlConfiguration.loadConfiguration(auth);

        Config.auth = new Auth(
                authConfig.getString("discord.webhookURL", ""),
                authConfig.getString("slack.webhookURL", "")
        );
        authPrefs = new AuthPrefs(
                authConfig.getBoolean("discord.enabled", false),
                authConfig.getString("discord.webhookURL", ""),
                authConfig.getBoolean("discord.embed.useEmbed", true),
                authConfig.getString("discord.embed.embedAltEnter", "@everyone {%PLAYER_NAME%} has activated panic mode! Please see to this immediately."),
                authConfig.getString("discord.embed.embedAltLeave", "{%PLAYER_NAME%} has deactivated panic mode!"),
                authConfig.getString("discord.embed.color", "13828351"),
                authConfig.getBoolean("slack.enabled", false),
                authConfig.getString("slack.webhookURL", ""),
                authConfig.getBoolean("slack.block.useBlock", true),
                authConfig.getString("slack.block.blockAltEnter", "@channel {%PLAYER_NAME%} has activated panic mode! Please see to this immediately."),
                authConfig.getString("slack.block.blockAltLeave", "{%PLAYER_NAME%} has deactivated panic mode!")
        );

        File embedsFolder = FileUtil.getFolder("embeds", Core.getPlugin().getDataFolder());
        Config.dEJson = FileUtil.getJsonFile("discordEnter.json", embedsFolder, Core.getPlugin().getResource("discordEnter.json"));
        Config.dLJson = FileUtil.getJsonFile("discordLeave.json", embedsFolder, Core.getPlugin().getResource("discordLeave.json"));
        Config.sEJson = FileUtil.getJsonFile("slackEnter.json", embedsFolder, Core.getPlugin().getResource("slackEnter.json"));
        Config.sLJson = FileUtil.getJsonFile("slackLeave.json", embedsFolder, Core.getPlugin().getResource("slackLeave.json"));
        Logger.warn("1: " + sLJson.toString());
        try {
            Logger.warn("2: " + new JsonParser().parse(new JsonReader(new FileReader(Config.getSLJson()))).toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDebugMode() {
        return true;
    }

    public static class Settings {
        public boolean bungee;
        public boolean showTitle;
        public boolean savePanicking;
        public long defaultCooldown;
        public boolean allowStaffTeleport;
        public GuiMenuSettings guis;
        public PanicInspectorSettings panicInspector;

        public Settings(boolean bungee, boolean showTitle, boolean savePanicking,
                        long defaultCooldown, boolean allowStaffTeleport,

                        boolean useGuis, boolean useBorder, StainedMaterialColor borderColor,
                        ChatColor titleColor, ChatColor defaultColor,

                        boolean usePanicInspector, String vanishCmd, String unvanishCmd,
                        int kickDelay, boolean inspectorAlert) {
            this.bungee = bungee;
            this.showTitle = showTitle;
            this.savePanicking = savePanicking;
            this.defaultCooldown = defaultCooldown;
            this.allowStaffTeleport = allowStaffTeleport;
            guis = new GuiMenuSettings(
                    useGuis,
                    useBorder,
                    borderColor,
                    titleColor,
                    defaultColor
            );
            panicInspector = new PanicInspectorSettings(
                    usePanicInspector,
                    vanishCmd,
                    unvanishCmd,
                    kickDelay,
                    inspectorAlert
            );
        }

        public static class PanicInspectorSettings {
            public boolean enabled;
            public String vanishCmd;
            public String unvanishCmd;
            public int kickDelay;
            public boolean inspectorAlert;

            public PanicInspectorSettings(boolean enabled, String vanishCmd, String unvanishCmd, int kickDelay, boolean inspectorAlert) {
                this.enabled = enabled;
                this.vanishCmd = vanishCmd;
                this.unvanishCmd = unvanishCmd;
                this.kickDelay = kickDelay;
                this.inspectorAlert = inspectorAlert;
            }
        }

        public static class GuiMenuSettings {
            public boolean enabled;
            public boolean useBorder;
            public StainedMaterialColor borderColor;
            public ChatColor titleColor;
            public ChatColor defaultColor;

            public GuiMenuSettings(boolean enabled, boolean useBorder, StainedMaterialColor borderColor, ChatColor titleColor, ChatColor defaultColor) {
                this.enabled = enabled;
                this.useBorder = useBorder;
                this.borderColor = borderColor;
                this.titleColor = titleColor;
                this.defaultColor = defaultColor;
            }
        }
    }

    public static class Auth {
        public String discord;
        public String slack;

        public Auth(String discord, String slack) {
            this.discord = discord;
            this.slack = slack;
        }
    }

    public static class AuthPrefs {
        public Discord discord;
        public Slack slack;

        public AuthPrefs(boolean discordEnabled, String discordWebhookURL, boolean useEmbed,
                         String embedAltEnter, String embedAltLeave, String color,

                         boolean slackEnabled, String slackWebhookURL, boolean useBlock,
                         String blockAltEnter, String blockAltLeave
        ) {
            discord = new Discord(discordEnabled, discordWebhookURL, useEmbed, embedAltEnter, embedAltLeave, color);
            slack = new Slack(slackEnabled, slackWebhookURL, useBlock, blockAltEnter, blockAltLeave);
        }

        public static class Discord {
            public boolean enabled;
            public String webhookURL;
            public boolean useEmbed;
            public String embedAltEnter;
            public String embedAltLeave;
            public String color;

            public Discord(boolean enabled, String webhookURL, boolean useEmbed, String embedAltEnter, String embedAltLeave, String color) {
                this.enabled = enabled;
                this.webhookURL = webhookURL;
                this.useEmbed = useEmbed;
                this.embedAltEnter = embedAltEnter;
                this.embedAltLeave = embedAltLeave;
                this.color = color;
            }
        }

        public static class Slack {
            public boolean enabled;
            public String webhookURL;
            public boolean useBlock;
            public String blockAltEnter;
            public String blockAltLeave;

            public Slack(boolean enabled, String webhookURL, boolean useBlock, String blockAltEnter, String blockAltLeave) {
                this.enabled = enabled;
                this.webhookURL = webhookURL;
                this.useBlock = useBlock;
                this.blockAltEnter = blockAltEnter;
                this.blockAltLeave = blockAltLeave;

            }
        }
    }
}
