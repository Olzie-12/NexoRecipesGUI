package com.olziedev.nexorecipesgui.entites;

import com.nexomc.nexo.recipes.CustomRecipe;
import com.nexomc.nexo.recipes.listeners.RecipeEventManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class NexoRecipe {

    private final CustomRecipe customRecipe;
    private final ItemStack itemStack;

    public NexoRecipe(CustomRecipe customRecipe) {
        this.customRecipe = customRecipe;
        this.itemStack = new ItemStack(customRecipe.getResult());
    }

    public boolean isSearch(String search) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) return false;

        String displayname = ChatColor.stripColor(itemMeta.getDisplayName()).toLowerCase();
        String name = ChatColor.stripColor(itemMeta.getItemName()).toLowerCase();
        List<String> lore = itemMeta.getLore();
        if (lore != null) {
            for (String line : lore) {
                if (ChatColor.stripColor(line).toLowerCase().contains(search.toLowerCase())) return true;
            }
        }
        return name.contains(search.toLowerCase()) || name.startsWith(search.toLowerCase()) || displayname.contains(search.toLowerCase()) || displayname.startsWith(search.toLowerCase()) || this.customRecipe.name.contains(search.toLowerCase()) || this.customRecipe.name.toLowerCase().startsWith(search.toLowerCase());
    }

    public boolean canSee(Player player) {
        return RecipeEventManager.instance().hasPermission(player, this.customRecipe);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getId() {
        return customRecipe.name;
    }
}
