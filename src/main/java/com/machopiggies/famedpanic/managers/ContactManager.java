package com.machopiggies.famedpanic.managers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.managers.bungee.BungeeMessageEvent;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContactManager extends Observer {

    @EventHandler
    private void onBungeeContact(BungeeMessageEvent event) {
        if (event.getSubChannel().equals("PanicEnter")) {
            JsonObject obj;
            OfflinePlayer target;
            try {
                obj = new JsonParser().parse(event.getData()).getAsJsonObject();
                target = Bukkit.getOfflinePlayer(UUID.fromString(obj.get("uuid").getAsString()));
            } catch (Exception e) {
                File file = Logger.createErrorLog(e, "json parsing error");
                Logger.severe("An error occurred whilst parsing an incoming bungee transmission, this has caused an essential operation to be aborted. Please contact the plugin developer with the following log. [Created error log at " + file.getPath() + "]");
                return;
            }

            if (!obj.get("serverId").getAsString().equals(Bukkit.getServerId())) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("famedpanic.alerts.receive")) {
                        Map<String, String> map = new HashMap<>();
                        map.put("{%PLAYER_NAME%}", target.getName());
                        map.put("{%PLAYER_DISPLAYNAME%}", ChatColor.translateAlternateColorCodes('&', obj.get("displayName").getAsString()));

                        Map<String, String> map1 = new HashMap<>();
                        map1.put("{%BUNGEE_SERVER%}", obj.get("server").getAsString());

                        if (Config.settings.allowStaffTeleport && Config.settings.panicInspector.enabled && player.hasPermission("famedpanic.inspector")) {
                            BaseComponent[] base = TextComponent.fromLegacyText(Message.format(Message.msgs.announceEnter, map));
                            BaseComponent[] bungee = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.format(Message.msgs.announceEnterBungee, map1)));
                            BaseComponent[] btn = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.msgs.announceEnterInspector));
                            BaseComponent[] space = new BaseComponent[]{new TextComponent(" ")};
                            BaseComponent[] hover = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.msgs.announceEnterInspectorHover));
                            BaseComponent[] hover1 = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Message.msgs.announceEnterBungeeHover));
                            for (BaseComponent comp : btn) {
                                TextComponent tc = (TextComponent) comp;
                                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/panicinspector " + player.getName()));
                                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
                            }
                            for (BaseComponent comp : bungee) {
                                TextComponent tc = (TextComponent) comp;
                                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + obj.get("server").getAsString()));
                                tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover1));
                            }
                            Message.send(player, base, space, bungee, space, btn);
                        } else {
                            Message.send(player, Message.msgs.announceEnter, map);
                        }
                    }
                }
            }
        } else if (event.getSubChannel().equals("PanicLeave")) {
            JsonObject obj;
            OfflinePlayer target;
            try {
                obj = new JsonParser().parse(event.getData()).getAsJsonObject();
                target = Bukkit.getOfflinePlayer(UUID.fromString(obj.get("uuid").getAsString()));
            } catch (Exception e) {
                File file = Logger.createErrorLog(e, "json parsing error");
                Logger.severe("An error occurred whilst parsing an incoming bungee transmission, this has caused an essential operation to be aborted. Please contact the plugin developer with the following log. [Created error log at " + file.getPath() + "]");
                return;
            }

            if (!obj.get("serverId").getAsString().equals(Bukkit.getServerId())) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("famedpanic.alerts.receive")) {
                        Map<String, String> map = new HashMap<>();
                        map.put("{%PLAYER_NAME%}", target.getName());
                        map.put("{%PLAYER_DISPLAYNAME%}", ChatColor.translateAlternateColorCodes('&', obj.get("displayName").getAsString()));
                        Message.send(player, Message.msgs.announceLeave, map);
                    }
                }
            }
        }
    }

    public void enterAnnounce(PanicData data) {
        if (Config.settings.bungee) {
            JsonObject obj = new JsonObject();
            obj.addProperty("serverId", Bukkit.getServerId());
            obj.addProperty("server", Bukkit.getServer().getName());
            obj.addProperty("uuid", data.uuid.toString());
            obj.addProperty("displayName", data.player.getDisplayName());

            Core.getBungeeManager().sendBungeeForwardMessage("PanicEnter", obj.toString(), data.player);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("famedpanic.alerts.receive")) {
                Map<String, String> map = new HashMap<>();
                map.put("{%PLAYER_NAME%}", data.player.getName());
                map.put("{%PLAYER_DISPLAYNAME%}", data.player.getName());
                if (Config.settings.allowStaffTeleport && Config.settings.panicInspector.enabled && player.hasPermission("famedpanic.inspector")) {
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
                    File file = Logger.createErrorLog(e, "json parse error");
                    Logger.severe("An error occurred whilst trying to parse '" + Config.getDEJson().getPath() + "'. Please try validating your JSON @ https://jsonformatter.curiousconcept.com/ or contact the plugin developer with the following log and your JSON file. [Created error log at " + file.getPath() + "]");
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
        if (Config.authPrefs.slack.enabled) {
            if (Config.authPrefs.slack.useBlock) {
                String json = "undefined";
                try {
                    json = new JsonParser().parse(new JsonReader(new FileReader(Config.getSEJson()))).toString();
                } catch (FileNotFoundException e) {
                    File file = Logger.createErrorLog(e, "json parse error");
                    Logger.severe("An error occurred whilst trying to parse '" + Config.getSEJson().getPath() + "'. Please try validating your JSON @ https://jsonformatter.curiousconcept.com/ or contact the plugin developer with the following log and your JSON file. [Created error log at " + file.getPath() + "]");
                }
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.slack);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            } else {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("text", Config.authPrefs.slack.blockAltEnter);
                String json = new Gson().toJson(jsonObj);
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.slack);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            }
        }
    }

    public void exitAnnounce(PanicData data) {
        if (Config.settings.bungee) {
            JsonObject obj = new JsonObject();
            obj.addProperty("serverId", Bukkit.getServerId());
            obj.addProperty("server", Bukkit.getServer().getName());
            obj.addProperty("uuid", data.uuid.toString());
            obj.addProperty("displayName", data.player.getDisplayName());

            Core.getBungeeManager().sendBungeeForwardMessage("PanicLeave", obj.toString(), data.player);
        }
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
                    File file = Logger.createErrorLog(e, "json parse error");
                    Logger.severe("An error occurred whilst trying to parse '" + Config.getDLJson().getPath() + "'. Please try validating your JSON @ https://jsonformatter.curiousconcept.com/ or contact the plugin developer with the following log and your JSON file. [Created error log at " + file.getPath() + "]");
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
        if (Config.authPrefs.slack.enabled) {
            if (Config.authPrefs.slack.useBlock) {
                String json = "undefined";
                try {
                    json = new JsonParser().parse(new JsonReader(new FileReader(Config.getSLJson()))).toString();
                } catch (FileNotFoundException e) {
                    File file = Logger.createErrorLog(e, "json parse error");
                    Logger.severe("An error occurred whilst trying to parse '" + Config.getSLJson().getPath() + "'. Please try validating your JSON @ https://jsonformatter.curiousconcept.com/ or contact the plugin developer with the following log and your JSON file. [Created error log at " + file.getPath() + "]");
                }
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.slack);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            } else {
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("text", Config.authPrefs.slack.blockAltLeave);
                String json = new Gson().toJson(jsonObj);
                json = convertPlaceholders(json, data);
                HTTPRequest http = new HTTPRequest(Config.auth.slack);
                http.addBody(new JsonParser().parse(json).getAsJsonObject());
                http.sendPOST();
            }
        }
    }

    private String convertPlaceholders(String input, PanicData data) {
        input = input.replace("{%PLAYER_NAME%}", data.player.getName());
        input = input.replace("{%PLAYER_UUID%}", data.player.getUniqueId().toString());
        input = input.replace("{%PLAYER_DISPLAYNAME%}", ChatColor.stripColor(data.player.getDisplayName()));
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
