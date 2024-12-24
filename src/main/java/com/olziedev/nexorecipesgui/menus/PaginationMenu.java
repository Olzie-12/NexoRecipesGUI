package com.olziedev.nexorecipesgui.menus;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.PaginationMenuAdapter;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.managers.MenuManager;
import com.olziedev.nexorecipesgui.entites.GUIPlayer;
import com.olziedev.nexorecipesgui.utils.Utils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

public abstract class PaginationMenu extends PaginationMenuAdapter implements MenuFactory {

    protected final ConfigurationSection section;
    private final MenuManager menuManager;

    protected SearchMenu searchMenu;

    public PaginationMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section.getInt("size") / 9, Utils.color(section.getString("title", "")), section.getStringList("blacklisted-slots").stream().map(x -> NumberUtils.toInt(x, -1)).filter(x -> x >= 0).collect(Collectors.toList()), olzieMenu);
        this.section = section;
        this.menuManager = NexoRecipesGUI.getMenuManager();
    }

    @Override
    public void load() {

    }

    public boolean handleSearch(Player player, int slot) {
        ConfigurationSection search = this.section.getConfigurationSection("search");
        if (search == null || !Utils.getSlots(search, "item").contains(slot) || !search.getBoolean("enabled")) return false;

        this.removeDataAndDontClose(player);
        this.searchMenu.openSearch(player);
        return true;
    }

    public GUIPlayer getGUIPlayer(Player player) {
        return this.getGUIPlayer(player.getUniqueId());
    }

    public GUIPlayer getGUIPlayer(UUID uuid) {
        return this.menuManager.getGUIPlayer(uuid);
    }

    public void removeGUIPlayer(Player player) {
        this.removeGUIPlayer(player.getUniqueId());
    }

    public void removeGUIPlayer(UUID uuid) {
        this.menuManager.removeGUIPlayer(uuid);
    }

    @Override
    public ConfigurationSection getSection() {
        return this.section;
    }

    @Override
    public MenuManager getMenuManager() {
        return this.menuManager;
    }

    @Override
    public SearchMenu getSearchMenu() {
        return this.searchMenu;
    }
}
