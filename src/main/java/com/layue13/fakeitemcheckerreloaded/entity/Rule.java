package com.layue13.fakeitemcheckerreloaded.entity;

import lombok.*;
import org.bukkit.permissions.Permission;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rule {
    private Long id;
    private String item;
    private Permission permission;
}
