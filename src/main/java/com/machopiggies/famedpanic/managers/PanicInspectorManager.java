package com.machopiggies.famedpanic.managers;

import com.google.gson.JsonObject;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.Instant;
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
        //todo
        inspectors.put(player, data);
        player.performCommand(Config.settings.panicInspector.vanishCmd);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(data.target.getLocation(), PlayerTeleportEvent.TeleportCause.SPECTATE);
        player.setMetadata("panickinspector", new FixedMetadataValue(Core.getPlugin(), data.time));
        Message.send(player, Message.msgs.inspectorEnter);
        if (Config.settings.panicInspector.inspectorAlert) {
            Map<String, String> map = new HashMap<>();
            map.put("{%STAFF_NAME%}", player.getName());
            map.put("{%STAFF_DISPLAYNAME%}", player.getDisplayName());
            Message.send(data.target, Message.msgs.inspectorArrival, map);
        }
    }

    public void removeInspector(Player player, InspectorData data, RemoveReason reason) {
        //todo

        switch (reason) {
            case PANIC_CANCELLED:
                if (Config.settings.panicInspector.kickDelay > 0) {
                    Map<String, String> map = new HashMap<>();
                    map.put("{%TARGET_NAME%}", data.target.getName());
                    map.put("{%TARGET_DISPLAYNAME%}", data.target.getDisplayName());
                    map.put("{%INSPECTOR_KICK_DELAY%}", TimeDateUtil.getSimpleDurationStringFromSeconds(Config.settings.panicInspector.kickDelay));
                    RepeatingTask rt = new RepeatingTask();
                    rt.setTask(Bukkit.getScheduler().runTaskLater(Core.getPlugin(), () -> {
                            remove(player, data, Message.msgs.inspectorLeave);
                            removing.remove(player, rt);
                    }, Config.settings.panicInspector.kickDelay * 20));
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

    public static class InspectorData {
        public Player player;
        public UUID playerUuid;
        public Player target;
        public UUID targetUuid;
        public Location origin;
        public GameMode gamemode;
        public Long time;

        public InspectorData(Player player, Player target) {
            this.player = player;
            this.target = target;
            origin = player.getLocation();
            gamemode = player.getGameMode();
            time = Instant.now().getEpochSecond();
        }

        public InspectorData(UUID playerUuid, UUID targetUuid, Location origin, GameMode gamemode, long time) {
            this.playerUuid = playerUuid;
            this.targetUuid = targetUuid;
            this.origin = origin;
            this.gamemode = gamemode;
            this.time = time;
        }
    }

    private void remove(Player player, InspectorData data, String message, Map<String, String> placeholders) {
        Logger.warn(player.getName());
        if (player.hasMetadata("panickinspector")) {
            player.removeMetadata("panickinspector", Core.getPlugin());
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
        ERROR;
    }
}
