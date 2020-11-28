package com.machopiggies.famedpanic.gui;

import org.bukkit.ChatColor;

public enum StainedMaterialColor {
    WHITE((byte) 0, (byte) 15, ChatColor.WHITE),
    ORANGE((byte) 1, (byte) 14, ChatColor.GOLD),
    MAGENTA((byte) 2, (byte) 13, ChatColor.LIGHT_PURPLE),
    LIGHT_BLUE((byte) 3, (byte) 12, ChatColor.AQUA),
    YELLOW((byte) 4, (byte) 11, ChatColor.YELLOW),
    LIME((byte) 5, (byte) 10, ChatColor.GREEN),
    PINK((byte) 6, (byte) 9, ChatColor.LIGHT_PURPLE),
    GRAY((byte) 7, (byte) 8, ChatColor.DARK_GRAY),
    LIGHT_GRAY((byte) 8, (byte) 7, ChatColor.GRAY),
    CYAN((byte) 9, (byte) 6, ChatColor.DARK_AQUA),
    PURPLE((byte) 10, (byte) 0, ChatColor.DARK_PURPLE),
    BLUE((byte) 11, (byte) 4, ChatColor.BLUE),
    BROWN((byte) 12, (byte) 3, ChatColor.DARK_RED),
    GREEN((byte) 13, (byte) 2, ChatColor.DARK_GREEN),
    RED((byte) 14, (byte) 1, ChatColor.RED),
    BLACK((byte) 15, (byte) 0, ChatColor.BLACK),
    NULL((byte) -1, (byte) -1, null);

    private final byte glassColor;
    private final byte dyeColor;
    private final ChatColor chatColor;

    StainedMaterialColor(byte glassColor, byte dyeColor, ChatColor chatColor) {
        this.glassColor = glassColor;
        this.dyeColor = dyeColor;
        this.chatColor = chatColor;
    }

    public byte getGlassColorId() {
        return this.glassColor;
    }

    public byte getDyeColorId() {
        return this.dyeColor;
    }

    public ChatColor getBestChatColor() {
        return this.chatColor;
    }
}
