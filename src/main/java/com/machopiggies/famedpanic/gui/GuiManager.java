package com.machopiggies.famedpanic.gui;

import com.machopiggies.famedpanic.observer.Observer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager extends Observer {
    private Map<UUID, MenuInterface> inventories;

    @Override
    public void onActivate() {
        inventories = new HashMap<>();
    }

    @Override
    public void onDeactivate() {
        for (UUID uuid : inventories.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.closeInventory();
            }
        }
        inventories = null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (inventories.containsKey(e.getWhoClicked().getUniqueId()) && e.getWhoClicked() instanceof Player) {
            inventories.get(e.getWhoClicked().getUniqueId()).clicked(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (inventories.containsKey(e.getPlayer().getUniqueId()) && e.getPlayer() instanceof Player) {
            inventories.get(e.getPlayer().getUniqueId()).closed(e);
            removeInventory(e.getPlayer().getUniqueId());
        }
    }

    public void addInventory(UUID uuid, MenuInterface inventory) {
        inventories.put(uuid, inventory);
    }

    public void removeInventory(UUID uuid) {
        inventories.remove(uuid);
    }
}
