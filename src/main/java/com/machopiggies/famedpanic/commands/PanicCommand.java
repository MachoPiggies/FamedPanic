package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.util.Config;
import com.machopiggies.famedpanic.util.Message;
import com.machopiggies.famedpanic.util.TimeDateUtil;
import com.machopiggies.famedpanic.managers.PanicData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PanicCommand extends CommandManager {

    public PanicCommand() {
        super("panic", "Sends a user into panic mode", "famedpanic.panic", "/panic");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (permissable(sender)) {
                if (!Core.getPanicInspectorManager().isInspector((Player) sender)) {
                    if (!Core.getPanicManager().isOnCooldown(((Player) sender).getUniqueId()) || permissable(sender, "famedpanic.panic.cooldown.bypass")) {
                        if (!Core.getPanicManager().panicking((Player) sender)) {
                            PanicData.Settings settings = new PanicData.Settings(
                                    ((Player) sender).getWalkSpeed(),
                                    ((Player) sender).getFlySpeed(),
                                    ((Player) sender).isFlying(),
                                    ((Player) sender).getAllowFlight()
                            );
                            Core.getPanicManager().protect(new PanicData((Player) sender, settings));
                        } else {
                            Core.getPanicManager().unprotect((Player) sender, sender);
                            if (Config.settings.defaultCooldown > 0 && !permissable(sender, "famedpanic.panic.cooldown.bypass")) {
                                Core.getPanicManager().addCooldown(((Player) sender).getUniqueId(), Config.settings.defaultCooldown);
                            }
                        }
                    } else {
                        Map<String, String> placeholders = new HashMap<>();
                        placeholders.put("{%COOLDOWN_EXPIRE%}", TimeDateUtil.getSimpleDurationStringFromSeconds(Core.getPanicManager().getCooldownExpiry(((Player) sender).getUniqueId()) - Instant.now().getEpochSecond()));
                        Message.send(sender, Message.msgs.onCooldown, placeholders);
                    }
                } else {
                    Message.send(sender, Message.msgs.panicWhilstSpec);
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
