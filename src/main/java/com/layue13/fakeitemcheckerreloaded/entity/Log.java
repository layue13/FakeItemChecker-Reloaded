package com.layue13.fakeitemcheckerreloaded.entity;

import lombok.*;
import org.bukkit.event.inventory.InventoryType;

import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Log {
    private UUID id;
    private String playerName;
    private String server;
    private Date time;
    private String location;
    private String event;
    private InventoryType inventoryType;
    private Rule rule;
}
