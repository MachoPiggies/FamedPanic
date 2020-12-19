package com.machopiggies.famedpanic.managers.bungee;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeeMessageEvent extends Event {
    private final String subChannel;
    private final String data;

    private static final HandlerList handlers = new HandlerList();

    public BungeeMessageEvent(String subChannel, String data) {
        this.subChannel = subChannel;
        this.data = data;
    }

    public String getSubChannel() {
        return subChannel;
    }

    public String getData() {
        return data;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
