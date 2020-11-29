package com.machopiggies.famedpanic.commands;

import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.gui.MenuInterface;
import com.machopiggies.famedpanic.gui.button.MenuInterfaceButton;
import com.machopiggies.famedpanic.util.*;
import com.mysql.jdbc.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class PanicCooldownCommand extends CommandManager {
    private static final Map<Player, Long> timeCache = new HashMap<>();

    public PanicCooldownCommand() {
        super("paniccooldown", "Shows remaining panic cooldown", "famedpanic.cooldown", "/paniccooldown <player>", "pcooldown");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player && Config.settings.guis.enabled) {
            if (permissable(sender)) {
                Player target = null;

                if (args.length > 0) {
                    Player local = Bukkit.getPlayer(args[0]);
                    if (local != null && local.isOnline() && local.isValid()) {
                        target = local;
                    }
                }
                if (target != null) {
                    MenuInterface menu = new MenuInterface("Cooldowns", 54);
                    reset(menu, (Player) sender, target);
                    menu.launch(sender);
                } else {
                    Message.send(sender, Message.msgs.mAPlayer);
                }
            } else {
                Message.send(sender, Message.msgs.noPermission);
            }
        } else {
            if (permissable(sender)) {
                Player target = null;

                if (args.length > 0) {
                    Player local = Bukkit.getPlayer(args[0]);
                    if (local != null && local.isOnline() && local.isValid()) {
                        target = local;
                    }
                }
                if (target != null) {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("remove")) {
                            if (Core.getPanicManager().isOnCooldown(target.getUniqueId())) {
                                Core.getPanicManager().removeCooldown(target.getUniqueId());

                                Map<String, String> map = new HashMap<>();
                                map.put("{%TARGET_NAME%}", target.getName());
                                map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                                Message.send(sender, Message.msgs.cmdCooldownRemove, map);
                            } else {
                                Map<String, String> map = new HashMap<>();
                                map.put("{%TARGET_NAME%}", target.getName());
                                map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                                Message.send(sender, Message.msgs.notInPanicMode, map);
                            }
                        }
                    } else if (args.length > 2) {
                        if (!args[1].equalsIgnoreCase("remove")) {
                            long time;
                            try {
                                time = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                Message.send(sender, Message.msgs.cmdInvalidNumber);
                                return super.onCommand(sender, command, s, args);
                            }

                            if (args[1].equalsIgnoreCase("add")) {
                                Map<String, String> map = new HashMap<>();
                                map.put("{%TARGET_NAME%}", target.getName());
                                map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                                map.put("{%DURATION%}", TimeDateUtil.getSimpleDurationStringFromSeconds(Core.getPanicManager().getCooldownLength(target.getUniqueId()) + time));
                                Message.send(sender, Message.msgs.cmdCooldownSet, map);

                                Core.getPanicManager().addCooldown(target.getUniqueId(), time);
                            } else if (args[1].equalsIgnoreCase("set")) {
                                Map<String, String> map = new HashMap<>();
                                map.put("{%TARGET_NAME%}", target.getName());
                                map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                                map.put("{%DURATION%}", TimeDateUtil.getSimpleDurationStringFromSeconds(time));
                                Message.send(sender, Message.msgs.cmdCooldownSet, map);

                                Core.getPanicManager().setCooldown(target.getUniqueId(), time);
                            } else {
                                Map<String, String> map = new HashMap<>();
                                map.put("{%OPERATION%}", args[1]);
                                Message.send(sender, Message.msgs.cmdCooldownUnknown, map);
                            }
                        } else {
                            if (Core.getPanicManager().isOnCooldown(target.getUniqueId())) {
                                Core.getPanicManager().removeCooldown(target.getUniqueId());

                                Map<String, String> map = new HashMap<>();
                                map.put("{%TARGET_NAME%}", target.getName());
                                map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                                Message.send(sender, Message.msgs.cmdCooldownRemove, map);
                            } else {
                                Map<String, String> map = new HashMap<>();
                                map.put("{%TARGET_NAME%}", target.getName());
                                map.put("{%TARGET_DISPLAYNAME%}", target.getDisplayName());
                                Message.send(sender, Message.msgs.notInPanicMode, map);
                            }
                        }
                    } else {
                        Message.send(sender, Message.msgs.cmdCooldownMissing);
                    }
                } else {
                    Message.send(sender, Message.msgs.mAPlayer);
                }
            } else {
                Message.send(sender, Message.msgs.noPermission);
            }
        }

        return super.onCommand(sender, command, s, args);
    }

    private void reset(MenuInterface menu, Player sender, Player target) {
        menu.borderise(Config.settings.guis.borderColor);
        menu.set(4, new MenuInterfaceButton(new ItemBuilder.PlayerSkull(target.getName()).getStackAsItemBuilder()
                .setDisplayName(target.getDisplayName())
                .setLore("",
                        Config.settings.guis.defaultColor + "You are reviewing " + Config.settings.guis.titleColor + target.getName(),
                        Config.settings.guis.defaultColor + "cooldown settings!"
                ).build()));

        if (Core.getPanicManager().isOnCooldown(target.getUniqueId())) {
            menu.set(22, new MenuInterfaceButton(new ItemBuilder(Material.WATCH).setDisplayName(Config.settings.guis.titleColor + "" + ChatColor.BOLD + "Active Cooldown").setLore(
                    "",
                    Config.settings.guis.titleColor + "Expires: " + Config.settings.guis.defaultColor + (Core.getPanicManager().isOnCooldown(target.getUniqueId()) && Core.getPanicManager().getCooldownLength(target.getUniqueId()) > 0L ? TimeDateUtil.getSimpleDurationStringFromSeconds(Core.getPanicManager().getCooldownExpiry(target.getUniqueId()) - Instant.now().getEpochSecond()) : "Imminently"),
                    Config.settings.guis.titleColor + "Time: " + Config.settings.guis.defaultColor + (timeCache.getOrDefault(sender, 0L) > 0 ? TimeDateUtil.getSimpleDurationStringFromSeconds(timeCache.getOrDefault(sender, 0L)) : "None!"),
                    "",
                    Config.settings.guis.titleColor + "Click to remove!").addGlow().build(),
            (g,e) -> {
                Core.getPanicManager().removeCooldown(target.getUniqueId());
                reset(menu, sender, target);
            }));
        } else {
            menu.set(22, new MenuInterfaceButton(new ItemBuilder(Material.WATCH).setDisplayName(Config.settings.guis.titleColor + "" + ChatColor.BOLD + "Active Cooldown").setLore(
                    "",
                    Config.settings.guis.titleColor + "Time: " + Config.settings.guis.defaultColor + (timeCache.getOrDefault(sender, 0L) > 0 ? TimeDateUtil.getSimpleDurationStringFromSeconds(timeCache.getOrDefault(sender, 0L)) : "None!")).build()));
        }

        menu.set(37, new MenuInterfaceButton(timeStack(60, timeCache.getOrDefault(sender, 0L)), (g,e) -> {
            timeCache.put(sender, timeCache.getOrDefault(sender, 0L) + 60);
            reset(menu, sender, target);
        }));

        menu.set(38, new MenuInterfaceButton(timeStack(10, timeCache.getOrDefault(sender, 0L)), (g,e) -> {
            timeCache.put(sender, timeCache.getOrDefault(sender, 0L) + 10);
            reset(menu, sender, target);
        }));

        menu.set(39, new MenuInterfaceButton(timeStack(1, timeCache.getOrDefault(sender, 0L)), (g,e) -> {
            timeCache.put(sender, timeCache.getOrDefault(sender, 0L) + 1);
            reset(menu, sender, target);
        }));

        menu.set(41, new MenuInterfaceButton(timeStack(-1, timeCache.getOrDefault(sender, 0L)), (g,e) -> {
            if (timeCache.getOrDefault(sender, 0L) - 1 >= 0) {
                timeCache.put(sender, timeCache.getOrDefault(sender, 0L) - 1);
            } else {
                timeCache.put(sender, 0L);
            }
            reset(menu, sender, target);
        }));

        menu.set(42, new MenuInterfaceButton(timeStack(-10, timeCache.getOrDefault(sender, 0L)), (g,e) -> {
            if (timeCache.getOrDefault(sender, 0L) - 10 >= 0) {
                timeCache.put(sender, timeCache.getOrDefault(sender, 0L) - 10);
            } else {
                timeCache.put(sender, 0L);
            }
            reset(menu, sender, target);
        }));

        menu.set(43, new MenuInterfaceButton(timeStack(-60, timeCache.getOrDefault(sender, 0L)), (g,e) -> {
            if (timeCache.getOrDefault(sender, 0L) - 60 >= 0) {
                timeCache.put(sender, timeCache.getOrDefault(sender, 0L) - 60);
            } else {
                timeCache.put(sender, 0L);
            }
            reset(menu, sender, target);
        }));

        menu.set(49, new MenuInterfaceButton(new ItemBuilder(Material.EMERALD_BLOCK).setDisplayName(Config.settings.guis.titleColor + "" + ChatColor.BOLD + "Apply Cooldown").setLore(
                "").addLore(timeCache.getOrDefault(sender, 0L) > 0,

                Config.settings.guis.defaultColor + "Set " + Config.settings.guis.titleColor + target.getName() + Config.settings.guis.defaultColor + "'s cooldown to",
                        Config.settings.guis.titleColor + TimeDateUtil.getSimpleDurationStringFromSeconds(timeCache.getOrDefault(sender, 0L)) + (Core.getPanicManager().isOnCooldown(target.getUniqueId()) ? "" : Config.settings.guis.defaultColor + " long!")).addLore(Core.getPanicManager().isOnCooldown(target.getUniqueId()),
                        Config.settings.guis.defaultColor + "or add to current which will become",
                        Config.settings.guis.titleColor + TimeDateUtil.getSimpleDurationStringFromSeconds(Core.getPanicManager().getCooldownLength(target.getUniqueId()) + timeCache.getOrDefault(sender, 0L)) + Config.settings.guis.defaultColor + " long!",
                        "",
                        Config.settings.guis.titleColor + "Click" + Config.settings.guis.defaultColor + " to set!").addLore(Core.getPanicManager().isOnCooldown(target.getUniqueId()),
                        Config.settings.guis.titleColor + "Shift-Click" + Config.settings.guis.defaultColor + " to add!").addLore(timeCache.getOrDefault(sender, 0L) <= 0,
                        Config.settings.guis.titleColor + "Please create a suitable time.").addGlow(timeCache.getOrDefault(sender, 0L) > 0).build(),
                (g,e) -> {
                    if (timeCache.getOrDefault(sender, 0L) > 0) {
                        if (Core.getPanicManager().isOnCooldown(target.getUniqueId())) {
                            Core.getPanicManager().addCooldown(target.getUniqueId(), timeCache.getOrDefault(sender, 0L));
                        } else {
                            Core.getPanicManager().setCooldown(target.getUniqueId(), timeCache.getOrDefault(sender, 0L));
                        }
                        timeCache.remove(sender);
                        reset(menu, sender, target);
                    }
                }));
//        menu.set(22, new MenuInterfaceButton(new ItemBuilder(Material.)))
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

    private ItemStack timeStack(long time, long currentTime) {
        boolean positive = time >= 0;
        return new ItemBuilder(Material.PAPER).setDisplayName(Config.settings.guis.titleColor + "" + ChatColor.BOLD + (positive ? "Add " + TimeDateUtil.getSimpleDurationStringFromSeconds(time) : "Remove " + TimeDateUtil.getSimpleDurationStringFromSeconds(time * - 1))).setLore(
                "",
                Config.settings.guis.defaultColor + "Duration will be " + (positive ? "increased" : "decreased") + " to",
                Config.settings.guis.titleColor + TimeDateUtil.getSimpleDurationStringFromSeconds(positive ? time + currentTime : currentTime - (time * -1) > 0 ? currentTime - (time * -1) : 0) + Config.settings.guis.defaultColor + "!"
        ).build();
    }
}
