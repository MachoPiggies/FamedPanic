package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.managers.InspectorData;
import com.machopiggies.famedpanic.managers.PanicInspectorManager;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PanicInspectorCommand extends CommandManager {
    public PanicInspectorCommand() {
        super("panicinspector", "Allows a player to enter panic inspector mode", "famedpanic.inspector", "/panicinspector <player>", "pi");
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
                                    InspectorData data = new InspectorData(player, target);
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList()), completions);
        Collections.sort(completions);
        return completions;
    }
}
