package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.managers.PanicData;
import com.machopiggies.famedpanic.managers.PanicManager;
import com.machopiggies.famedpanic.util.Logger;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PanicCommand extends CommandManager {

    public PanicCommand() {
        super("panic", "Sends a user into panic mode", "famedpanic.panic", "/panic");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (permissable(sender)) {
                if (!PanicManager.panicking((Player) sender)) {
                    PanicData.Settings settings = new PanicData.Settings(
                            ((Player) sender).getWalkSpeed(),
                            ((Player) sender).getFlySpeed(),
                            ((Player) sender).isFlying(),
                            ((Player) sender).getAllowFlight()
                    );
                    Core.getPanicManager().protect(new PanicData((Player) sender, settings));
                } else {
                    Core.getPanicManager().unprotect((Player) sender, sender);
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
