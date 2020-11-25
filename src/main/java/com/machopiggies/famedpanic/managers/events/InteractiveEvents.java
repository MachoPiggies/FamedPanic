package com.machopiggies.famedpanic.managers.events;

import com.machopiggies.famedpanic.managers.PanicManager;
import com.machopiggies.famedpanic.observer.EventListener;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.*;

import java.util.EnumSet;
import java.util.Set;

public class InteractiveEvents extends EventListener {

    private final Set<Material> containers = EnumSet.of(
            Material.CHEST,
            Material.DROPPER,
            Material.HOPPER,
            Material.DISPENSER,
            Material.TRAPPED_CHEST,
            Material.BREWING_STAND,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.CAULDRON
    );

    private final Set<Material> badItems = EnumSet.of(
            Material.FLINT_AND_STEEL,
            Material.FIREWORK,
            Material.FIREBALL,
            Material.FISHING_ROD,
            Material.EYE_OF_ENDER,
            Material.BREWING_STAND,
            Material.FURNACE,
            Material.BURNING_FURNACE,
            Material.DRAGON_EGG,
            Material.BOAT,
            Material.MINECART,
            Material.COMMAND_MINECART,
            Material.EXPLOSIVE_MINECART,
            Material.HOPPER_MINECART,
            Material.POWERED_MINECART,
            Material.STORAGE_MINECART,
            Material.ENDER_PEARL,
            Material.ENDER_PORTAL_FRAME,
            Material.ARMOR_STAND,
            Material.EGG,
            Material.CAKE,
            Material.CAKE_BLOCK
        );

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            if (prefs.stopWorldInteraction) {
                event.setCancelled(true);
                Message.send(event.getPlayer(), msgs.noBlockBreak);
            }
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            if (prefs.stopWorldInteraction) {
                event.setCancelled(true);
                Message.send(event.getPlayer(), msgs.noBlockPlace);
            }
        }
    }

    @EventHandler
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            if (prefs.stopWorldInteraction) {
                event.setCancelled(true);
                Message.send(event.getPlayer(), msgs.noBlockBreak);
            }
        }
    }

    @EventHandler
    private void onBucketFill(PlayerBucketFillEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            if (prefs.stopWorldInteraction) {
                event.setCancelled(true);
                Message.send(event.getPlayer(), msgs.noBlockPlace);
            }
        }
    }

    @EventHandler
    private void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getAttacker() instanceof Player) {
            if (PanicManager.panicking((Player) event.getAttacker())) {
                if (prefs.stopWorldInteraction) {
                    event.setCancelled(true);
                    Message.send(event.getAttacker(), msgs.noBlockBreak);
                }
            }
        }
    }

    @EventHandler
    private void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getAttacker() instanceof Player) {
            if (PanicManager.panicking((Player) event.getAttacker())) {
                if (prefs.stopWorldInteraction) {
                    event.setCancelled(true);
                    Message.send(event.getAttacker(), msgs.noBlockBreak);
                }
            }
        }
    }

    @EventHandler
    private void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            if (PanicManager.panicking((Player) event.getEntered())) {
                if (prefs.stopWorldInteraction) {
                    event.setCancelled(true);
                    Message.send(event.getEntered(), msgs.noVehicleUse);
                }
            }
        }
    }

    @EventHandler
    private void onVehicleCollision(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player) {
            if (PanicManager.panicking((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player) {
            if (PanicManager.panicking((Player) event.getTarget())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onBedEnter(PlayerBedEnterEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            if (prefs.stopWorldInteraction) {
                event.setCancelled(true);
                Message.send(event.getPlayer(), msgs.noMisc);
            }
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if ((event.getClickedBlock() != null && (badItems.contains(event.getClickedBlock().getType()) || (prefs.stopOpening && containers.contains(event.getClickedBlock().getType())))) || (event.getItem() != null && badItems.contains(event.getItem().getType()))) {
            if (PanicManager.panicking(event.getPlayer())) {
                if (prefs.stopWorldInteraction) {
                    event.setCancelled(true);
                    Message.send(event.getPlayer(), msgs.noOpen);
                }
            }
        }
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            if (prefs.stopDropping) {
                event.setCancelled(true);
                Message.send(event.getPlayer(), msgs.noDrop);
            }
        }
    }

    @EventHandler
    private void onPickupItem(PlayerPickupItemEvent event) {
        if (PanicManager.panicking(event.getPlayer())) {
            if (prefs.stopPickup) {
                event.setCancelled(true);
            }
        }
    }
}
