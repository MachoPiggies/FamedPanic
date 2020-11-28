package com.machopiggies.famedpanic;

import com.machopiggies.famedpanic.commands.CommandManager;
import com.machopiggies.famedpanic.gui.GuiManager;
import com.machopiggies.famedpanic.managers.ContactManager;
import com.machopiggies.famedpanic.managers.PanicInspectorManager;
import com.machopiggies.famedpanic.managers.PanicManager;
import com.machopiggies.famedpanic.observer.EventListener;
import com.machopiggies.famedpanic.observer.EventListenerUtil;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.observer.ObserverUtil;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Logger;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Core extends JavaPlugin {

    private static Core core;
    private static boolean vaultInc;
    private static List<Observer> observers;

    private static Permission perms = null;
    public static Permission getPerms() {
        return perms;
    }

    public static boolean isVaultInc() {
        return vaultInc;
    }

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

    @Override
    public void onEnable() {
        core = this;

        Plugin vault;
        if ((vault = Bukkit.getPluginManager().getPlugin("Vault")) != null) {
            Logger.debug(vault.getDescription().getName() + " v" + vault.getDescription().getVersion() + " found! Initializing with Vault.");
            vaultInc = true;
        } else {
            Logger.debug("Vault not found, ignoring...");
        }

        if (vaultInc) {
            RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
            perms = rsp.getProvider();
        }

        Config.initialize();
        EventListener.intialize();

        ObserverUtil.activate(this, (observers = Arrays.asList(
                panicManager = new PanicManager(),
                contactManager = new ContactManager(),
                eventListenerUtil = new EventListenerUtil(),
                panicInspectorManager = new PanicInspectorManager(),
                guiManager = new GuiManager()
        )));
        eventListenerUtil.registerListeners(this);
        CommandManager.activateCmds(this);
    }

    @Override
    public void onDisable() {
        panicManager.emergencyResetAll();
        panicInspectorManager.emergencyResetAll();

        ObserverUtil.deactivate(observers);
        panicManager = null;
        contactManager = null;
        eventListenerUtil = null;
        panicInspectorManager = null;
        guiManager = null;
        core = null;
    }

    public static Core getPlugin() {
        return core;
    }
}
