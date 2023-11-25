package com.layue13.fakeitemcheckerreloaded.ban;


import org.bukkit.BanList;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.function.Consumer;

public enum BanMethod {
    BUNGEE_BAN(banInfo -> {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
                dataOutputStream.writeUTF("gban");
                dataOutputStream.writeUTF(banInfo.getPlayer().getUniqueId().toString());
                dataOutputStream.writeUTF(banInfo.getSource());
                dataOutputStream.writeUTF(banInfo.getReason());
            }
            banInfo.getPlugin().getServer().sendPluginMessage(banInfo.getPlugin(), "BungeeBan", byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
