package com.machopiggies.famedpanic;

import com.machopiggies.famedpanic.commands.CommandManager;
import com.machopiggies.famedpanic.gui.GuiManager;
import com.machopiggies.famedpanic.managers.*;
import com.machopiggies.famedpanic.managers.bungee.BungeeManager;
import com.machopiggies.famedpanic.observer.EventListener;
import com.machopiggies.famedpanic.observer.EventListenerUtil;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Core extends JavaPlugin {

    private static Core core;
    private static Plugin api;
    private static List<Observer> observers;

    private static PanicManager panicManager;
    public static PanicManager getPanicManager() {
        return panicManager;
    }

    private static ContactManager contactManager;
    public static ContactManager getContactManager() {
        return contactManager;
    }

    private static EventListenerUtil eventListenerUtil;
    public static EventListenerUtil getEventListenerUtil() {
        return eventListenerUtil;
    }

    private static PanicInspectorManager panicInspectorManager;
    public static PanicInspectorManager getPanicInspectorManager() {
        return panicInspectorManager;
    }

    private static GuiManager guiManager;
    public static GuiManager getGuiManager() {
        return guiManager;
    }

    private static BungeeManager bungeeManager;
    public static BungeeManager getBungeeManager() {
        return bungeeManager;
    }

    private static APIManager apiManager;
    public static APIManager getApiManager() {
        return apiManager;
    }

    @Override
    public void onEnable() {
        core = this;

        Config.initialize();
        EventListener.intialize();

        if ((api = Bukkit.getPluginManager().getPlugin("FamedPanicAPI")) != null) {
            Logger.debug(api.getDescription().getName() + " v" + api.getDescription().getVersion() + " found! Attempting to load with API...");
        } else {
            Logger.debug("API not found. Loading as standalone...");
        }

        Observer.activate(this, (observers = Arrays.asList(
                panicManager = new PanicManager(),
                contactManager = new ContactManager(),
                eventListenerUtil = new EventListenerUtil(),
                panicInspectorManager = new PanicInspectorManager(),
                guiManager = new GuiManager()
        )));

        if (api != null) {
            if (Config.getConfig().getBoolean("api.enabled")) {
                Observer.activate(this, Collections.singletonList(apiManager = new APIManager(api)));
                Logger.debug(api.getDescription().getName() + " v" + api.getDescription().getVersion() + " loaded successfully!");
            } else {
                Bukkit.getPluginManager().disablePlugin(api);
                Logger.debug(api.getDescription().getName() + " v" + api.getDescription().getVersion() + " has been disabled due to API usage being turned off! Loading as standalone...");
            }
        }

        if (Config.settings.bungee) {
            Observer.activate(this, Collections.singletonList(bungeeManager = new BungeeManager()));
            Logger.debug(api.getDescription().getName() + " v" + api.getDescription().getVersion() + " hooking onto bungeecord!");
        }

        eventListenerUtil.registerListeners(this);
        CommandManager.activateCmds(this);
    }

    @Override
    public void onDisable() {
        try {
            panicManager.emergencyResetAll();
            panicInspectorManager.emergencyResetAll();
        } catch (Exception ignored) { }

        Observer.deactivate(observers);
        panicManager = null;
        contactManager = null;
        eventListenerUtil = null;
        panicInspectorManager = null;
        guiManager = null;
        apiManager = null;
        core = null;
    }

    public static Core getPlugin() {
        return core;
    }

    public static Plugin getApi() {
        return api;
    }
}
