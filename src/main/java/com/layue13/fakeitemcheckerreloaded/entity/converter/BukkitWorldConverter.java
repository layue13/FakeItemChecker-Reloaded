package com.layue13.fakeitemcheckerreloaded.entity.converter;

import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.persistence.AttributeConverter;

public class BukkitWorldConverter implements AttributeConverter<World, String> {
    @Override
    public String convertToDatabaseColumn(World attribute) {
        return attribute.getName();
    }

    @Override
    public World convertToEntityAttribute(String dbData) {
        return Bukkit.getWorld(dbData);
    }
}
