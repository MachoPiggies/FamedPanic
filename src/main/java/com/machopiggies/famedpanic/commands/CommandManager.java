package com.machopiggies.famedpanic.commands;

import com.google.common.reflect.ClassPath;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.Observer;
import com.machopiggies.famedpanic.util.Message;
import com.machopiggies.famedpanic.util.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

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
                e.printStackTrace();
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
        ClassPath classPath;
        try {
            classPath = ClassPath.from(plugin.getClass().getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Package p = plugin.getClass().getPackage();

        for (ClassPath.ClassInfo info : classPath.getTopLevelClassesRecursive(p.getName())) {
            Class<?> clss = info.load();
            if (CommandManager.class.equals(clss.getSuperclass())) {
                try {
                    clss.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
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
            e.printStackTrace();
        }
        return commands;
    }

    static {
        executors = new HashMap<>();
    }
}
