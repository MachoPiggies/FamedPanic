package com.machopiggies.famedpanic.gui.button;

import com.machopiggies.famedpanic.gui.MenuInterface;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface OnClick {
    void onClick(MenuInterface i, InventoryClickEvent e);
}
