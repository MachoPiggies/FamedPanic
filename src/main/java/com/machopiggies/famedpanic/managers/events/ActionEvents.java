package com.machopiggies.famedpanic.managers.events;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.EventListener;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActionEvents extends EventListener {

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (Core.getPanicManager().panickingObj(event.getPlayer()) != null) {
            event.getPlayer().setAllowFlight(true);
            event.getPlayer().setFlying(true);
            if (Objects.requireNonNull(Core.getPanicManager().panickingObj(event.getPlayer())).location.distance(event.getTo()) > 0.2) {
                if (prefs.disableMovement) {
                    event.getPlayer().teleport(Objects.requireNonNull(Core.getPanicManager().panickingObj(event.getPlayer())).location);
                }
            }
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (Core.getPanicManager().panicking((Player) event.getWhoClicked())) {
                if (prefs.stopInventoryMoving) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (Core.getPanicManager().panicking((Player) event.getWhoClicked())) {
                if (prefs.stopInventoryMoving) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (Core.getPanicManager().panicking((Player) event.getDamager())) {
                if (prefs.stopDamager) {
                    event.setCancelled(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("{%TARGET_NAME%}", event.getEntity().getName());
                    map.put("{%TARGET_DISPLAYNAME%}", event.getEntity().getName());
                    Message.send(event.getDamager(), msgs.noDamager, map);
                }
            }
        }
        if (event.getEntity() instanceof Player) {
            if (Core.getPanicManager().panicking((Player) event.getEntity())) {
                if (prefs.stopDamagee) {
                    event.setCancelled(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("{%TARGET_NAME%}", event.getEntity().getName());
                    map.put("{%TARGET_DISPLAYNAME%}", event.getEntity().getName());
                    Message.send(event.getDamager(), msgs.noDamagee, map);
                }
            }
        }
    }

    @EventHandler
    private void onToggleFlight(PlayerToggleFlightEvent event) {
        if (Core.getPanicManager().panicking(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onCommandReprocess(PlayerCommandPreprocessEvent event) {
        if (Core.getPanicManager().panicking(event.getPlayer())) {
            boolean block = false;
            String cmd = event.getMessage().split(" ")[0];
            for (Command command : prefs.stopCommands) {
                if (("/" + command.getName()).equalsIgnoreCase(cmd)) {
                    block = true;
                    break;
                }
                for (String alias : command.getAliases()) {
                    if (("/" + alias).equalsIgnoreCase(cmd)) {
                        block = true;
                        break;
                    }
                }
            }
            if (block) {
                event.setCancelled(true);
                Message.send(event.getPlayer(), msgs.noCommands);
            }
        }
    }

    @EventHandler
    private void onHungerLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            if (Core.getPanicManager().panicking((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }
}
