package com.layue13.fakeitemcheckerreloaded.entity.converter;

import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;

import javax.persistence.AttributeConverter;

public class BukkitItemStackConverter implements AttributeConverter<ItemStack, String> {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public String convertToDatabaseColumn(ItemStack attribute) {
        return GSON.toJson(attribute);
    }

    @Override
    public ItemStack convertToEntityAttribute(String dbData) {
        return GSON.fromJson(dbData, ItemStack.class);
    }
}
