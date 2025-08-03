package com.layue13.fakeitemcheckerreloaded.entity;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Log {
    private String id;
    private String playerName;
    private String server;
    private Date time;
    private String location;
    private String event;
    private String inventoryType;
    private Rule rule;
}
