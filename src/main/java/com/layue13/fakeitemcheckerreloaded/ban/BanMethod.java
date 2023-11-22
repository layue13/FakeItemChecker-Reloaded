package com.layue13.fakeitemcheckerreloaded.ban;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public enum BanMethod {
    BUNGEE_BAN(banInfo -> {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FakeItemChecker-Reloaded");
        if (!Bukkit.getServer().getMessenger().getOutgoingChannels().contains("BungeeBan")) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeBan");
        }
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("ban");
        byteArrayDataOutput.writeUTF(banInfo.getPlayer().getName());
        byteArrayDataOutput.writeUTF(banInfo.getSource());
        byteArrayDataOutput.writeUTF(banInfo.getSource());
        byteArrayDataOutput.writeUTF(banInfo.getReason());
        Bukkit.getServer().sendPluginMessage(plugin,"BungeeBan",byteArrayDataOutput.toByteArray());
    });

    private final Consumer<BanInfo> consumer;

    BanMethod(Consumer<BanInfo> consumer) {
        this.consumer = consumer;
    }

    public void ban(BanInfo banInfo) {
        this.consumer.accept(banInfo);
    }
}
