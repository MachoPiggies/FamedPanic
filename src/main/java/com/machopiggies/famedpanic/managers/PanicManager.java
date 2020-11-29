package com.machopiggies.famedpanic.managers;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.*;

public class PanicManager extends Observer {

    private Set<PanicData> panicking;
    private RepeatingTask task;
    private Map<UUID, Pair<Pair<Long, Long>, RepeatingTask>> cooldowns;

    @Override
    public void onActivate() {
        panicking = new HashSet<>();
        task = new RepeatingTask();
        cooldowns = new HashMap<>();

        if (Config.settings.showTitle) {
            BaseComponent[] title = TextComponent.fromLegacyText(Config.getConfig().getString("title.title", "&c&lPANIC"));
            BaseComponent[] subtitle = TextComponent.fromLegacyText(Config.getConfig().getString("title.subtitle", "&eA staff member will be with you shortly!"));
            String titleText = ComponentSerializer.toString(title);
            titleText = titleText.replace("u0026", "u00a7");

            String subtitleText = ComponentSerializer.toString(subtitle);
            subtitleText = subtitleText.replace("u0026", "u00a7");

            String finalTitleText = titleText;
            String finalSubtitleText = subtitleText;

            task.setTask(Bukkit.getScheduler().runTaskTimer(Core.getPlugin(), () -> {
                Set<PanicData> local = new HashSet<>(panicking);
                for (PanicData data : local) {
                    PacketManager.sendTitle(data.player, finalTitleText, finalSubtitleText, 0, 45, 0);
                }
            }, 0, 40));
        }
    }

    @Override
    public void onDeactivate() {
        task.cancel();
        panicking = null;
        cooldowns = null;
        task = null;
    }

    public void protect(PanicData data) {
        panicking.add(data);
        if (!Config.isSafemode()) {
            Core.getContactManager().enterAnnounce(data);
        }
        Message.send(data.player, Config.isSafemode() ? Message.msgs.enabledSafemode : Message.msgs.enabled);
        data.player.setMetadata("panicking", new FixedMetadataValue(Core.getPlugin(), data.time));
        data.player.setWalkSpeed(0);
        data.player.setFlySpeed(0);
        data.player.setAllowFlight(true);
        data.player.setFlying(true);
    }

    @Deprecated
    public void unprotect(PanicData data, CommandSender remover) {
        panicking.remove(data);
        if (!Config.isSafemode()) {
            Core.getContactManager().exitAnnounce(data);
        }
        if (remover.equals(data.player)) {
            Message.send(data.player, Message.msgs.disabled);
        } else {
            Message.send(data.player, Message.msgs.staffDisabled);
        }
        if (data.player.hasMetadata("panicking")) {
            data.player.removeMetadata("panicking", Core.getPlugin());
        }
        data.player.setWalkSpeed(data.settings.speed);
        data.player.setFlySpeed(data.settings.flyspeed);
        data.player.setFlying(data.settings.flying);
        data.player.setAllowFlight(data.settings.allowedFlying);

        if (Config.settings.panicInspector.enabled) {
            for (PanicInspectorManager.InspectorData inspector : Core.getPanicInspectorManager().getInspectors().values()) {
                if (inspector.target.equals(data.player)) {
                    if (Core.getPanicInspectorManager().isInspector(inspector.player)) {
                        Core.getPanicInspectorManager().removeInspector(inspector.player, inspector, PanicInspectorManager.RemoveReason.PANIC_CANCELLED);
                    }
                }
            }
        }
    }

