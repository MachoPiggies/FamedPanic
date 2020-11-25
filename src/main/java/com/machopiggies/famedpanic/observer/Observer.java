package com.machopiggies.famedpanic.observer;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Observer implements Listener {
    private boolean activated;
    private boolean stopped;

    public void activate(Plugin plugin) {
        if (!activated) {
            stopped = false;
            activated = true;
            try {
                onActivate();
            } catch (Exception e) {
                stop();
                e.printStackTrace();
            }

            if (stopped) {
                deactivate();
                return;
            }
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    public void deactivate() {
        if (activated) {
            HandlerList.unregisterAll(this);
            try {
                onDeactivate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            activated = false;
        }
    }

    protected void stop() {
        stopped = true;
    }

    protected boolean isStopped() {
        return stopped;
    }

    protected void onActivate() {
    }

    protected void onDeactivate() {
    }

    public boolean isActivated() {
        return activated;
    }
}
