package com.layue13.fakeitemcheckerreloaded.entity.converter;

import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import org.bukkit.Location;

import javax.persistence.AttributeConverter;

public class BukkitLocationConverter implements AttributeConverter<Location, String> {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public String convertToDatabaseColumn(Location attribute) {
        return GSON.toJson(attribute);
    }

    @Override
    public Location convertToEntityAttribute(String dbData) {
        return GSON.fromJson(dbData, Location.class);
    }
}
