package com.machopiggies.famedpanic.observer;

import com.google.common.reflect.ClassPath;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventListenerUtil extends Observer {
    private Map<Class<? extends Plugin>, List<Observer>> observers;

    @Override
    public void onActivate() {
        observers = new ConcurrentHashMap<>();
    }

    public void registerListeners(Plugin plugin) {
        List<Observer> observers = getObservers(plugin);
        this.observers.put(plugin.getClass(), observers);

        assert observers != null;
        ObserverUtil.activate(plugin, observers);
    }

    @Override
    public void onDeactivate() {
        for (Class<? extends Plugin> pluginClass : observers.keySet()) {
            ObserverUtil.deactivate(observers.get(pluginClass));
        }

        observers = null;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        Class<? extends Plugin> pluginClass = e.getPlugin().getClass();

        ObserverUtil.deactivate(observers.get(pluginClass));

        observers.remove(pluginClass);
    }

    @SuppressWarnings({"beta", "UnstableApiUsage"})
    private List<Observer> getObservers(Plugin plugin) {
        List<Observer> Observers = new ArrayList<>();

        ClassPath classPath;
        try {
            classPath = ClassPath.from(plugin.getClass().getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Package p = plugin.getClass().getPackage();

        for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(p.getName())) {
            Class<?> clazz = info.load();
            if (EventListener.class.equals(clazz.getSuperclass())) {
                try {
                    Observers.add((Observer) clazz.getDeclaredConstructor().newInstance());
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return Observers;
    }
}
