package com.olziedev.nexorecipesgui.utils;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {

    public static String color(String s) {
        if (s == null || s.trim().isEmpty()) return "";

        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void sendMessage(CommandSender sender, String s) {
        if (s == null || s.trim().isEmpty() || sender == null) return;

        s = color(s);
        if (!(sender instanceof Player)) {
            Bukkit.getServer().getConsoleSender().sendMessage(s);
            return;
        }
        sender.sendMessage(s);
    }

    public static List<Integer> getSlots(ConfigurationSection section, String path) {
        if (section == null || path == null) return Collections.emptyList();

        path = path + ".slot";
        List<Integer> slots = getSlot(section, path);
        slots = slots.isEmpty() ? getSlot(section, path + "s") : slots;
        return slots;
    }

    public static List<Integer> getSlot(ConfigurationSection section, String path) {
        if (section == null || path == null) return Collections.emptyList();

        int lastIndexOf = path.lastIndexOf(".");
        String material = section.getString((lastIndexOf == -1 ? "" : path.substring(0, lastIndexOf + 1)) + "material");
        if (material != null && (material.trim().isEmpty() || material.equalsIgnoreCase("AIR")))
            return Collections.emptyList();

        List<String> list = section.getStringList(path);
        return (list.isEmpty() ? Collections.singletonList(section.getString(path)) : list).stream().filter(Objects::nonNull).flatMap(x -> getRange(x).stream()).collect(Collectors.toList());
    }

    public static List<Integer> getRange(String t) {
        if (t == null) return Collections.emptyList();

        String[] split = t.split("-");
        return IntStream.range(org.apache.commons.lang.math.NumberUtils.toInt(split[0], -1), NumberUtils.toInt(split[split.length == 2 && !split[0].isEmpty() ? 1 : 0], -2) + 1).boxed().collect(Collectors.toList());
    }
}
