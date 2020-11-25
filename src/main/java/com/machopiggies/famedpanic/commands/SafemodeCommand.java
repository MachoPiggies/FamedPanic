package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.managers.PanicData;
import com.machopiggies.famedpanic.managers.PanicManager;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SafemodeCommand extends CommandManager {

    public SafemodeCommand() {
        super("psafemode", "Stops sending panic alerts to webhooks", "famedpanic.safemode", "/safemode [on/off]", "psm");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (permissable(sender)) {
            if (args.length == 0) {
                Message.send(sender, Config.isSafemode() ? Message.msgs.safemodeOn : Message.msgs.safemodeOff);
            } else if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "on":
                        Config.setSafemode(true);
                        Message.send(sender, Message.msgs.setSafemodeOn);
                        break;
                    case "off":
                        Config.setSafemode(false);
                        Message.send(sender, Message.msgs.setSafemodeOff);
                        break;
                    default:
                        Message.send(sender, Config.isSafemode() ? Message.msgs.safemodeOn : Message.msgs.safemodeOff);
                        break;
                }
            }
        } else {
            Message.send(sender, Message.msgs.noPermission);
        }

        return super.onCommand(sender, command, s, args);
    }

    //todo auto update all files if some settings are missing
    //todo tabcompleter for this command
}
