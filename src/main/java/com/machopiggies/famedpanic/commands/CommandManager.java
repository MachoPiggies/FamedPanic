package com.machopiggies.famedpanic.commands;

import com.google.common.reflect.ClassPath;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.Logger;
import com.machopiggies.famedpanic.util.Message;
import com.machopiggies.famedpanic.util.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandManager extends Observer implements CommandExecutor, TabCompleter {

    private final static Map<PluginCommand, CommandExecutor> executors;

    protected String command;
    protected String desc;
    protected String usage;
    protected String permission;
    protected String[] aliases;

    public CommandManager(String command, String desc, String permission, String usage, String... aliases) {
        command = command.toLowerCase();
        this.command = command;
        this.desc = desc;
        this.usage = usage;
        this.permission = permission;
        aliases = aliases != null ? aliases : new String[0];
        this.aliases = aliases;

        SimpleCommandMap commands = getCommandMap();

        if (commands != null) {
            try {
                Constructor<PluginCommand> privateConst = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                privateConst.setAccessible(true);
                PluginCommand cmd = Bukkit.getPluginCommand(command);
                cmd = cmd == null ? privateConst.newInstance(command, Core.getPlugin()) : cmd;
                cmd.setPermission(permission);
                cmd.setPermissionMessage(Message.format(Message.msgs.noPermission));
                cmd.setDescription(desc);
                cmd.setUsage(usage);
                cmd.setAliases(Arrays.asList(aliases));
                cmd.setExecutor(this);
                cmd.setTabCompleter(this);
                commands.register(command, cmd);

                executors.put(cmd, this);
            } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                File file = Logger.createErrorLog(e, "command creation error (" + command + ")");
                Logger.severe("An error occurred whilst trying to load a command. If restarting your server does not fix this, please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
            }
        }
    }

    //

    public boolean permissable(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean permissable(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    public void mustBePlayer(CommandSender sender) {
        sender.sendMessage("You must be a player to execute this command!");
    }

    //
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }

    @SuppressWarnings({"beta", "UnstableApiUsage"})
    public static void activateCmds(Plugin plugin) {
        Package p = plugin.getClass().getPackage();

        try {
            ClassPath classPath = ClassPath.from(plugin.getClass().getClassLoader());
            for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(p.getName())) {
                Class<?> clss = info.load();
                if (CommandManager.class.equals(clss.getSuperclass())) {
                    try {
                        clss.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        File file = Logger.createErrorLog(e, "command instantiation error");
                        Logger.severe("An error occurred whilst trying to get a command which means some of its functionality has been lost. If restarting your server does not fix this, please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
                        Bukkit.getPluginManager().disablePlugin(Core.getPlugin());
                    }
                }
            }
        } catch (IOException e) {
            File file = Logger.createErrorLog(e, "classpath not found");
            Logger.severe("A critical error has occurred and as such, some of the commands in the plugin have lost functionality. If restarting your server does not fix this, please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
            Bukkit.getPluginManager().disablePlugin(Core.getPlugin());
        }
    }

    public static Map<PluginCommand, CommandExecutor> getExecutors() {
        return executors;
    }

    public static SimpleCommandMap getCommandMap() {
        SimpleCommandMap commands = null;
        try {
            Object cserver = Objects.requireNonNull(PacketManager.getClassNMS("CraftServer", PacketManager.NMSType.CRAFTBUKKIT)).cast(Bukkit.getServer());
            commands = (SimpleCommandMap) cserver.getClass().getMethod("getCommandMap").invoke(cserver);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            File file = Logger.createErrorLog(e, "commandmap unreachable");
            Logger.severe("An error occurred whilst trying to get commandmap. If restarting your server does not fix this, please contact the plugin developer with the following error log! [Created error log at " + file.getPath() + "]");
        }
        return commands;
    }

    static {
        executors = new HashMap<>();
    }
}
