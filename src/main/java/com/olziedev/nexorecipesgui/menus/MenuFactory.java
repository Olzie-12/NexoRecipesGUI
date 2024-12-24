package com.olziedev.nexorecipesgui.menus;

import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.nexorecipesgui.managers.MenuManager;
import com.olziedev.nexorecipesgui.utils.ItemFactory;
import com.olziedev.nexorecipesgui.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface MenuFactory extends ItemFactory {

    ConfigurationSection getSection();

    void dontActivateClose(Player player);

    SearchMenu getSearchMenu();

    MenuManager getMenuManager();

    default boolean handleSearchAuctions(Player player, int slot) {
        ConfigurationSection search = this.getSection().getConfigurationSection("search");
        if (search == null || !Utils.getSlots(search, "item").contains(slot) || !search.getBoolean("enabled")) return false;

        this.dontActivateClose(player);
        this.getSearchMenu().openSearch(player);
        return true;
    }

    default void addSearchItem(FrameworkMenu menu) {
        if (!this.getSection().getBoolean("search.enabled")) return;

        ConfigurationSection perSection = this.getSection().getConfigurationSection("search.item");
        ItemStack search = this.createItem(perSection);
        Utils.getSlots(this.getSection(), "search.item").forEach(slot -> menu.setItem(slot, search));
    }
}
