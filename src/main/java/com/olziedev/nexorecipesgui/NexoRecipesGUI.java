package com.olziedev.nexorecipesgui;

import com.olziedev.olziecommand.v1_3_3.OlzieCommand;
import com.olziedev.olziecommand.v1_3_3.framework.action.CommandActionType;
import com.olziedev.nexorecipesgui.events.RecipeEvent;
import com.olziedev.nexorecipesgui.managers.AddonManager;
import com.olziedev.nexorecipesgui.managers.MenuManager;
import com.olziedev.nexorecipesgui.utils.Configuration;
import com.olziedev.nexorecipesgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NexoRecipesGUI extends JavaPlugin {

    private static NexoRecipesGUI instance;

    private MenuManager menuManager;
    private AddonManager addonManager;

    @Override
    public void onLoad() {
        this.addonManager = new AddonManager(this);
        this.addonManager.setup();
    }

    @Override
    public void onEnable() {
        instance = this;
        new Configuration(this).load();

        this.addonManager.load();
        this.menuManager = new MenuManager(this);
        this.menuManager.setup();
        this.menuManager.load();

        new OlzieCommand(this, getClass())
                .getActionRegister()
                .registerAction(CommandActionType.CMD_NO_PERMISSION, cmd -> {
                    Utils.sendMessage(cmd.getSender(), Configuration.getConfig().getString("lang.no-permission"));
                })
                .registerAction(CommandActionType.CMD_NOT_PLAYER, cmd -> {
                    Utils.sendMessage(cmd.getSender(), Configuration.getConfig().getString("lang.entites-only"));
                }).buildActions().registerCommands(); // automatically register commands

        Bukkit.getPluginManager().registerEvents(new RecipeEvent(), this);
    }

    @Override
    public void onDisable() {

    }

    public static NexoRecipesGUI getInstance() {
        return instance;
    }

    public static MenuManager getMenuManager() {
        return instance.menuManager;
    }

    public static AddonManager getAddonManager() {
        return instance.addonManager;
    }
}
