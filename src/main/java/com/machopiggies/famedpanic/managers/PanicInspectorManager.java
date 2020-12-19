package com.machopiggies.famedpanic.managers;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.*;

public class PanicInspectorManager extends Observer {

    private Map<Player, InspectorData> inspectors;
    private Map<Player, RepeatingTask> removing;

    @Override
    public void onActivate() {
        inspectors = new HashMap<>();
        removing = new HashMap<>();
    }

    @Override
    public void onDeactivate() {
        removing = null;
        inspectors = null;
    }

    public void emergencyResetAll() {
        for (Map.Entry<Player, RepeatingTask> entry : removing.entrySet()) {
            entry.getValue().cancel();
            remove(entry.getKey(), inspectors.get(entry.getKey()), Message.msgs.inspectorLeave);
            removing.remove(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Player, InspectorData> entry : inspectors.entrySet()) {
            Message.send(entry.getKey(), Message.msgs.forcedOut);
            removeInspector(entry.getKey(), entry.getValue(), RemoveReason.SERVER_CLOSE);
        }
    }

    public void addInspector(Player player, InspectorData data) {
        if (Core.getApi() != null) {
            Event event = null;
            try {
                Constructor<?> dataConst = Objects.requireNonNull(Class.forName("com.machopiggies.famedpanicapi.misc.InspectorData").getConstructor(Player.class, Player.class, Location.class, GameMode.class, long.class));
                Object dataObj = dataConst.newInstance(data.player, data.target, data.origin, data.gamemode, data.time);

                Constructor<?> eventConst = Objects.requireNonNull(Class.forName("com.machopiggies.famedpanicapi.events.PlayerInspectorEnterEvent")).getConstructor(Objects.requireNonNull(Class.forName("com.machopiggies.famedpanicapi.misc.InspectorData")));
                event = (Event) eventConst.newInstance(dataObj);
            } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                File file = Logger.createErrorLog(e, "inspector enter api error");
                Logger.severe("An error occurred whilst making an API event, this should not cause any issues with your server, but may still need to be reported to the plugin developer as it may break other plugins. [Created error log at " + file.getPath() + "]");
            }
            if (event != null) {
                Bukkit.getPluginManager().callEvent(event);
                try {
                    if ((boolean) event.getClass().getMethod("isCancelled").invoke(event)) {
                        if (Core.getApiManager().apiSettings.enabled && Core.getApiManager().apiSettings.canChangeInspector) {
                            return;
                        } else {
                            Logger.severe("Api tried to change inspector status of a player when setting is disabled in config.yml");
                        }
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    File file = Logger.createErrorLog(e, "inspector enter api error");
                    Logger.severe("An error occurred whilst making an API event, this should not cause any issues with your server, but may still need to be reported to the plugin developer as it may break other plugins. [Created error log at " + file.getPath() + "]");
                }
            }
        }

        inspectors.put(player, data);
        player.performCommand(Config.settings.panicInspector.vanishCmd);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(data.target.getLocation(), PlayerTeleportEvent.TeleportCause.SPECTATE);
        player.setMetadata("panicinspector", new FixedMetadataValue(Core.getPlugin(), data.time));
        Message.send(player, Message.msgs.inspectorEnter);
        if (Config.settings.panicInspector.inspectorAlert) {
            Map<String, String> map = new HashMap<>();
            map.put("{%STAFF_NAME%}", player.getName());
            map.put("{%STAFF_DISPLAYNAME%}", player.getDisplayName());
            Message.send(data.target, Message.msgs.inspectorArrival, map);
        }
    }

    public void removeInspector(Player player, InspectorData data, RemoveReason reason) {
        switch (reason) {
            case PANIC_CANCELLED:
                int kickDelay = Config.settings.panicInspector.kickDelay;
                if (Core.getApi() != null) {
                    Event event = null;
                    try {
                        Constructor<?> dataConst = Objects.requireNonNull(Class.forName("com.machopiggies.famedpanicapi.misc.InspectorData").getConstructor(Player.class, Player.class, Location.class, GameMode.class, long.class));
                        Object dataObj = dataConst.newInstance(data.player, data.target, data.origin, data.gamemode, data.time);

                        Constructor<?> eventConst = Objects.requireNonNull(Class.forName("com.machopiggies.famedpanicapi.events.PlayerInspectorLeaveEvent")).getConstructor(Player.class, Objects.requireNonNull(Class.forName("com.machopiggies.famedpanicapi.misc.InspectorData")), int.class, int.class);
                        event = (Event) eventConst.newInstance(player, dataObj, reason.ordinal(), kickDelay);
                    } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        File file = Logger.createErrorLog(e, "inspector leave api error");
                        Logger.severe("An error occurred whilst making an API event, this should not cause any issues with your server, but may still need to be reported to the plugin developer as it may break other plugins. [Created error log at " + file.getPath() + "]");
                    }
                    if (event != null) {
                        Bukkit.getPluginManager().callEvent(event);
                        try {
                            Object casted = Objects.requireNonNull(Class.forName("com.machopiggies.famedpanicapi.events.PlayerInspectorLeaveEvent")).cast(event);
                            int newKickDelay = (int) casted.getClass().getMethod("getDelay").invoke(casted);
                            if (newKickDelay != kickDelay) {
                                if (Core.getApiManager().apiSettings.enabled && Core.getApiManager().apiSettings.canChangeInspector) {
                                    kickDelay = newKickDelay;
                                } else {
                                    Logger.severe("Api tried to change inspector status of a player when setting is disabled in config.yml");
                                }
                            }
                        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
                            File file = Logger.createErrorLog(e, "inspector leave api error");
                            Logger.severe("An error occurred whilst making an API event, this should not cause any issues with your server, but may still need to be reported to the plugin developer as it may break other plugins. [Created error log at " + file.getPath() + "]");
                        }
                    }
                }

                if (kickDelay > 0) {
                    Map<String, String> map = new HashMap<>();
                    map.put("{%TARGET_NAME%}", data.target.getName());
                    map.put("{%TARGET_DISPLAYNAME%}", data.target.getDisplayName());
                    map.put("{%INSPECTOR_KICK_DELAY%}", TimeDateUtil.getSimpleDurationStringFromSeconds(kickDelay));
                    RepeatingTask rt = new RepeatingTask();
                    rt.setTask(Bukkit.getScheduler().runTaskLater(Core.getPlugin(), () -> {
                            remove(player, data, Message.msgs.inspectorLeave);
                            removing.remove(player, rt);
                    }, kickDelay * 20L));
                    removing.put(player, rt);
                    Message.send(player, Message.msgs.inspectorKick, map);
                } else {
                    remove(player, data, Message.msgs.inspectorLeave);
                }
                break;
            case COMMAND:
            case QUIT:
                remove(player, data, Message.msgs.inspectorLeave);
                break;
            case SERVER_CLOSE:
            case ERROR:
            default:
                remove(player, data, Message.msgs.forcedOut);
                break;
        }
    }

    public boolean isInspector(Player player) {
        return inspectors.containsKey(player);
    }

    public Map<Player, InspectorData> getInspectors() {
        return inspectors;
    }

    private void remove(Player player, InspectorData data, String message, Map<String, String> placeholders) {

        if (player.hasMetadata("panicinspector")) {
            player.removeMetadata("panicinspector", Core.getPlugin());
        }
        if (data != null) {
            player.setGameMode(data.gamemode);
            player.teleport(data.origin);
        } else {
            player.setGameMode(player.getServer().getDefaultGameMode());
            player.teleport(player.getWorld().getSpawnLocation());
        }
        player.performCommand(Config.settings.panicInspector.unvanishCmd);
        if (placeholders != null) {
            Message.send(player, message, placeholders);
        } else {
            Message.send(player, message);
        }
        inspectors.remove(player);
    }

    private void remove(Player player, InspectorData data, String message) {
        remove(player, data, message, null);
    }

    public enum RemoveReason {
        PANIC_CANCELLED,
        COMMAND,
        SERVER_CLOSE,
        QUIT,
        ERROR
    }
}
