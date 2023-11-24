package com.layue13.fakeitemcheckerreloaded.entity;

import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rule {
    private Long id;
    private String item;
    private String permission;
    private ItemStack itemStack;

    public synchronized ItemStack getItemStack() {
        if (itemStack == null) {
            String[] split = item.split(":");
            this.itemStack = new ItemStack(Material.getMaterial(split[0]), Integer.parseInt(split[2]), Short.parseShort(split[1]));
        }
        return this.itemStack;
    }
}
