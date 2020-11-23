package com.machopiggies.famedpanic;

import com.machopiggies.famedpanic.util.Config;
import org.bukkit.plugin.java.JavaPlugin;

public class Core extends JavaPlugin {

    private static Core core;

    @Override
    public void onEnable() {
        core = this;

        Config.intialize();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Core getPlugin() {
        return core;
    }
}
