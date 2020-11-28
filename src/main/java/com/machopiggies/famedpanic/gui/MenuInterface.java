package com.machopiggies.famedpanic.gui;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.gui.button.MenuInterfaceButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class MenuInterface {
    private final Map<Integer, MenuInterfaceButton> buttons;
    private final Inventory inventory;

    private OnClose executeOnClose = null;

    public MenuInterface(String name, int size) {
        inventory = Bukkit.createInventory(null, size, name);
        buttons = new HashMap<>();
    }

    public void launch(Player player) {
        player.closeInventory();
        Core.getGuiManager().addInventory(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    public void launch(CommandSender sender) {
        if (sender instanceof Player) {
            ((Player) sender).closeInventory();
            Core.getGuiManager().addInventory(((Player) sender).getUniqueId(), this);
            ((Player) sender).openInventory(inventory);
        }
    }

    public void add(MenuInterfaceLimb button) {
        if (button instanceof MenuInterfaceButton) {
            if (inventory.firstEmpty() != -1) {
                buttons.put(inventory.firstEmpty(), (MenuInterfaceButton) button);
                inventory.setItem(inventory.firstEmpty(), ((MenuInterfaceButton) button).getItem());
            } else {
                throw new ArrayIndexOutOfBoundsException("gui is full");
            }
        }
    }

    public void set(int slot, MenuInterfaceLimb button) {
        if (button instanceof MenuInterfaceButton) {
            buttons.remove(slot);
            buttons.put(slot, (MenuInterfaceButton) button);
            inventory.setItem(slot, ((MenuInterfaceButton) button).getItem());
        }
    }

    protected void clicked(InventoryClickEvent e) {
        e.setCancelled(true);
        if (buttons.containsKey(e.getRawSlot())) {
            if (buttons.get(e.getRawSlot()).getClick() != null) {
                buttons.get(e.getRawSlot()).getClick().onClick(this, e);
            }
        }
    }

    public void executeOnClose(OnClose e) {
        executeOnClose = e;
    }

    protected void closed(InventoryCloseEvent e) {
        if (executeOnClose != null) {
            executeOnClose.onClose(e);
        }
    }

    public void borderise(StainedMaterialColor color) {
        ItemStack spacer = new ItemStack(Material.STAINED_GLASS_PANE, 1, color.getGlassColorId());
        ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.setDisplayName(ChatColor.BLACK + "_");
        spacer.setItemMeta(spacerMeta);

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, spacer);
            inventory.setItem(i + inventory.getSize() - 9, spacer);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i % 9 == 0 || (i + 1) % 9 == 0) {
                inventory.setItem(i, spacer);
            }
        }
    }

    public ItemStack[] getContents() {
        return inventory.getContents();
    }

    @Deprecated
    public void setContents(ItemStack[] stacks) {
        inventory.setContents(stacks);
    }

    public void clear() {
        inventory.clear();
        buttons.clear();
    }
}
