package com.olziedev.nexorecipesgui.menus;

import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.addons.NexoAddon;
import com.olziedev.nexorecipesgui.addons.ProtocolLibAddon;
import com.olziedev.nexorecipesgui.entites.NexoRecipe;
import com.olziedev.nexorecipesgui.managers.AddonManager;
import com.olziedev.nexorecipesgui.managers.MenuManager;
import com.olziedev.nexorecipesgui.entites.GUIPlayer;
import com.olziedev.nexorecipesgui.utils.Configuration;
import com.olziedev.nexorecipesgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SearchMenu {

    protected static MenuManager menuManager;
    private static AddonManager addonManager;
    protected final MenuFactory menu;
    protected final String title;

    public SearchMenu(MenuFactory menu) {
        this.menu = menu;
        this.title = Utils.color(menu.getSection().getString("search.title"));
        menuManager = NexoRecipesGUI.getMenuManager();
        addonManager = NexoRecipesGUI.getAddonManager();
    }

    public void openSearch(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(menuManager.plugin, () -> {
            try {
                List<String> list = this.menu.getSection().getStringList("search.lines");
                int index = !list.contains("%search%") ? 0 : list.indexOf("%search%");
                ProtocolLibAddon.newSignEditor(list, index, player, lines -> {
                    menuManager.getGUIPlayer(player).setSearch(ChatColor.stripColor(lines[index]));
                    this.open(player);
                    return true;
                }, Material.getMaterial(Configuration.getString(this.menu.getSection(), "material")));
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    public FrameworkMenu open(Player player) {
        GUIPlayer guiPlayer = menuManager.getGUIPlayer(player);
        String search = guiPlayer.getSearch();
        try {
            List<NexoRecipe> items = new ArrayList<>(addonManager.getAddon(NexoAddon.class).getNexoRecipes().stream().filter(x -> search.trim().isEmpty() || x.isSearch(search)).collect(Collectors.toList()));
            this.menu.dontActivateClose(player);
            guiPlayer.setNexoRecipes(items);
            return this.open(player, guiPlayer, items);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public abstract FrameworkMenu open(Player player, GUIPlayer guiPlayer, List<NexoRecipe> nexoRecipes);
}
