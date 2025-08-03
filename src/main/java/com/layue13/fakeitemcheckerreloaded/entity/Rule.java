package com.layue13.fakeitemcheckerreloaded.entity;

import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Rule {
    private Long id;
    private String item;
    private String permission;
    private transient ItemStack itemStack;
    
    public synchronized ItemStack getItemStack() {
        if (itemStack == null && item != null) {
            try {
                String[] split = item.split(":");
                if (split.length < 3) {
                    throw new IllegalArgumentException("Invalid item format: " + item + ". Expected format: MATERIAL:DURABILITY:AMOUNT");
                }
                
                Material material = Material.getMaterial(split[0]);
                if (material == null) {
                    throw new IllegalArgumentException("Invalid material: " + split[0]);
                }
                
                short durability = Short.parseShort(split[1]);
                int amount = Integer.parseInt(split[2]);
                
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be positive: " + amount);
                }
                
                this.itemStack = new ItemStack(material, amount, durability);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in item: " + item, e);
            }
        }
        return this.itemStack;
    }
    
    public void setItem(String item) {
        this.item = item;
        this.itemStack = null; // Reset cached itemStack
    }
    
    public boolean isValid() {
        try {
            return item != null && !item.trim().isEmpty() && 
                   permission != null && !permission.trim().isEmpty() &&
                   getItemStack() != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getValidationError() {
        if (item == null || item.trim().isEmpty()) {
            return "Item cannot be null or empty";
        }
        if (permission == null || permission.trim().isEmpty()) {
            return "Permission cannot be null or empty";
        }
        try {
            getItemStack();
            return null;
        } catch (Exception e) {
            return "Invalid item format: " + e.getMessage();
        }
    }
}
