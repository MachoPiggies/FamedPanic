package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SafemodeCommand extends CommandManager {

    public SafemodeCommand() {
        super("panicsafemode", "Stops sending panic alerts to webhooks", "famedpanic.safemode", "/panicsafemode [on/off]", "psafemode", "psm");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (permissable(sender)) {
            if (args.length == 0) {
                Message.send(sender, Config.isSafemode() ? Message.msgs.safemodeOn : Message.msgs.safemodeOff);
            } else if (args.length == 1) {
                if (permissable(sender, "famedpanic.safemode.configure")) {
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
                } else {
                    Message.send(sender, Config.isSafemode() ? Message.msgs.safemodeOn : Message.msgs.safemodeOff);
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
        StringUtil.copyPartialMatches(args[0], Arrays.asList("on", "off"), completions);
        Collections.sort(completions);
        return completions;
    }
}
