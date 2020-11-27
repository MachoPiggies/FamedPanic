package com.machopiggies.famedpanic.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonReader;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.EventListener;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactManager extends Observer {

    public void enterAnnounce(PanicData data) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("famedpanic.alerts.receive")) {
                Map<String, String> map = new HashMap<>();
                map.put("{%PLAYER_NAME%}", data.player.getName());
                map.put("{%PLAYER_DISPLAYNAME%}", data.player.getName());
                if (Config.settings.allowStaffTeleport && Config.settings.panicInspector.enabled) {
                    BaseComponent[] base = TextComponent.fromLegacyText(Message.format(Message.msgs.announceEnter, map));
                    BaseComponent[] btn = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.msgs.announceEnterInspector));
                    BaseComponent[] space = new BaseComponent[]{new TextComponent(" ")};
                    BaseComponent[] hover = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.msgs.announceEnterInspectorHover));
                    for (BaseComponent comp : btn) {
                        TextComponent tc = (TextComponent) comp;
                        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/panicinspector " + data.player.getName()));
                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                    }
                    Message.send(player, base, space, btn);
                } else {
                    Message.send(player, Message.msgs.announceEnter, map);
                }
            }
        }
        if (Config.authPrefs.discord.enabled) {
            if (Config.authPrefs.discord.useEmbed) {
                String json = "undefined";
                try {
                    json = new JsonParser().parse(new JsonReader(new FileReader(Config.getDEJson()))).toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.discord);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            } else {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("content", Config.authPrefs.discord.embedAltEnter);
                String json = new Gson().toJson(jsonObj);
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.discord);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            }
        }
    }

    public void exitAnnounce(PanicData data) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("famedpanic.alerts.receive")) {
                Map<String, String> map = new HashMap<>();
                map.put("{%PLAYER_NAME%}", data.player.getName());
                map.put("{%PLAYER_DISPLAYNAME%}", data.player.getName());
                Message.send(player, Message.msgs.announceLeave, map);
            }
        }
        if (Config.authPrefs.discord.enabled) {
            if (Config.authPrefs.discord.useEmbed) {
                String json = "undefined";
                try {
                    json = new JsonParser().parse(new JsonReader(new FileReader(Config.getDLJson()))).toString();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.discord);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            } else {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("content", Config.authPrefs.discord.embedAltLeave);
                String json = new Gson().toJson(jsonObj);
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.discord);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            }
        }
    }

    private String convertPlaceholders(String input, PanicData data) {
        input = input.replace("{%PLAYER_NAME%}", data.player.getName());
        input = input.replace("{%PLAYER_UUID%}", data.player.getUniqueId().toString());
        input = input.replace("{%PLAYER_DISPLAY_NAME%}", ChatColor.stripColor(data.player.getDisplayName()));
        input = input.replace("{%PLAYER_FACE%}", MiscUtil.getPlayerFace(data.player.getName()));
        input = input.replace("{%VOLATILE_BUNGEE_SERVER%}", Config.settings.bungee ? Bukkit.getServer().getName() : "");
        input = input.replace("{%VOLATILE_BUNGEE_COMMAND%}", Config.settings.bungee ? "/server " + Bukkit.getServer().getName() : "");
        input = input.replace("{%TIMESTAMP%}", new Timestamp(new Date().getTime()).toString());
        input = input.replace("{%SERVER_IMAGE%}", Message.serverInfo.image);
        input = input.replace("{%SERVER_NAME%}", Message.serverInfo.name);
        input = input.replace("{%BUNGEE_SERVER_NAME%}", Config.settings.bungee ? Bukkit.getServerName() : "Server is not bungee");
        input = input.replace("{%BUNGEE_SERVER_COMMAND%}", Config.settings.bungee ? Message.serverInfo.transferCommand + " " + Bukkit.getServerName() : "Server is not bungee");
        input = input.replace("{%TIME%}", TimeDateUtil.getTimeAndDateFromEpoch(Instant.now().getEpochSecond()));
        input = input.replace("{%WORLD%}", data.player.getWorld().getName());
        input = input.replace("{%LOCATION_X%}", Double.toString(MiscUtil.round(data.player.getLocation().getX(), 2)));
        input = input.replace("{%LOCATION_Y%}", Double.toString(MiscUtil.round(data.player.getLocation().getX(), 2)));
        input = input.replace("{%LOCATION_Z%}", Double.toString(MiscUtil.round(data.player.getLocation().getX(), 2)));
        return input;
    }
}
