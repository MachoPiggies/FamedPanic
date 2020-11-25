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

import java.util.HashSet;
import java.util.Set;

public class PanicManager extends Observer {

    private static Set<PanicData> panicking;
    private static RepeatingTask task;

    @Override
    public void onActivate() {
        panicking = new HashSet<>();

        task = new RepeatingTask();

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

    @Override
    public void onDeactivate() {
        for (PanicData data : panicking) {
            Message.send(data.player, Message.msgs.forcedOut);
        }
        panicking = null;
    }

    public void protect(PanicData data) {
        panicking.add(data);
        if (!Config.isSafemode()) {
            Core.getContactManager().enterAnnounce(data);
        }
        Message.send(data.player, Message.msgs.enabled);
        data.player.setMetadata("panicking", new FixedMetadataValue(Core.getPlugin(), data.time));
        data.player.setWalkSpeed(0);
        data.player.setFlySpeed(0);
        data.player.setAllowFlight(true);
        data.player.setFlying(true);
    }

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
        if (remover.equals(data.player)) {
            Message.send(data.player, Message.msgs.disabled);
        } else {
            Message.send(data.player, Message.msgs.staffDisabled);
        }
        if (data.player.hasMetadata("panicking")) {
            data.player.removeMetadata("panicking", Core.getPlugin());
        }
        panicking.removeIf(entry -> entry.player.equals(player));
        Logger.severe(data.settings.toString());
        data.player.setWalkSpeed(data.settings.speed);
        data.player.setFlySpeed(data.settings.flyspeed);
        data.player.setFlying(data.settings.flying);
        data.player.setAllowFlight(data.settings.allowedFlying);
    }

    public static boolean panicking(Player player) {
        for (PanicData data : panicking) {
            if (data.player.equals(player)) {
                return true;
            }
        }
        return false;
    }

    public static PanicData panickingObj(Player player) {
        for (PanicData data : panicking) {
            if (data.player.equals(player)) {
                return data;
            }
        }
        return null;
    }

    public static Set<PanicData> getPanicking() {
        return panicking;
    }
}
