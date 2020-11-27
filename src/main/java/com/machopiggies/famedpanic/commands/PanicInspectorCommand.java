package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.managers.PanicInspectorManager;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Logger;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import sun.rmi.runtime.Log;

import java.util.HashMap;
import java.util.Map;

public class PanicInspectorCommand extends CommandManager {
    //todo allow disabling and adding of more aliases in config for each command, probably in separate config file
    public PanicInspectorCommand() {
            super("panicinspector", "Allows a player to enter panic inspector mode", "famedpanic.panicinspector", "/pi");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (permissable(sender)) {
                if (Config.settings.panicInspector.enabled) {
                    Player player = (Player) sender;

                    if (Core.getPanicInspectorManager().isInspector(player)) {
                        Core.getPanicInspectorManager().removeInspector(player, Core.getPanicInspectorManager().getInspectors().get(player), PanicInspectorManager.RemoveReason.COMMAND);
                    } else {
                        if (!Core.getPanicManager().panicking(player)) {
                            Player target = null;
                            if (args.length > 0) {
                                Player local = Bukkit.getPlayer(args[0]);
                                if (local != null && local.isValid() && local.isOnline()) {
                                    target = local;
                                } else {
                                    Message.send(sender, Message.msgs.mAPlayer);
                                    return true;
                                }
                            }
                            if (target != null) {
                                if (Core.getPanicManager().panicking(target)) {
                                    PanicInspectorManager.InspectorData data = new PanicInspectorManager.InspectorData(player, target);
                                    Core.getPanicInspectorManager().addInspector(player, data);
                                } else {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("{%TARGET_NAME%}", target.getName());
                                    map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                                    Message.send(sender, Message.msgs.cmdCPNotPanicking, map);
                                }
                            } else {
                                Message.send(sender, Message.msgs.mAPlayer);
                            }
                        } else {
                            Message.send(sender, Message.msgs.specWhilstPanic);
                        }
                    }
                } else {
                    Message.send(sender, Message.msgs.inspectorDisabled);
                }
            } else {
                Message.send(sender, Message.msgs.noPermission);
            }
        } else {
            mustBePlayer(sender);
        }

        return super.onCommand(sender, command, s, args);
    }
}
