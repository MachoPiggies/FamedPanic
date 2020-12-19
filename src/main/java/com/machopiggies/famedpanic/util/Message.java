package com.machopiggies.famedpanic.util;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.commands.CommandManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Message {

    private static boolean initial = true;

    public static Messages msgs;
    public static ServerInfo serverInfo;

    public static void buildConfig() {

        File file = FileUtil.getFolder("design", Core.getPlugin().getDataFolder());
        Messages md = new Messages();

        Map<String, Object> defaults = new HashMap<>();
        defaults.put("general.prefix", md.prefix);
        defaults.put("general.emergency-prefix", md.emergencyPrefix);
        defaults.put("general.permission-denied", md.noPermission);
        defaults.put("general.missing-arg.player", md.mAPlayer);
        defaults.put("general.safemode.toggle-on", md.setSafemodeOn);
        defaults.put("general.safemode.toggle-off", md.setSafemodeOff);
        defaults.put("general.safemode.on", md.safemodeOn);
        defaults.put("general.safemode.off", md.safemodeOff);
        defaults.put("alerts.announce-enter", md.announceEnter);
        defaults.put("alerts.announce-enter-inspectorclick", md.announceEnterInspector);
        defaults.put("alerts.announce-enter-bungeeclick", md.announceEnterBungee);
        defaults.put("alerts.announce-enter-inspectorclick-hover", md.announceEnterInspectorHover);
        defaults.put("alerts.announce-enter-bungeeclick-hover", md.announceEnterBungeeHover);
        defaults.put("alerts.announce-leave", md.announceLeave);
        defaults.put("alerts.enabled", md.enabled);
        defaults.put("alerts.enabled-safemode", md.enabledSafemode);
        defaults.put("alerts.disabled", md.disabled);
        defaults.put("alerts.staff-disabled", md.staffDisabled);
        defaults.put("alerts.on-cooldown", md.onCooldown);
        defaults.put("alerts.forced-out", md.forcedOut);
        defaults.put("alerts.reset-success", md.resetSuccess);
        defaults.put("alerts.panic-whilst-spec", md.panicWhilstSpec);
        defaults.put("alerts.spec-whilst-panic", md.specWhilstPanic);
        defaults.put("alerts.inspector-enter", md.inspectorEnter);
        defaults.put("alerts.inspector-leave", md.inspectorLeave);
        defaults.put("alerts.inspector-mode-disabled", md.inspectorDisabled);
        defaults.put("alerts.inspector-kick", md.inspectorKick);
        defaults.put("alerts.inspector-arrival", md.inspectorArrival);
        defaults.put("alerts.cooldown-expire", md.cooldownExpire);
        defaults.put("alerts.cooldown-add", md.cooldownAdd);
        defaults.put("alerts.in-panic-mode", md.inPanicMode);
        defaults.put("alerts.not-in-panic-mode", md.notInPanicMode);

        defaults.put("protections.interact.no-open", md.noOpen);
        defaults.put("protections.interact.no-drop", md.noDrop);
        defaults.put("protections.interact.misc", md.noMisc);
        defaults.put("protections.interact.worldinteract.no-block-break", md.noBlockBreak);
        defaults.put("protections.interact.worldinteract.no-block-place", md.noBlockPlace);
        defaults.put("protections.interact.worldinteract.no-vehicle-use", md.noVehicleUse);
        defaults.put("protections.combat.no-damager", md.noDamager);
        defaults.put("protections.combat.no-damagee", md.noDamagee);
        defaults.put("protections.no-command", md.noCommands);
        defaults.put("protections.no-chat", md.noChat);
        defaults.put("commands.cancelpanic.success", md.cmdCPSuccess);
        defaults.put("commands.cancelpanic.notpanicking", md.cmdCPNotPanicking);
        defaults.put("commands.parsing.invalid-number", md.cmdInvalidNumber);
        defaults.put("commands.cooldown.nogui.set", md.cmdCooldownSet);
        defaults.put("commands.cooldown.nogui.remove", md.cmdCooldownRemove);
        defaults.put("commands.cooldown.nogui.unknown", md.cmdCooldownUnknown);
        defaults.put("commands.cooldown.nogui.missing", md.cmdCooldownMissing);
        File messages = FileUtil.getYamlFile("messages.yml", file, defaults);
        FileConfiguration yml = YamlConfiguration.loadConfiguration(messages);

        msgs = new Messages(
                yml.getString("general.prefix", md.prefix),
                yml.getString("general.emergency-prefix", md.emergencyPrefix),
                yml.getString("general.permission-denied", md.noPermission),
                yml.getString("general.missing-arg.player", md.mAPlayer),
                yml.getString("general.safemode.toggle-on", md.setSafemodeOn),
                yml.getString("general.safemode.toggle-off", md.setSafemodeOff),
                yml.getString("general.safemode.on", md.safemodeOn),
                yml.getString("general.safemode.off", md.safemodeOff),
                yml.getString("alerts.announce-enter", md.announceEnter),
                yml.getString("alerts.announce-enter-inspectorclick", md.announceEnterInspector),
                yml.getString("alerts.announce-enter-bungeeclick", md.announceEnterBungee),
                yml.getString("alerts.announce-enter-inspectorclick-hover", md.announceEnterInspectorHover),
                yml.getString("alerts.announce-enter-bungeeclick-hover", md.announceEnterBungeeHover),
                yml.getString("alerts.announce-leave", md.announceLeave),
                yml.getString("alerts.enabled", md.enabled),
                yml.getString("alerts.enabled-safemode", md.enabledSafemode),
                yml.getString("alerts.disabled", md.disabled),
                yml.getString("alerts.staff-disabled", md.staffDisabled),
                yml.getString("alerts.on-cooldown", md.onCooldown),
                yml.getString("alerts.forced-out", md.forcedOut),
                yml.getString("alerts.reset-success", md.resetSuccess),
                yml.getString("alerts.panic-whilst-spec", md.panicWhilstSpec),
                yml.getString("alerts.spec-whilst-panic", md.specWhilstPanic),
                yml.getString("alerts.inspector-enter", md.inspectorEnter),
                yml.getString("alerts.inspector-leave", md.inspectorLeave),
                yml.getString("alerts.inspector-mode-disabled", md.inspectorDisabled),
                yml.getString("alerts.inspector-kick", md.inspectorKick),
                yml.getString("alerts.inspector-arrival", md.inspectorArrival),
                yml.getString("alerts.cooldown-expire", md.cooldownExpire),
                yml.getString("alerts.cooldown-add", md.cooldownAdd),
                yml.getString("alerts.in-panic-mode", md.inPanicMode),
                yml.getString("alerts.not-in-panic-mode", md.notInPanicMode),

                yml.getString("protections.interact.no-open", md.noOpen),
                yml.getString("protections.interact.no-drop", md.noDrop),
                yml.getString("protections.interact.misc", md.noMisc),
                yml.getString("protections.interact.worldinteract.no-block-break", md.noBlockBreak),
                yml.getString("protections.interact.worldinteract.no-block-place", md.noBlockPlace),
                yml.getString("protections.interact.worldinteract.no-vehicle-use", md.noVehicleUse),
                yml.getString("protections.combat.no-damager", md.noDamager),
                yml.getString("protections.combat.no-damagee", md.noDamagee),
                yml.getString("protections.no-command", md.noCommands),
                yml.getString("protections.no-chat", md.noChat),
                yml.getString("commands.cancelpanic.success", md.cmdCPSuccess),
                yml.getString("commands.cancelpanic.notpanicking", md.cmdCPNotPanicking),
                yml.getString("commands.parsing.invalid-number", md.cmdInvalidNumber),
                yml.getString("commands.cooldown.nogui.set", md.cmdCooldownSet),
                yml.getString("commands.cooldown.nogui.remove", md.cmdCooldownRemove),
                yml.getString("commands.cooldown.nogui.unknown", md.cmdCooldownUnknown),
                yml.getString("commands.cooldown.nogui.missing", md.cmdCooldownMissing)
        );

        if (!initial) {
            for (PluginCommand key : CommandManager.getExecutors().keySet()) {
                key.setPermissionMessage(format(Message.msgs.noPermission));
            }
        }

        if (initial) {
            initial = false;
        }

        Map<String, Object> sDDefaults = new HashMap<>();
        sDDefaults.put("server.name", "Minecraft Server");
        sDDefaults.put("server.image", "https://static.planetminecraft.com/files/resource_media/screenshot/1606/photo9868183_lrg.jpg");
        File serverData = FileUtil.getYamlFile("server-data.yml", file, sDDefaults);
        FileConfiguration yml1 = YamlConfiguration.loadConfiguration(serverData);

        serverInfo = new ServerInfo(
                yml1.getString("server.name", "Minecraft Server"),
                yml1.getString("server.image", "https://static.planetminecraft.com/files/resource_media/screenshot/1606/photo9868183_lrg.jpg"),
                yml1.getString("bungee.transferCommand", "/server")
        );
    }

    public static void send(CommandSender sender, String message) {
        if (message.length() > 0) {
            sender.sendMessage(format(message));
        }
    }

    public static void send(CommandSender sender, String message, Map<String, String> placeholders) {
        if (message.length() > 0) {
            sender.sendMessage(format(message, placeholders));
        }
    }

    public static void send(CommandSender sender, BaseComponent[]... components) {
        if (sender instanceof Player) {
            List<BaseComponent> comps = new ArrayList<>();
            for (BaseComponent[] comp : components) {
                comps.addAll(Arrays.asList(comp));
            }
            ((Player) sender).spigot().sendMessage(comps.toArray(new BaseComponent[0]));
        }
    }

    public static String format(String message) {
        return format(message, null);
    }

    @SuppressWarnings("unchecked")
    public static String format(String message, Map<String, String> placeholders) {
        placeholders = placeholders != null ? placeholders : new HashMap<>();
        placeholders.put("{%PREFIX%}", msgs.prefix);
        placeholders.put("{%EMERGENCY_PREFIX%}", msgs.emergencyPrefix);
        if (Core.getApi() != null) {
            Event event = null;
            try {
                Constructor<?> eventConstructor = Class.forName("com.machopiggies.famedpanicapi.events.MessageParsingEvent").getConstructor(String.class, Map.class);
                event = (Event) eventConstructor.newInstance(message, placeholders);
            } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) { }

            if (event != null) {
                Bukkit.getPluginManager().callEvent(event);
                try {
                    message = (String) event.getClass().getMethod("getMessage").invoke(event);
                    placeholders.putAll((Map<String, String>) event.getClass().getMethod("getPlaceholders").invoke(event));
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException ignored) { }
            }
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static class ServerInfo {
        public String name;
        public String image;
        public String transferCommand;

        public ServerInfo(String name, String image, String transferCommand) {
            this.name = name;
            this.image = image;
            this.transferCommand = transferCommand;
        }
    }

    public static class Messages {
        public String prefix = "&f[&cPanic&f] &r";
        public String emergencyPrefix = "&b&kAA&r &c&lPANIC > ";
        public String noPermission = "{%PREFIX%}&cYou do not have permission to do this!";
        public String mAPlayer = "{%PREFIX%}&7Please specify a valid player!";
        public String setSafemodeOn = "{%PREFIX%}&7Safemode turned &aon&7!";
        public String setSafemodeOff = "{%PREFIX%}&7Safemode turned &coff&7!";
        public String safemodeOn = "{%PREFIX%}&7Safemode is turned &aon&7!";
        public String safemodeOff = "{%PREFIX%}&7Safemode is turned &coff&7!";
        public String announceEnter = "{%EMERGENCY_PREFIX%}&c{%PLAYER_DISPLAYNAME%} &ehas activated panic mode! Please investigate immediately.";
        public String announceEnterInspector = "&6&lCLICK TO TELEPORT!";
        public String announceEnterBungee = "You need to transfer to {%BUNGEE_SERVER%} by clicking &6&lHERE&7!";
        public String announceEnterInspectorHover = "&6You will enter inspector mode!";
        public String announceEnterBungeeHover = "&cTHE PLUGIN WILL NOT VANISH YOU BEFORE YOU ENTER THE SERVER, DO THAT NOW!";
        public String announceLeave = "{%EMERGENCY_PREFIX%}&c{%PLAYER_DISPLAYNAME%} &eis no longer in panic mode!";
        public String enabled = "{%PREFIX%}&7You have entered panic mode! Staff have been alerted and will be with you momentarily.";
        public String enabledSafemode = "{%PREFIX%}&7You have entered panic mode! The system is in safemode, this means staff have NOT been notified externally, you may need to direct message a staff member instead of waiting.";
        public String disabled = "{%PREFIX%}&7You have left panic mode.";
        public String staffDisabled = "{%PREFIX%}&7A staff member has turned off your panic mode!";
        public String onCooldown = "{%PREFIX%}&7You cannot go on panic mode for another &c{%COOLDOWN_EXPIRE%}&7!";
        public String forcedOut = "{%PREFIX%}&7The plugin is reloading/disabling, you have been forced out of the mode you were in!";
        public String resetSuccess = "{%PREFIX%}&7Successfully reset speed!";
        public String panicWhilstSpec = "{%PREFIX%}&7You cannot go into panic mode whilst in inspector mode!";
        public String specWhilstPanic = "{%PREFIX%}&7You cannot go into inspector mode whilst in panic mode!";
        public String inspectorEnter = "{%PREFIX%}&7You have entered panic inspector mode! Please note that this system is in beta and we expect bugs, if you come across anything you believe is an issue to do with this mode, please report it to your serer owner/admin!";
        public String inspectorLeave = "{%PREFIX%}&7You have left panic inspector mode!";
        public String inspectorDisabled = "{%PREFIX%}&7The panic inspector is not enabled on this server!";
        public String inspectorKick = "{%PREFIX%}&c{%TARGET_NAME%} &7has left panic mode. You will be removed from inspector mode in &c{%INSPECTOR_KICK_DELAY%}&7!";
        public String inspectorArrival = "{%PREFIX%}&7A staff member has arrived!";
        public String cooldownExpire = "{%PREFIX%}&7You can use panic again!";
        public String cooldownAdd = "{%PREFIX%}&7You are on panic cooldown for &c{%COOLDOWN_EXPIRE%}&7!";
        public String inPanicMode = "{%PREFIX%}&c{%TARGET_NAME%} &7is in panic mode!";
        public String notInPanicMode = "{%PREFIX%}&c{%TARGET_NAME%} &7is not in panic mode!";

        public String noOpen = "{%PREFIX%}&7You cannot open containers whilst in panic mode!";
        public String noDrop = "{%PREFIX%}&7You cannot drop items whilst in panic mode!";
        public String noMisc = "{%PREFIX%}&7You cannot interact with this whilst in panic mode!";
        public String noBlockBreak = "{%PREFIX%}&7You cannot break blocks whilst in panic mode!";
        public String noBlockPlace = "{%PREFIX%}&7You cannot place blocks whilst in panic mode!";
        public String noVehicleUse = "{%PREFIX%}&7You cannot interact with this vehicle whilst in panic mode!";
        public String noDamager = "{%PREFIX%}&7You cannot damage players whilst in panic mode!";
        public String noDamagee = "{%PREFIX%}&7You cannot attack &c{%TARGET_NAME%} &7whilst they are in panic mode!";
        public String noCommands = "{%PREFIX%}&7You cannot use that command whilst in panic mode!";
        public String noChat = "{%PREFIX%}&7You cannot chat whilst in panic mode!";
        public String cmdCPSuccess = "{%PREFIX%}&7Successfully cancelled &c{%TARGET_NAME%}&7's panic status!";
        public String cmdCPNotPanicking = "{%PREFIX%}&c{%TARGET_NAME%} &7is not in panic mode!";
        public String cmdInvalidNumber = "{%PREFIX%}&7You did not specify a valid number!";
        public String cmdCooldownSet = "{%PREFIX%}&c{%TARGET_NAME%} &7is now on cooldown for &c{%DURATION%}&7!";
        public String cmdCooldownRemove = "{%PREFIX%}&c{%TARGET_NAME%}&7's cooldown has been removed!";
        public String cmdCooldownUnknown = "{%PREFIX%}&7Unknown operation '&c{%OPERATION%}&7', 2nd argument must be either set/add/remove!";
        public String cmdCooldownMissing = "{%PREFIX%}&7Missing operation, please select either set/add/remove!";

        public Messages(String prefix, String emergencyPrefix, String noPermission, String mAPlayer, String setSafemodeOn,
                        String setSafemodeOff, String safemodeOn, String safemodeOff,
                        String announceEnter, String announceEnterInspector, String announceEnterBungee,
                        String announceEnterInspectorHover, String announceEnterBungeeHover,
                        String announceLeave, String enabled, String enabledSafemode,
                        String disabled, String staffDisabled, String onCooldown, String forcedOut,
                        String resetSuccess, String panicWhilstSpec, String specWhilstPanic,
                        String inspectorEnter, String inspectorLeave, String inspectorDisabled,
                        String inspectorKick, String inspectorArrival, String cooldownExpire, String cooldownAdd,
                        String inPanicMode, String notInPanicMode,

                        String noOpen, String noDrop, String noMisc,
                        String noBlockBreak, String noBlockPlace, String noVehicleUse,
                        String noDamager, String noDamagee, String noCommands,
                        String noChat, String cmdCPSuccess, String cmdCPNotPanicking,
                        String cmdInvalidNumber, String cmdCooldownSet, String cmdCooldownRemove,
                        String cmdCooldownUnknown, String cmdCooldownMissing) {
            this.prefix = prefix;
            this.emergencyPrefix = emergencyPrefix;
            this.noPermission = noPermission;
            this.mAPlayer = mAPlayer;
            this.setSafemodeOn = setSafemodeOn;
            this.setSafemodeOff = setSafemodeOff;
            this.safemodeOn = safemodeOn;
            this.safemodeOff = safemodeOff;
            this.announceEnter = announceEnter;
            this.announceEnterInspector = announceEnterInspector;
            this.announceEnterBungee = announceEnterBungee;
            this.announceEnterInspectorHover = announceEnterInspectorHover;
            this.announceEnterBungeeHover = announceEnterBungeeHover;
            this.announceLeave = announceLeave;
            this.enabled = enabled;
            this.enabledSafemode = enabledSafemode;
            this.disabled = disabled;
            this.staffDisabled = staffDisabled;
            this.onCooldown = onCooldown;
            this.forcedOut = forcedOut;
            this.resetSuccess = resetSuccess;
            this.panicWhilstSpec = panicWhilstSpec;
            this.specWhilstPanic = specWhilstPanic;
            this.inspectorEnter = inspectorEnter;
            this.inspectorLeave = inspectorLeave;
            this.inspectorDisabled = inspectorDisabled;
            this.inspectorKick = inspectorKick;
            this.inspectorArrival = inspectorArrival;
            this.cooldownExpire = cooldownExpire;
            this.cooldownAdd = cooldownAdd;
            this.inPanicMode = inPanicMode;
            this.notInPanicMode = notInPanicMode;

            this.noOpen = noOpen;
            this.noDrop = noDrop;
            this.noMisc = noMisc;
            this.noBlockBreak = noBlockBreak;
            this.noBlockPlace = noBlockPlace;
            this.noVehicleUse = noVehicleUse;
            this.noDamager = noDamager;
            this.noDamagee = noDamagee;
            this.noCommands = noCommands;
            this.noChat = noChat;
            this.cmdCPSuccess = cmdCPSuccess;
            this.cmdCPNotPanicking = cmdCPNotPanicking;
            this.cmdInvalidNumber = cmdInvalidNumber;
            this.cmdCooldownSet = cmdCooldownSet;
            this.cmdCooldownRemove = cmdCooldownRemove;
            this.cmdCooldownUnknown = cmdCooldownUnknown;
            this.cmdCooldownMissing = cmdCooldownMissing;
        }

        public Messages() { }
    }
}
