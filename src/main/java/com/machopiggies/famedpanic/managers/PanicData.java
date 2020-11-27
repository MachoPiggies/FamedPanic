package com.machopiggies.famedpanic.managers;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PanicData {
    public Player player;
    public UUID uuid;
    public long time;
    public Location location;
    public Settings settings;

    public PanicData(Player player, Settings settings) {
        this.player = player;
        uuid = player.getUniqueId();
        time = Instant.now().getEpochSecond();
        location = player.getLocation();
        this.settings = settings;
    }

    public PanicData(Player player, long time) {
        this.player = player;
        uuid = player.getUniqueId();
        this.time = time;
        location = player.getLocation();
        settings = new Settings();
    }

    public PanicData(UUID uuid, long time, Location location, float speed, float flyspeed, boolean flying, boolean allowedFlying) {
        this.uuid = uuid;
        this.time = time;
        this.location = location;
        this.settings = new Settings(
                speed,
                flyspeed,
                flying,
                allowedFlying
        );
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
