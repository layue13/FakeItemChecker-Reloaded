//package com.layue13.fakeitemcheckerreloaded.api;
//
//import com.layue13.fakeitemcheckerreloaded.FakeItemCheckerReloaded;
//import org.bukkit.Bukkit;
//import org.bukkit.OfflinePlayer;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitRunnable;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class FakeItemCheckerAPI {
//    public static FakeItemCheckerAPI getInstance() {
//        return null;
//    }
//    public static YamlConfiguration getPlayerData(final Player p) {
//        if (FakeItemCheckerReloaded.playerDataMap.get(p.getName()) != null) {
//            return FakeItemCheckerReloaded.playerDataMap.get(p.getName());
//        }
//        return null;
//    }
//
//    public static List<String> getPlayerErrorList(final Player p) {
//        if (getPlayerData(p) == null) {
//            return null;
//        }
//        return (List<String>)getPlayerData(p).getStringList("list");
//    }
//
//    public static void addToErrorList(final Player p, final String addreason, final String invname) {
//        if (getPlayerErrorList(p) == null) {
//            return;
//        }
//        final List<String> list = getPlayerErrorList(p);
//        boolean canadd = true;
//        for (final String reason : list) {
//            if (reason.split("@")[0].equals(addreason)) {
//                canadd = false;
//                break;
//            }
//        }
//        if (!canadd) {
//            return;
//        }
//        list.add(String.valueOf(addreason) + "@" + System.currentTimeMillis());
//        final YamlConfiguration pdata = getPlayerData(p);
//        pdata.set("list", (Object)list);
//        new BukkitRunnable(pdata) {
//            final YamlConfiguration finalpdata = finalpdata;
//
//            public void run() {
//                if (FakeItemCheckerReloaded.config.getStringList("main.player-whitelist").contains(p.getName())) {
//                    return;
//                }
//                FakeItemCheckerReloaded.playerDataMap.put(p.getName(), this.finalpdata);
//                for (final OfflinePlayer op : Bukkit.getOperators()) {
//                    if (op.isOnline()) {
//                        op.getPlayer().sendMessage("\u73a9\u5bb6" + p.getName() + "\u56e0\u4e3a" + addreason + "\u800c\u6b63\u5728\u88ab\u8ba1\u5165\u5f02\u5e38!");
//                        op.getPlayer().sendMessage("\u73a9\u5bb6\u4f4d\u7f6e: " + p.getLocation().getWorld().getName() + " (" + p.getLocation().getBlockX() + "," + p.getLocation().getBlockY() + "," + p.getLocation().getBlockZ() + ")");
//                        op.getPlayer().sendMessage("\u5bb9\u5668\u540d\u79f0: " + invname);
//                    }
//                }
//                System.out.println("\u73a9\u5bb6" + p.getName() + "\u56e0\u4e3a" + addreason + "\u800c\u6b63\u5728\u88ab\u8ba1\u5165\u5f02\u5e38!");
//                System.out.println("\u73a9\u5bb6\u4f4d\u7f6e: " + p.getLocation().getWorld().getName() + " (" + p.getLocation().getBlockX() + "," + p.getLocation().getBlockY() + "," + p.getLocation().getBlockZ() + ")");
//                System.out.println("\u5bb9\u5668\u540d\u79f0: " + invname);
//                final File playerFile = new File("./plugins/FakeItemChecker/PlayerData", String.valueOf(p.getName()) + ".yml");
//                try {
//                    FakeItemCheckerReloaded.playerDataMap.get(p.getName()).save(playerFile);
//                }
//                catch (Exception ex) {}
//            }
//        }.runTask(FakeItemCheckerReloaded.plugin);
//    }
//
//    public static void checkListTime(final Player p) {
//        if (getPlayerErrorList(p) == null) {
//            return;
//        }
//        List<String> list = getPlayerErrorList(p);
//        for (int index = 1; index < list.size(); ++index) {
//            final String reason = list.get(index);
//            if (System.currentTimeMillis() - Long.valueOf(reason.split("@")[1]) > FakeItemCheckerReloaded.config.getInt("main.clearTime-byDay") * 86400000L) {
//                if (list.size() <= 1) {
//                    list = new ArrayList<String>();
//                    break;
//                }
//                list.remove(index);
//                --index;
//            }
//        }
//        final YamlConfiguration pdata = getPlayerData(p);
//        pdata.set("list", (Object)list);
//        final List<String> runlist = list;
//        new BukkitRunnable(runlist, pdata) {
//            final int size = list.size();
//            final YamlConfiguration finalpdata = finalpdata;
//
//            public void run() {
//                FakeItemCheckerReloaded.playerDataMap.put(p.getName(), this.finalpdata);
//                if (FakeItemCheckerReloaded.config.getStringList("main.player-whitelist").contains(p.getName())) {
//                    return;
//                }
//                int bannum = FakeItemCheckerReloaded.config.getInt("main.banRuleNumber");
//                if (FakeItemCheckerReloaded.config.get("main.specialPlayerRuleNumber." + p.getName()) != null) {
//                    bannum = FakeItemCheckerReloaded.config.getInt("main.specialPlayerRuleNumber." + p.getName());
//                }
//                if (this.size >= bannum) {
//                    p.kickPlayer(FakeItemCheckerReloaded.config.getString("main.kickinfo"));
//                    System.out.println("\u73a9\u5bb6 " + p.getName() + " \u56e0\u4e3a\u6570\u636e\u5f02\u5e38\u800c\u88ab\u5c01\u7981!");
//                    ((OfflinePlayer)p).setBanned(true);
//                }
//            }
//        }.runTask(FakeItemCheckerReloaded.plugin);
//    }
//}
