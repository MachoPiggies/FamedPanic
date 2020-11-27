package com.machopiggies.famedpanic.managers.events;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.managers.PanicInspectorManager;
import com.machopiggies.famedpanic.observer.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class MiscEvents extends EventListener {

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (Core.getPanicManager().panicking(event.getPlayer())) {
            Core.getPanicManager().unprotect(event.getPlayer(), event.getPlayer());
        }
        if (Core.getPanicInspectorManager().isInspector(event.getPlayer())) {
            Core.getPanicInspectorManager().removeInspector(event.getPlayer(), Core.getPanicInspectorManager().getInspectors().get(event.getPlayer()), PanicInspectorManager.RemoveReason.QUIT);
        }
    }
}
