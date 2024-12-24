package com.olziedev.nexorecipesgui.action;

import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.utils.Configuration;
import com.olziedev.nexorecipesgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public enum ActionType {

    PLAYER(Bukkit::dispatchCommand),
    SERVER((player, value) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value)),
    BROADCAST((player, value) -> Bukkit.broadcastMessage(Utils.color(value))),
    CONSOLE((player, value) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value)),
    CLOSE((player, value) -> player.closeInventory()),
    MESSAGE((player, value) -> Utils.sendMessage(player, value)),
    TITLE((player, value) -> {
        int fadein = Configuration.getConfig().getInt("settings.title-fadein", 10);
        int stay = Configuration.getConfig().getInt("settings.title-stay", 70);
        int fadeout = Configuration.getConfig().getInt("settings.title-fadeout", 20);
        player.sendTitle(Utils.color(value.split("\n")[0]), Utils.color(value.split("\n")[1]), fadein, stay, fadeout);
    });

    private final BiConsumer<Player, String> action;

    ActionType(BiConsumer<Player, String> action) {
        this.action = action;
    }

    public void execute(Player player, String value) {
        if (this.action == null) return;

        Bukkit.getScheduler().runTask(NexoRecipesGUI.getInstance(), () ->
                this.action.accept(player, (player == null ? value : value.replace("%player%", player.getName()))));
    }

    public static ActionType parse(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (Exception ignored) {}

        return null;
    }
}
