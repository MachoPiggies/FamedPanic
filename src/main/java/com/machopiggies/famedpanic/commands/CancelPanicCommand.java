package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class CancelPanicCommand extends CommandManager {

    public CancelPanicCommand() {
        super("cancelpanic", "Allows player to cancel another players panic", "famedpanic.panic.cancel", "/cp");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (permissable(sender)) {
                Player target;
                if (args.length > 0 && (target = Bukkit.getPlayer(args[0])) != null && target.isOnline() && target.isValid()) {
                    if (Core.getPanicManager().panicking((Player) sender)) {
                        Core.getPanicManager().unprotect(target, sender);
                        Map<String, String> map = new HashMap<>();
                        map.put("{%TARGET_NAME%}", target.getName());
                        map.put("{TARGET_DISPLAYNAME%}", target.getDisplayName());
                        Message.send(sender, Message.msgs.cmdCPSuccess, map);
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put("{%TARGET_NAME%}", target.getName());
                        map.put("{TARGET_DISPLAYNAME%}", target.getDisplayName());
                        Message.send(sender, Message.msgs.cmdCPNotPanicking, map);
                    }
                } else {
                    Message.send(sender, Message.msgs.mAPlayer);
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
