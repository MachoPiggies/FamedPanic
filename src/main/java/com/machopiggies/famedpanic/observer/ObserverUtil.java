package com.machopiggies.famedpanic.observer;

import com.google.common.collect.Lists;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class ObserverUtil {

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
