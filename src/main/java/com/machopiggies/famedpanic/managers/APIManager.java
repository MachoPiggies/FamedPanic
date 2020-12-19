package com.machopiggies.famedpanic.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.EventListener;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Logger;
import com.machopiggies.famedpanicapi.FamedPanicAPI;
import com.machopiggies.famedpanicapi.misc.Request;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class APIManager extends Observer {

    private final Plugin api;
    public APISettings apiSettings;

    public APIManager(Plugin api) {
        this.api = api;
    }

    @Override
    public void onActivate() {
        try {
            apiSettings = new APISettings(
                    Config.getConfig().getBoolean("api.enabled", false),
                    Config.getConfig().getBoolean("api.canAccessTokens", false),
                    Config.getConfig().getBoolean("api.canChangeSafemode", false),
                    Config.getConfig().getBoolean("api.canChangePanicking", false),
                    Config.getConfig().getBoolean("api.canChangeInspector", false)
            );

            // Primary Settings \\

            Constructor<?> guiSettings = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$Settings$GuiMenuSettings").getConstructor(boolean.class, boolean.class, String.class, ChatColor.class, ChatColor.class);
            guiSettings.setAccessible(true);
            Object guiSettingsObject = guiSettings.newInstance(Config.settings.guis.enabled, Config.settings.guis.useBorder, Config.settings.guis.borderColor.name().toLowerCase().replace("_", " "), Config.settings.guis.titleColor, Config.settings.guis.defaultColor);

            Constructor<?> inspectorSettings = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$Settings$PanicInspectorSettings").getConstructor(boolean.class, String.class, String.class, int.class, boolean.class);
            inspectorSettings.setAccessible(true);
            Object inspectorSettingsObject = inspectorSettings.newInstance(Config.settings.panicInspector.enabled, Config.settings.panicInspector.vanishCmd, Config.settings.panicInspector.unvanishCmd, Config.settings.panicInspector.kickDelay, Config.settings.panicInspector.inspectorAlert);

            Constructor<?> primarySettings = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$Settings").getConstructor(boolean.class, boolean.class, boolean.class, long.class, boolean.class, Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$Settings$GuiMenuSettings"), Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$Settings$PanicInspectorSettings"));
            primarySettings.setAccessible(true);
            Object primarySettingsObject = primarySettings.newInstance(Config.settings.bungee, Config.settings.showTitle, Config.settings.savePanicking, Config.settings.defaultCooldown, Config.settings.allowStaffTeleport, guiSettingsObject, inspectorSettingsObject);

            // Action Preferences \\

            Constructor<?> actionPrefs = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$ActionPreferences").getConstructor(boolean.class, boolean.class, boolean.class, boolean.class, boolean.class, boolean.class, boolean.class, boolean.class, List.class);
            actionPrefs.setAccessible(true);
            Object actionPrefsObject = actionPrefs.newInstance(EventListener.prefs.disableMovement, EventListener.prefs.stopOpening, EventListener.prefs.stopDropping, EventListener.prefs.stopPickup, EventListener.prefs.stopInventoryMoving, EventListener.prefs.stopWorldInteraction, EventListener.prefs.stopDamager, EventListener.prefs.stopDamagee, EventListener.prefs.stopCommands);

            // Title Settings \\

            Constructor<?> titleSettings = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$TitleSettings").getConstructor(String.class, String.class);
            titleSettings.setAccessible(true);
            Object titleSettingsObject = titleSettings.newInstance(Config.getConfig().getString("title.title", "&c&lPANIC"), Config.getConfig().getString("title.subtitle", "&eA staff member will be with you shortly!"));

            // Auth Contact \\

            Constructor<?> authContact = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthSecrets").getConstructor(String.class, String.class);
            authContact.setAccessible(true);
            Object authContactObject = authContact.newInstance(apiSettings.canAccessTokens ? Config.auth.discord : "", apiSettings.canAccessTokens ? Config.auth.slack : "");

            // Auth Preferences \\

            Constructor<?> discordPrefs = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthPrefs$Discord").getConstructor(boolean.class, String.class, boolean.class, String.class, String.class, String.class);
            discordPrefs.setAccessible(true);
            Object discordPrefsObject = discordPrefs.newInstance(Config.authPrefs.discord.enabled, Config.authPrefs.discord.webhookURL, Config.authPrefs.discord.useEmbed, Config.authPrefs.discord.embedAltEnter, Config.authPrefs.discord.embedAltLeave, Config.authPrefs.discord.color);

            Constructor<?> slackPrefs = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthPrefs$Slack").getConstructor(boolean.class, String.class, boolean.class, String.class, String.class);
            slackPrefs.setAccessible(true);
            Object slackPrefsObject = slackPrefs.newInstance(Config.authPrefs.slack.enabled, Config.authPrefs.slack.webhookURL, Config.authPrefs.slack.useBlock, Config.authPrefs.slack.blockAltEnter, Config.authPrefs.slack.blockAltLeave);

            Constructor<?> authPrefs = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthPrefs").getConstructor(Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthPrefs$Discord"), Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthPrefs$Slack"));
            authPrefs.setAccessible(true);
            Object authPrefsObject = authPrefs.newInstance(discordPrefsObject, slackPrefsObject);

            // Cache Establishment \\

            Constructor<?> constructor = Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache").getConstructor(boolean.class, boolean.class, Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$Settings"), Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$ActionPreferences"), Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$TitleSettings"), Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthSecrets"), Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache$AuthPrefs"),  com.machopiggies.famedpanicapi.misc.APISettings.class);
            constructor.setAccessible(true);
            Object cache = constructor.newInstance(Config.isSafemode(), Config.isDebugMode(), primarySettingsObject, actionPrefsObject, titleSettingsObject, authContactObject, authPrefsObject, apiSettings.getAPIVersion());
            Method method = FamedPanicAPI.class.getDeclaredMethod("a", Class.forName("com.machopiggies.famedpanicapi.FamedPanicAPI$Cache"));
            method.setAccessible(true);
            method.invoke(null, cache);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            File file = Logger.createErrorLog(e, "information relay error");
            Logger.severe("An error occurred whilst trying to relay information to API. If restarting your server does not fix this, please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
        }
    }

    public static class APISettings {
        public boolean enabled;
        public boolean canAccessTokens;
        public boolean canChangeSafemode;
        public boolean canChangePanicking;
        public boolean canChangeInspector;

        public APISettings(boolean enabled, boolean canAccessTokens, boolean canChangeSafemode, boolean canChangePanicking, boolean canChangeInspector) {
            this.enabled = enabled;
            this.canAccessTokens = canAccessTokens;
            this.canChangeSafemode = canChangeSafemode;
            this.canChangePanicking = canChangePanicking;
            this.canChangeInspector = canChangeInspector;
        }

        public com.machopiggies.famedpanicapi.misc.APISettings getAPIVersion() {
            return new com.machopiggies.famedpanicapi.misc.APISettings(
                    enabled,
                    canAccessTokens,
                    canChangeSafemode,
                    canChangePanicking,
                    canChangeInspector
            );
        }
    }

    @EventHandler
    private void request(Request req) {
        if (req.a == null || req.b == null) {
            throw new NullPointerException();
        }

        JsonElement a = new JsonParser().parse(new String(req.b, StandardCharsets.UTF_8));
        switch (req.a) {
            case a:
                try {
                    if (apiSettings.enabled && apiSettings.canChangePanicking) {
                        Player target;
                        if ((target = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("a").getAsString()))) != null) {
                            if (!target.isValid() || !target.isOnline()) {
                                throw new IllegalStateException(target.isValid() ? "player is not online" : "player is not valid");
                            }

                            PanicData data = new PanicData(
                                    target,
                                    new PanicData.Settings(
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("a").getAsFloat(),
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("b").getAsFloat(),
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("c").getAsBoolean(),
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("d").getAsBoolean()
                                    )
                            );

                            Core.getPanicManager().protect(data);
                        }
                    } else {
                        Logger.severe("Api tried to change panic status of a player when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "commandmap unreachable");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            case b:
                try {
                    if (apiSettings.enabled && apiSettings.canChangePanicking) {
                        Player target;
                        if ((target = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("a").getAsString()))) != null) {
                            if (!target.isValid() || !target.isOnline()) {
                                throw new IllegalStateException(target.isValid() ? "player is not online" : "player is not valid");
                            }

                            float yaw = a.getAsJsonObject().get("b").getAsJsonObject().get("f") != null ? a.getAsJsonObject().get("b").getAsJsonObject().get("f").getAsFloat() : 0f;
                            float pitch = a.getAsJsonObject().get("b").getAsJsonObject().get("g") != null ? a.getAsJsonObject().get("b").getAsJsonObject().get("g").getAsFloat() : 0f;

                            PanicData data = new PanicData(
                                    target,
                                    target.getUniqueId(),
                                    a.getAsJsonObject().get("d").getAsLong(),
                                    new Location(
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("b").getAsBoolean() ? Bukkit.getServer().getWorld(UUID.fromString(a.getAsJsonObject().get("b").getAsJsonObject().get("a").getAsString())) : Bukkit.getServer().getWorld(a.getAsJsonObject().get("b").getAsJsonObject().get("a").getAsString()),
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("c").getAsDouble(),
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("d").getAsDouble(),
                                            a.getAsJsonObject().get("b").getAsJsonObject().get("e").getAsDouble(),
                                            yaw,
                                            pitch
                                    ),
                                    new PanicData.Settings(
                                            a.getAsJsonObject().get("c").getAsJsonObject().get("a").getAsFloat(),
                                            a.getAsJsonObject().get("c").getAsJsonObject().get("b").getAsFloat(),
                                            a.getAsJsonObject().get("c").getAsJsonObject().get("c").getAsBoolean(),
                                            a.getAsJsonObject().get("c").getAsJsonObject().get("d").getAsBoolean()
                                    )
                            );

                            Core.getPanicManager().protect(data);
                        }
                    } else {
                        Logger.severe("Api tried to change panic status of a player when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "commandmap unreachable");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            case c:
                try {
                    if (apiSettings.enabled && apiSettings.canChangePanicking) {
                        Player target;
                        if ((target = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("a").getAsString()))) != null) {
                            if (!target.isValid() || !target.isOnline()) {
                                throw new IllegalStateException(target.isValid() ? "player is not online" : "player is not valid");
                            }

                            CommandSender sender = null;
                            try {
                                if (a.getAsJsonObject().get("b") != null && !a.getAsJsonObject().get("b").getAsString().equals("null")) {
                                    sender = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("b").getAsString()));
                                }
                            } catch (Exception ignored) { }

                            Core.getPanicManager().unprotect(target, sender);
                        }
                    } else {
                        Logger.severe("Api tried to change panic status of a player when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "commandmap unreachable");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            case d:
                try {
                    if (apiSettings.enabled && apiSettings.canChangeSafemode) {
                        Config.setSafemode(a.getAsJsonObject().get("a").getAsBoolean());
                    } else {
                        Logger.severe("Api tried to change safemode status status of the plugin when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "commandmap unreachable");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            case e:
                try {
                    if (apiSettings.enabled && apiSettings.canChangeInspector) {
                        Player target;
                        if ((target = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("a").getAsString()))) != null) {
                            if (!target.isValid() || !target.isOnline()) {
                                throw new IllegalStateException(target.isValid() ? "player is not online" : "player is not valid");
                            }

                            Player targetA;
                            Player targetB;

                            if ((targetA = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("b").getAsJsonObject().get("a").getAsString()))) != null) {
                                if (!targetA.isValid() || !targetA.isOnline()) {
                                    throw new IllegalStateException("inspector player cannot be null");
                                }
                            }

                            if ((targetB = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("b").getAsJsonObject().get("b").getAsString()))) != null) {
                                if (!targetB.isValid() || !targetB.isOnline()) {
                                    throw new IllegalStateException("target player cannot be null");
                                }
                            }

                            if (targetA != null && targetB != null) {
                                Core.getPanicInspectorManager().addInspector(target, new InspectorData(
                                        targetA,
                                        targetB
                                ));
                            }
                        }
                    } else {
                        Logger.severe("Api tried to change inspector status of a player when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "inspector creation");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            case f:
                try {
                    if (apiSettings.enabled && apiSettings.canChangeInspector) {
                        Player target;
                        if ((target = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("a").getAsString()))) != null) {
                            if (!target.isValid() || !target.isOnline()) {
                                throw new IllegalStateException(target.isValid() ? "player is not online" : "player is not valid");
                            }

                            Core.getPanicInspectorManager().removeInspector(target, Core.getPanicInspectorManager().getInspectors().get(target), PanicInspectorManager.RemoveReason.values()[a.getAsJsonObject().get("b").getAsInt()]);
                        }
                    } else {
                        Logger.severe("Api tried to change inspector status of a player when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "inspector removal");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            case g:
                try {
                    if (apiSettings.enabled && apiSettings.canChangePanicking) {
                        Player target;
                        if ((target = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("a").getAsString()))) != null) {
                            if (!target.isValid() || !target.isOnline()) {
                                throw new IllegalStateException(target.isValid() ? "player is not online" : "player is not valid");
                            }

                            target.setWalkSpeed(0.2f);
                            target.setFlySpeed(0.1f);
                            target.setFlying(false);
                            target.setAllowFlight(false);
                            target.setFallDistance(0);
                        }
                    } else {
                        Logger.severe("Api tried to change panic status of a player when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "panic reset");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            case h:
                try {
                    if (apiSettings.enabled && apiSettings.canChangeInspector) {
                        Player target;
                        if ((target = Bukkit.getPlayer(UUID.fromString(a.getAsJsonObject().get("a").getAsString()))) != null) {
                            if (!target.isValid() || !target.isOnline()) {
                                throw new IllegalStateException(target.isValid() ? "player is not online" : "player is not valid");
                            }

                            InspectorData data;
                            if ((data = Core.getPanicInspectorManager().getInspectors().get(target)) != null && data.player.equals(target)) {
                                target.teleport(data.origin);
                                target.setGameMode(data.gamemode);
                            } else {
                                target.teleport(target.getWorld().getSpawnLocation());
                                target.setGameMode(target.getServer().getDefaultGameMode());
                            }
                            Core.getPanicInspectorManager().getInspectors().remove(target);
                        }
                    } else {
                        Logger.severe("Api tried to change inspector status of a player when setting is disabled in config.yml");
                    }
                } catch (Exception e) {
                    File file = Logger.createErrorLog(e, "inspector reset");
                    Logger.severe("API request error. Please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                }
                break;
            default:
                throw new IllegalStateException("invalid request");
        }
    }
}
