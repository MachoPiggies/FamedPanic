package com.machopiggies.famedpanic.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PanicData {
    public Player player;
    public long time;
    public Location location;
    public Settings settings;

    public PanicData(Player player, Settings settings) {
        this.player = player;
        time = Instant.now().getEpochSecond();
        location = player.getLocation();
        this.settings = settings;
    }

    public PanicData(Player player, long time) {
        this.player = player;
        this.time = time;
        location = player.getLocation();
        settings = new Settings();
    }

    public static class Settings {
        public float speed;
        public float flyspeed;
        public boolean flying;
        public boolean allowedFlying;

        public Settings(float speed, float flyspeed, boolean flying, boolean allowedFlying) {
            this.speed = speed;
            this.flyspeed = flyspeed;
            this.flying = flying;
            this.allowedFlying = allowedFlying;
        }

        public Settings() {
            speed = 0.2f;
            flyspeed = 0.1f;
            flying = false;
            allowedFlying = false;
        }

        @Override
        public String toString() {
            Map<String, Object> map = new HashMap<>();
            map.put("speed", speed);
            map.put("flyspeed", flyspeed);
            map.put("flying", flying);
            map.put("allowedFlying", allowedFlying);
            return map.toString();
        }
    }
}

//todo: /panicking, /ispanicking, /cancelpanic, /paniccooldown
