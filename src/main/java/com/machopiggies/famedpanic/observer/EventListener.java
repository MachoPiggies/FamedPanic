package com.machopiggies.famedpanic.observer;

import com.machopiggies.famedpanic.commands.CommandManager;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Message;
import com.machopiggies.famedpanic.util.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class EventListener extends Observer {

    public static ActionPreferences prefs;
    protected Message.Messages msgs;

    @Override
    public void onActivate() {
        msgs = Message.msgs;
    }

    public static void intialize() {
        List<Command> commands = new ArrayList<>();
        List<String> list = Config.getConfig().getStringList("panic.stopCommands");
        SimpleCommandMap commandMap = CommandManager.getCommandMap();

        if (commandMap != null) {
            for (Command cmd : commandMap.getCommands()) {
                if (list.contains(cmd.getName())) {
                    commands.add(cmd);
                }
            }
        }

        prefs = new ActionPreferences(
                Config.getConfig().getBoolean("panic.disableMovement", true),
                Config.getConfig().getBoolean("panic.interaction.stopOpening", true),
                Config.getConfig().getBoolean("panic.interaction.stopDropping", true),
                Config.getConfig().getBoolean("panic.interaction.stopPickup", true),
                Config.getConfig().getBoolean("panic.interaction.stopInventoryMoving", true),
                Config.getConfig().getBoolean("panic.interaction.stopWorldInteraction", true),
                Config.getConfig().getBoolean("panic.combat.stopDamager", true),
                Config.getConfig().getBoolean("panic.combat.stopDamagee", true),
                commands,
                Config.getConfig().getBoolean("panic.stopChat", true)
        );
    }

    public static class ActionPreferences {
        public boolean disableMovement;
        public boolean stopOpening;
        public boolean stopDropping;
        public boolean stopPickup;
        public boolean stopInventoryMoving;
        public boolean stopWorldInteraction;
        public boolean stopDamager;
        public boolean stopDamagee;
        public List<Command> stopCommands;
        public boolean stopChat;

        public ActionPreferences(boolean disableMovement,
                                 boolean stopOpening, boolean stopDropping, boolean stopPickup,
                                 boolean stopInventoryMoving, boolean stopWorldInteraction,
                                 boolean stopDamager, boolean stopDamagee, List<Command> stopCommands,
                                 boolean stopChat) {
            this.disableMovement = disableMovement;
            this.stopOpening = stopOpening;
            this.stopDropping = stopDropping;
            this.stopPickup = stopPickup;
            this.stopInventoryMoving = stopInventoryMoving;
            this.stopWorldInteraction = stopWorldInteraction;
            this.stopDamager = stopDamager;
            this.stopDamagee = stopDamagee;
            this.stopCommands = stopCommands;
            this.stopChat = stopChat;
        }
    }
}