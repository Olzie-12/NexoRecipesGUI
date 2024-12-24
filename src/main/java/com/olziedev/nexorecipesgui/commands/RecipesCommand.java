package com.olziedev.nexorecipesgui.commands;

import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.olziecommand.v1_3_3.framework.CommandExecutor;
import com.olziedev.olziecommand.v1_3_3.framework.ExecutorType;
import com.olziedev.olziecommand.v1_3_3.framework.api.FrameworkCommand;
import com.olziedev.nexorecipesgui.addons.NexoAddon;
import com.olziedev.nexorecipesgui.entites.GUIPlayer;
import com.olziedev.nexorecipesgui.managers.AddonManager;
import com.olziedev.nexorecipesgui.managers.MenuManager;
import com.olziedev.nexorecipesgui.menus.guis.RecipesMenu;
import org.bukkit.entity.Player;

public class RecipesCommand extends FrameworkCommand {

    private final MenuManager menuManager;
    private final AddonManager addonManager;

    public RecipesCommand() {
        super("recipes");
        this.setExecutorType(ExecutorType.PLAYER_ONLY);
        this.setDescription("Open the custom recipes GUI");
        menuManager = NexoRecipesGUI.getMenuManager();
        addonManager = NexoRecipesGUI.getAddonManager();
    }

    @Override
    public void onExecute(CommandExecutor cmd) {
        Player player = (Player) cmd.getSender();
        GUIPlayer guiPlayer = menuManager.getGUIPlayer(player);
        RecipesMenu recipesMenu = menuManager.getMenu(RecipesMenu.class);
        guiPlayer.setNexoRecipes(addonManager.getAddon(NexoAddon.class).getNexoRecipes());
        guiPlayer.setThirdParty(false);
        recipesMenu.open(player);
    }
}
