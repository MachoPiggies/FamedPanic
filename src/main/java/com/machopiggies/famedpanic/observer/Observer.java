package com.machopiggies.famedpanic.observer;

import com.google.common.collect.Lists;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public abstract class Observer implements Listener {
    private boolean activated;
    private boolean stopped;

    public void activate(Plugin plugin) {
        if (!activated) {
            stopped = false;
            try {
                onActivate();
            } catch (Exception e) {
                File file = Logger.createErrorLog(e, "startup");
                Logger.severe("An error occurred whilst loading an observer, this will cause functionality issues with " + Core.getPlugin().getDescription().getName() + " v" + Core.getPlugin().getDescription().getVersion() + ". Contact the plugin developer if this persists with the error log created. [Created error log at " + file.getPath() + "]");
                return;
            }

            if (stopped) {
                Logger.severe(this.getClass().getSimpleName() + " has disabled itself. This is probably due to some sort of error on the front end of the plugin. If this perists, please DM the plugin developer. This may/will cause functionality issues with the plugin.");
                return;
            }

            try {
                Bukkit.getPluginManager().registerEvents(this, plugin);
            } catch (Exception e) {
                File file = Logger.createErrorLog(e, "listener application");
                Logger.severe("An error occurred whilst loading an observer, this will cause functionality issues with " + Core.getPlugin().getDescription().getName() + " v" + Core.getPlugin().getDescription().getVersion() + ". Contact the plugin developer if this persists with the error log created. [Created error log at " + file.getPath() + "]");
                return;
            }
            activated = true;
        }
    }

    public void deactivate() {
        if (activated) {
            HandlerList.unregisterAll(this);
            try {
                onDeactivate();
            } catch (Exception e) {
                File file = Logger.createErrorLog(e, "observer disable");
                Logger.severe("An error occurred whilst disabling an observer, this should not cause any issues with your server, but may still need to be reported to the plugin developer. [Created error log at " + file.getPath() + "]");
            }
        }
    }

    protected void onActivate() { }
    protected void onDeactivate() { }

    public boolean isActivated() {
        return activated;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        stopped = true;
    }

    public static void activate(Plugin plugin, List<Observer> observers) {
        for (Observer observer : observers) {
            observer.activate(plugin);
        }
    }

    public static void deactivate(List<Observer> observers) {
        if (observers != null) {
            for (Observer observer : Lists.reverse(observers)) {
                if (observer != null) {
                    observer.deactivate();
                }
            }
        }
    }
}
