package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.managers.PanicInspectorManager;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ForceInspectorCancelCommand extends CommandManager {

    public ForceInspectorCancelCommand() {
        super("panicforceinspectorcancel", "The reset command for the inspector", "famedpanic.inspector.forcecancel", "pfic");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (permissable(sender)) {
            if (Config.settings.panicInspector.enabled) {
                Player target = null;
                if (args.length > 0) {
                    Player local = Bukkit.getPlayer(args[0]);
                    if (local != null && local.isValid() && local.isOnline()) {
                        target = local;
                    } else {
                        Message.send(sender, Message.msgs.mAPlayer);
                        return super.onCommand(sender, command, s, args);
                    }
                }

                if (target == null || target.equals(sender)) {
                    if (sender instanceof Player) {
                        reset((Player) sender);
                    }
                } else {
                    reset(target);
                }
            } else {
                Message.send(sender, Message.msgs.inspectorDisabled);
            }
        } else {
            Message.send(sender, Message.msgs.noPermission);
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

    private void reset(Player player) {
        PanicInspectorManager.InspectorData data;
        if ((data = Core.getPanicInspectorManager().getInspectors().get(player)) != null && data.player.equals(player)) {
            player.teleport(data.origin);
            player.setGameMode(data.gamemode);
        }
        player.teleport(player.getWorld().getSpawnLocation());
        player.setGameMode(player.getServer().getDefaultGameMode());
        Core.getPanicInspectorManager().getInspectors().remove(player);
    }
}
