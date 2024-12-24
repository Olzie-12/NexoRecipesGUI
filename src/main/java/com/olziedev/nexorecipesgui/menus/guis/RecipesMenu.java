package com.olziedev.nexorecipesgui.menus.guis;

import com.olziedev.olziemenu.OlzieMenu;
import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.entites.GUIPlayer;
import com.olziedev.nexorecipesgui.entites.NexoRecipe;
import com.olziedev.nexorecipesgui.menus.PaginationMenu;
import com.olziedev.nexorecipesgui.menus.search.SearchImpl;
import com.olziedev.nexorecipesgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RecipesMenu extends PaginationMenu implements Listener {

    private final List<ConfigurationSection> nextItem = new ArrayList<>();
    private final List<ConfigurationSection> previousItem = new ArrayList<>();

    public RecipesMenu(ConfigurationSection section, OlzieMenu olzieMenu) {
        super(section, olzieMenu);
        this.searchMenu = new SearchImpl(this);

        Bukkit.getPluginManager().registerEvents(this, this.plugin);
        ConfigurationSection clickableItems = section.getConfigurationSection("clickable-items");
        if (clickableItems == null) return;

        for (String keys : clickableItems.getKeys(false)) {
            if (keys.startsWith("next")) {
                nextItem.add(clickableItems.getConfigurationSection(keys));
            }
            if (keys.startsWith("previous")) {
                previousItem.add(clickableItems.getConfigurationSection(keys));
            }
        }
    }

    @Override
    public void load() {
        ConfigurationSection itemSection = this.section.getConfigurationSection("items");
        if (itemSection == null) return;

        for (String keys : itemSection.getKeys(false)) {
            ConfigurationSection item = itemSection.getConfigurationSection(keys);
            if (item == null) continue;

            Utils.getSlots(item, "").forEach(x -> this.setItem(x, this.createItem(item)));
        }
    }

    @Override
    public FrameworkMenu open(Player player, Consumer<Inventory> consumer, Function<String, String> function) {
        GUIPlayer guiPlayer = this.getGUIPlayer(player);
        FrameworkMenu menu = super.build(x -> guiPlayer.getTitle() == null ? x : guiPlayer.getTitle());
        this.addSearchItem(menu);
        List<PageItem> pageItems = this.getItems(player);
        for (NexoRecipe nexoRecipe : !pageItems.isEmpty() ? Collections.<NexoRecipe>emptyList() : guiPlayer.getNexoRecipes()) {
            if (!nexoRecipe.canSee(player)) continue;

            pageItems.add(new PageItem<>(nexoRecipe::getItemStack, (click, item) -> {
                this.dontActivateClose(player);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    guiPlayer.setThirdParty(true);
                    player.performCommand("nexo recipes show " + nexoRecipe.getId());
                });
            }, nexoRecipe));
        }
        this.nextItem.forEach(x -> Utils.getSlots(x, "").forEach(slot -> menu.setItem(slot, this.createItem(x))));
        this.previousItem.forEach(x -> Utils.getSlots(x, "").forEach(slot -> menu.setItem(slot, this.createItem(x))));
        this.open(player, pageItems, menu, consumer);
        return menu;
    }

    @Override
    public boolean onMenuClick(InventoryClickEvent event, FrameworkMenu menu) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if (handleSearch(player, slot)) return true;

        if (Utils.getSlots(this.section, "clickable-items.next").contains(slot)) {
            this.nextPage(player);
            return true;
        }
        if (Utils.getSlots(this.section, "clickable-items.previous").contains(slot)) {
            this.previousPage(player);
            return true;
        }
        return super.onMenuClick(event, menu);
    }

    @Override
    public boolean onMenuClose(InventoryCloseEvent event, FrameworkMenu menu) {
        Player player = (Player) event.getPlayer();
        GUIPlayer guiPlayer = this.getGUIPlayer(player);
        Bukkit.getScheduler().runTaskLater(NexoRecipesGUI.getInstance(), () -> {
            if (guiPlayer.isThirdParty() || this.olzieMenu.isOlzieMenu(player.getOpenInventory().getTopInventory())) return;

            this.removeGUIPlayer(player);
        }, 1L);
        return super.onMenuClose(event, menu);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        GUIPlayer guiPlayer = this.getGUIPlayer(player);
        if (!guiPlayer.isThirdParty() || this.olzieMenu.isOlzieMenu(event.getInventory())) return;

        guiPlayer.setThirdParty(false);
        Bukkit.getScheduler().runTaskLater(plugin, () -> this.open(player), 1L);
    }
}
