package com.olziedev.nexorecipesgui.menus.search;

import com.olziedev.olziemenu.framework.menu.FrameworkMenu;
import com.olziedev.nexorecipesgui.entites.GUIPlayer;
import com.olziedev.nexorecipesgui.entites.NexoRecipe;
import com.olziedev.nexorecipesgui.menus.MenuFactory;
import com.olziedev.nexorecipesgui.menus.SearchMenu;
import com.olziedev.nexorecipesgui.menus.guis.RecipesMenu;
import org.bukkit.entity.Player;

import java.util.List;

public class SearchImpl extends SearchMenu {

    public SearchImpl(MenuFactory menu) {
        super(menu);
    }

    @Override
    public FrameworkMenu open(Player player, GUIPlayer guiPlayer, List<NexoRecipe> nexoRecipes) {
        guiPlayer.setTitle(this.title.replace("%search%", guiPlayer.getSearch()));
        return menuManager.getMenu(RecipesMenu.class).open(player);
    }
}
