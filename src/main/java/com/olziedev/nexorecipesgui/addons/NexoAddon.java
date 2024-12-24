package com.olziedev.nexorecipesgui.addons;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.recipes.CustomRecipe;
import com.nexomc.nexo.recipes.listeners.RecipeEventManager;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.entites.NexoRecipe;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class NexoAddon extends ItemParserAddon {

    private final List<NexoRecipe> nexoRecipes = new ArrayList<>();

    public NexoAddon(NexoRecipesGUI plugin) {
        super(plugin);
    }

    @Override
    public ItemStack getItemStack(String id) {
        if (id == null || !id.startsWith(this.getPrefix())) return null;

        return NexoItems.itemFromId(id).build();
    }

    @Override
    public String getPrefix() {
        return "nexo-";
    }

    @Override
    public void load() {
        if (!this.nexoRecipes.isEmpty()) return;

        for (CustomRecipe permittedRecipe : RecipeEventManager.instance().permittedRecipes(Bukkit.getConsoleSender())) {
            NexoRecipe nexoRecipe = new NexoRecipe(permittedRecipe);
            this.nexoRecipes.add(nexoRecipe);
        }
    }

    public List<NexoRecipe> getNexoRecipes() {
        this.load();
        return this.nexoRecipes;
    }
}
