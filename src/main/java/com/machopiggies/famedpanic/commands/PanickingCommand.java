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

public class PanickingCommand extends CommandManager {

    public PanickingCommand() {
        super("panicking", "Shows if a player is in panic mode", "famedpanic.panicking", "/panicking <player>");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (permissable(sender)) {
            if (args != null && args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target.isValid() && target.isOnline()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("{%TARGET_NAME%}", target.getName());
                    map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                    String message = Core.getPanicManager().panicking(target) ? Message.msgs.inPanicMode : Message.msgs.notInPanicMode;
                    Message.send(sender, message, map);
                } else {
                    Message.send(sender, Message.msgs.mAPlayer);
                }
            } else {
                Message.send(sender, Message.msgs.mAPlayer);
            }
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