    public void unprotect(Player player, CommandSender remover) {
        PanicData data = null;
        for (PanicData dat : panicking) {
            if (dat.player.equals(player)) {
                data = dat;
            }
        }
        data = data != null ? data : new PanicData(player, -1);
        if (!Config.isSafemode()) {
            Core.getContactManager().exitAnnounce(data);
        }
        if (remover != null && remover.equals(data.player)) {
            Message.send(data.player, Message.msgs.disabled);
        } else {
            Message.send(data.player, Message.msgs.staffDisabled);
        }
        if (data.player.hasMetadata("panicking")) {
            data.player.removeMetadata("panicking", Core.getPlugin());
        }
        panicking.removeIf(entry -> entry.player.equals(player));
        data.player.setWalkSpeed(data.settings.speed);
        data.player.setFlySpeed(data.settings.flyspeed);
        data.player.setFlying(data.settings.flying);
        data.player.setAllowFlight(data.settings.allowedFlying);

        if (Config.settings.panicInspector.enabled) {
            for (PanicInspectorManager.InspectorData inspector : Core.getPanicInspectorManager().getInspectors().values()) {
                if (inspector.target.equals(data.player)) {
                    if (Core.getPanicInspectorManager().isInspector(inspector.player)) {
                        if (remover != null) {
                            Core.getPanicInspectorManager().removeInspector(inspector.player, inspector, PanicInspectorManager.RemoveReason.PANIC_CANCELLED);
                        }
                    }
                }
            }
        }
    }

    public void emergencyResetAll() {
        for (PanicData data : panicking) {
            Message.send(data.player, Message.msgs.forcedOut);
            unprotect(data.player, null);
        }
        for (Map.Entry<UUID, Pair<Pair<Long, Long>, RepeatingTask>> entry : cooldowns.entrySet()) {
            entry.getValue().getSecond().cancel();
            cooldowns.remove(entry.getKey(), entry.getValue());
        }
    }

    public boolean panicking(Player player) {
        for (PanicData data : panicking) {
            if (data.player.equals(player)) {
                return true;
            }
        }
        return false;
    }

    public PanicData panickingObj(Player player) {
        for (PanicData data : panicking) {
            if (data.player.equals(player)) {
                return data;
            }
        }
        return null;
    }

    public long getCooldownLength(UUID uuid) {
        return cooldowns.containsKey(uuid) ? cooldowns.get(uuid).getFirst().getFirst() : 0L;
    }

    public long getCooldownExpiry(UUID uuid) {
        return cooldowns.containsKey(uuid) ? cooldowns.get(uuid).getFirst().getSecond() : 0L;
    }

    public void setCooldown(UUID uuid, long time) {
        if (cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).getSecond().cancel();
        }
        RepeatingTask task = new RepeatingTask();
        task.setTask(Bukkit.getScheduler().runTaskLater(Core.getPlugin(), () -> removeCooldown(uuid), time * 20));
        cooldowns.put(uuid, new LinkedPair<>(new LinkedPair<>(time, Instant.now().getEpochSecond() + time), task));

        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isValid() && target.isOnline()) {
            Map<String, String> map = new HashMap<>();
            map.put("{%COOLDOWN_EXPIRE%}", TimeDateUtil.getSimpleDurationStringFromSeconds(time));
            Message.send(target, Message.msgs.cooldownAdd, map);
        }
    }

    public void addCooldown(UUID uuid, long time) {
        long initialTime = 0L;
        if (cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).getSecond().cancel();
            initialTime = cooldowns.get(uuid).getFirst().getFirst();
        }
        RepeatingTask task = new RepeatingTask();
        task.setTask(Bukkit.getScheduler().runTaskLater(Core.getPlugin(), () -> removeCooldown(uuid), time * 20));
        cooldowns.put(uuid, new LinkedPair<>(new LinkedPair<>(initialTime + time, Instant.now().getEpochSecond() + initialTime + time), task));

        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isValid() && target.isOnline()) {
            Map<String, String> map = new HashMap<>();
            map.put("{%COOLDOWN_EXPIRE%}", TimeDateUtil.getSimpleDurationStringFromSeconds(time));
            Message.send(target, Message.msgs.cooldownAdd, map);
        }
    }

    public void removeCooldown(UUID uuid) {
        if (cooldowns.containsKey(uuid)) {
            cooldowns.get(uuid).getSecond().cancel();
        }
        cooldowns.remove(uuid);

        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isValid() && target.isOnline()) {
            Message.send(target, Message.msgs.cooldownExpire);
        }
    }

    public boolean isOnCooldown(UUID uuid) {
        return cooldowns.containsKey(uuid);
    }
}
