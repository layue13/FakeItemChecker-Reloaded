package com.layue13.fakeitemcheckerreloaded.entity;

import com.layue13.fakeitemcheckerreloaded.entity.converter.BukkitLocationConverter;
import lombok.*;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryType;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "logs")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String playerName;
    private String server;
    private LocalTime time;
    @Convert(converter = BukkitLocationConverter.class)
    private Location location;
    private Class<? extends Event> event;
    @Enumerated
    private InventoryType inventoryType;
}
