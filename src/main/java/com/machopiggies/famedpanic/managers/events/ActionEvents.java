package com.machopiggies.famedpanic.managers.events;

import com.machopiggies.famedpanic.managers.PanicManager;
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

import java.util.Objects;

public class ActionEvents extends EventListener {

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (PanicManager.panickingObj(event.getPlayer()) != null) {
            if (Objects.requireNonNull(PanicManager.panickingObj(event.getPlayer())).location.distance(event.getTo()) > 0.2) {
                if (prefs.disableMovement) {
                    event.getPlayer().teleport(Objects.requireNonNull(PanicManager.panickingObj(event.getPlayer())).location);
                }
            }
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (PanicManager.panicking((Player) event.getWhoClicked())) {
                if (prefs.stopInventoryMoving) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            if (PanicManager.panicking((Player) event.getWhoClicked())) {
                if (prefs.stopInventoryMoving) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (PanicManager.panicking((Player) event.getDamager())) {
                if (prefs.stopDamager) {
                    event.setCancelled(true);
                }
            }
            Message.send(event.getDamager(), msgs.noDamager);
        }
        if (event.getEntity() instanceof Player) {
            if (PanicManager.panicking((Player) event.getDamager())) {
                if (prefs.stopDamagee) {
                    event.setCancelled(true);
                }
            }
            Message.send(event.getDamager(), msgs.noDamagee);
        }
    }

    @EventHandler
    private void onToggleFlight(PlayerToggleFlightEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onCommandReprocess(PlayerCommandPreprocessEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
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
            if (PanicManager.panicking((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }
}
