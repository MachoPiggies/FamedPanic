package com.machopiggies.famedpanic.managers;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonReader;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContactManager extends Observer {

    public void enterAnnounce(PanicData data) {
        String json = "undefined";
        try {
            json = new JsonParser().parse(new JsonReader(new FileReader(Config.getdJson()))).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        json = convertPlaceholders(json, data);
        HTTPRequest http = new HTTPRequest(Config.auth.discord);
        http.addBody(new JsonParser().parse(json).getAsJsonObject());
        http.sendPOST();
    }

    public void exitAnnounce(PanicData data) {

    }

    private String convertPlaceholders(String input, PanicData data) {
        input = input.replace("{%PLAYER_NAME%}", data.player.getName());
        input = input.replace("{%PLAYER_UUID%}", data.player.getUniqueId().toString());
        input = input.replace("{%PLAYER_DISPLAY_NAME%}", ChatColor.stripColor(data.player.getDisplayName()));
        input = input.replace("{%PLAYER_FACE%}", MiscUtil.getPlayerFace(data.player.getName()));
        input = input.replace("{%VOLATILE_BUNGEE_COMMAND%}", Config.isBungee() ? "/server " + Bukkit.getServer().getName() : "");
        input = input.replace("{%TIMESTAMP%}", new Timestamp(new Date().getTime()).toString());
        input = input.replace("{%SERVER_IMAGE%}", Message.serverInfo.image);
        input = input.replace("{%SERVER_NAME%}", Message.serverInfo.name);
        input = input.replace("{%BUNGEE_SERVER_NAME%}", Config.isBungee() ? Bukkit.getServerName() : "Server is not bungee");
        input = input.replace("{%BUNGEE_SERVER_COMMAND%}", Config.isBungee() ? Message.serverInfo.transferCommand + " " + Bukkit.getServerName() : "Server is not bungee");
        input = input.replace("{%TIME%}", MiscUtil.getTimeAndDateFromEpoch(Instant.now().getEpochSecond()));
        input = input.replace("{%WORLD%}", data.player.getWorld().getName());
        input = input.replace("{%LOCATION_X%}", Double.toString(MiscUtil.round(data.player.getLocation().getX(), 2)));
        input = input.replace("{%LOCATION_Y%}", Double.toString(MiscUtil.round(data.player.getLocation().getX(), 2)));
        input = input.replace("{%LOCATION_Z%}", Double.toString(MiscUtil.round(data.player.getLocation().getX(), 2)));
        return input;
    }
}
