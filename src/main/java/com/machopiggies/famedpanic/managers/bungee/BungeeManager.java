package com.machopiggies.famedpanic.managers.bungee;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.machopiggies.famedpanic.Core;
import com.machopiggies.famedpanic.observer.Observer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

public class BungeeManager extends Observer implements PluginMessageListener {

    @Override
    protected void onActivate() {
        Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(Core.getPlugin(), "BungeeCord");
        Bukkit.getServer().getMessenger().registerIncomingPluginChannel(Core.getPlugin(), "BungeeCord", this);
    }

    @Override
    @SuppressWarnings({"beta", "UnstableApiUsage"})
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subchannel = in.readUTF();
            String data = in.readUTF();
            Bukkit.getPluginManager().callEvent(new BungeeMessageEvent(subchannel, data));
        }
    }

    public void sendBungeeForwardMessage(String subchannel, String message) {
        sendBungeeForwardMessage(subchannel, message, null);
    }

    @SuppressWarnings({"beta", "UnstableApiUsage"})
    public void sendBungeeForwardMessage(String subchannel, String message, Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF(subchannel);

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
            msgout.writeShort(123);
        } catch (IOException exception){
            exception.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        if (Bukkit.getOnlinePlayers().size() > 0) {
            (Objects.requireNonNull(player != null ? player : Iterables.getFirst(Bukkit.getOnlinePlayers(), null))).sendPluginMessage(Core.getPlugin(), "BungeeCord", out.toByteArray());
        }
    }
}
