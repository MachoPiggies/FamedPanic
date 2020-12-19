package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PanicResetCommand extends CommandManager {

    public PanicResetCommand() {
        super("panicreset", "Sets players speed back to default values", "famedpanic.resetplayer", "/panicreset [Player]", "preset");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (permissable(sender)) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Core.getPanicManager().unprotect((Player) sender, sender);
                    reset((Player) sender);
                    Message.send(sender, Message.msgs.resetSuccess);
                } else {
                    mustBePlayer(sender);
                }
            } else if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target.isValid() && target.isOnline()) {
                    if ((sender instanceof ConsoleCommandSender && !sender.equals(target)) || sender instanceof Player) {
                        Core.getPanicManager().unprotect(target, sender);
                        reset(target);
                        Message.send(sender, Message.msgs.resetSuccess);
                    } else {
                        mustBePlayer(sender);
                    }
                } else {
                    Message.send(sender, Message.msgs.mAPlayer);
                }
            }
        } else {
            Message.send(sender, Message.msgs.noPermission);
        }

        return super.onCommand(sender, command, s, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList()), completions);
        Collections.sort(completions);
        return completions;
    }

    private void reset(Player player) {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFallDistance(0);
    }
}
