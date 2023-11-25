package com.layue13.fakeitemcheckerreloaded.ban;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.BanList;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.function.Consumer;

public enum BanMethod {
    BUNGEE_BAN(banInfo -> {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF("gban");
        byteArrayDataOutput.writeUTF(banInfo.getPlayer().getDisplayName());
        byteArrayDataOutput.writeUTF(banInfo.getSource());
        byteArrayDataOutput.writeUTF(banInfo.getReason());
        banInfo.getPlugin().getServer().sendPluginMessage(banInfo.getPlugin(), "BungeeBan", byteArrayDataOutput.toByteArray());
    }),
    BUKKIT_BAN(banInfo -> {
        try {
            banInfo.getPlugin().getServer().getBanList(BanList.Type.NAME).addBan(banInfo.getPlayer().getName(), banInfo.getReason(), DateFormat.getDateInstance().parse("9999-12-31 00:00:00"), banInfo.getSource());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    });
    private final Consumer<BanInfo> consumer;

    BanMethod(Consumer<BanInfo> consumer) {
        this.consumer = consumer;
    }

    public void ban(BanInfo banInfo) {
        this.consumer.accept(banInfo);
    }
}
