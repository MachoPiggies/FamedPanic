package com.machopiggies.famedpanic.observer;

import com.google.common.reflect.ClassPath;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListenerUtil extends Observer {
    private Map<Class<? extends Plugin>, List<Observer>> observers;

    @Override
    public void onActivate() {
        observers = new HashMap<>();
    }

    public void registerListeners(Plugin plugin) {
        List<Observer> observers = getObservers(plugin);
        this.observers.put(plugin.getClass(), observers);

        Observer.activate(plugin, observers);
    }

    @Override
    public void onDeactivate() {
        for (Class<? extends Plugin> pluginClass : observers.keySet()) {
            Observer.deactivate(observers.get(pluginClass));
        }

        observers = null;
    }

    @SuppressWarnings({"beta", "UnstableApiUsage"})
    private List<Observer> getObservers(Plugin plugin) {
        List<Observer> observers = new ArrayList<>();

        Package p = plugin.getClass().getPackage();

        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(p.getName())) {
                Class<?> clss = info.load();
                if (EventListener.class.equals(clss.getSuperclass())) {
                    try {
                        observers.add((Observer) clss.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        File file = Logger.createErrorLog(e, "observer instantiation stress");
                        Logger.severe("An error occurred whilst loading an observer, this will cause functionality issues with " + Core.getPlugin().getDescription().getName() + " v" + Core.getPlugin().getDescription().getVersion() + ". Contact the plugin developer if this persists with the error log created. [Created error log at " + file.getPath() + "]");
                    }
                }
            }
        } catch (IOException e) {
            File file = Logger.createErrorLog(e, "classpath not found");
            Logger.severe("A critical error has occurred which has severely affected functionality of this plugin. For this reason, the plugin has disabled itself. Please report this to the plugin developer immediately with the following log! [Created error log at " + file.getPath() + "]");
            Bukkit.getPluginManager().disablePlugin(Core.getPlugin());
        }

        return observers;
    }
}
