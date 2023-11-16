package com.layue13.fakeitemcheckerreloaded.entity;

import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import javax.persistence.*;

@Entity
@Table(name = "rules")
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private ItemStack item;
    private String permission;
}
