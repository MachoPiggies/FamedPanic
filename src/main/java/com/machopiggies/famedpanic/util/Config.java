package com.machopiggies.famedpanic.util;

import com.machopiggies.famedpanic.Core;
import jdk.javadoc.internal.doclets.formats.html.AllClassesIndexWriter;
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
    private static boolean safemode = false;

    public static boolean isSafemode() {
        return safemode;
    }

    public static void setSafemode(boolean safemode) {
        Config.safemode = safemode;
    }

    public static File getDEJson() {
        return dEJson;
    }
    public static File getDLJson() {
        return dLJson;
    }

    public static void initialize() {
        Core.getPlugin().saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        Message.buildConfig();
        settings = new Settings(
                getConfig().getBoolean("settings.bungee", false),
                getConfig().getBoolean("settings.showTitle", true),
                getConfig().getBoolean("settings.savePanicking", false),
                getConfig().getInt("settings.defaultCooldown", -1),
                getConfig().getBoolean("settings.allowStaffTeleportPI", true),
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
        defaults.put("discord.embed.colorHEX", 13828351);
        defaults.put("slack.enabled", false);
        defaults.put("slack.webhookURL", "");
        defaults.put("slack.useBlock", true);
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
                authConfig.getInt("discord.embed.colorHEX", 13828351),
                authConfig.getBoolean("slack.enabled", false),
                authConfig.getString("slack.webhookURL", ""),
                authConfig.getBoolean("slack.useBlock", true)
        );

        File embedsFolder = FileUtil.getFolder("embeds", Core.getPlugin().getDataFolder());
        Config.dEJson = FileUtil.getJsonFile("discordEnter.json", embedsFolder, Core.getPlugin().getResource("discordEnter.json"));
        Config.dLJson = FileUtil.getJsonFile("discordLeave.json", embedsFolder, Core.getPlugin().getResource("discordLeave.json"));
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
        public PanicInspectorSettings panicInspector;

        public Settings(boolean bungee, boolean showTitle, boolean savePanicking,
                        long defaultCooldown, boolean allowStaffTeleport,
                        boolean usePanicInspector, String vanishCmd, String unvanishCmd, int kickDelay, boolean inspectorAlert) {
            this.bungee = bungee;
            this.showTitle = showTitle;
            this.savePanicking = savePanicking;
            this.defaultCooldown = defaultCooldown;
            this.allowStaffTeleport = allowStaffTeleport;
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
                         String embedAltEnter, String embedAltLeave, int color,

                         boolean slackEnabled, String slackWebhookURL, boolean useBlock
        ) {
            discord = new Discord(discordEnabled, discordWebhookURL, useEmbed, embedAltEnter, embedAltLeave, color);
            slack = new Slack(slackEnabled, slackWebhookURL, useBlock);
        }

        public static class Discord {
            public boolean enabled;
            public String webhookURL;
            public boolean useEmbed;
            public String embedAltEnter;
            public String embedAltLeave;
            public int color;

            public Discord(boolean enabled, String webhookURL, boolean useEmbed, String embedAltEnter, String embedAltLeave, int color) {
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

            public Slack(boolean enabled, String webhookURL, boolean useBlock) {
                this.enabled = enabled;
                this.webhookURL = webhookURL;
                this.useBlock = useBlock;
            }
        }
    }
}
