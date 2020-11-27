package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CancelPanicCommand extends CommandManager {

    public CancelPanicCommand() {
        super("cancelpanic", "Allows player to cancel another players panic", "famedpanic.panic.cancel", "/cp");
    }

    //todo allow this to work cross-network through plugin messaging
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
}
