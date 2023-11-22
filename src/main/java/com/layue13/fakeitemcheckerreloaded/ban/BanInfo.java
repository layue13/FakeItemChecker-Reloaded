package com.layue13.fakeitemcheckerreloaded.ban;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.bukkit.entity.Player;

@Data
@ToString
@Builder
public class BanInfo {
    private Player player;
    private String server;
    private String source;
    private String reason;
}
