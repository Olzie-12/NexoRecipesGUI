package com.olziedev.nexorecipesgui.managers;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.MenuAdapter;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.entites.GUIPlayer;
import com.olziedev.nexorecipesgui.menus.guis.RecipesMenu;
import com.olziedev.nexorecipesgui.utils.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuManager extends Manager {

    private final List<MenuAdapter> menus;
    private final Map<UUID, GUIPlayer> players = new ConcurrentHashMap<>();

    private final OlzieMenu olzieMenu;

    public MenuManager(NexoRecipesGUI plugin) {
        super(plugin);
        menus = new ArrayList<>();
        this.olzieMenu = new OlzieMenu(plugin).getActionRegister().buildActions();
    }

    @Override
    public void setup() {
        menus.add(new RecipesMenu(Configuration.getConfig().getConfigurationSection("menus.recipes"), olzieMenu));
    }

    @Override
    public void load() {
        menus.forEach(MenuAdapter::load);
    }

    @SuppressWarnings("unchecked")
    public <T extends MenuAdapter> T getMenu(Class<T> clazz) {
        return menus.stream().filter(x -> x.getClass().equals(clazz)).map(x -> (T) x).findFirst().orElse(null);
    }

    @Override
    public void close() {
        menus.clear();
    }

    public void closeMenus() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!this.olzieMenu.isOlzieMenu(player.getOpenInventory().getTopInventory())) continue;

            player.closeInventory();
        }
    }

    public OlzieMenu getOlzieMenu() {
        return this.olzieMenu;
    }

    public GUIPlayer getGUIPlayer(UUID uuid) {
        return players.computeIfAbsent(uuid, GUIPlayer::new);
    }

    public GUIPlayer getGUIPlayer(Player player) {
        return getGUIPlayer(player.getUniqueId());
    }

    public void removeGUIPlayer(Player player) {
        removeGUIPlayer(player.getUniqueId());
    }

    public void removeGUIPlayer(UUID uuid) {
        players.remove(uuid);
    }
}
