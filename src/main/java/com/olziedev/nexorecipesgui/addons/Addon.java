package com.olziedev.nexorecipesgui.addons;

import com.olziedev.nexorecipesgui.NexoRecipesGUI;

public abstract class Addon {

    public final NexoRecipesGUI plugin;

    public Addon(NexoRecipesGUI plugin) {
        this.plugin = plugin;
    }

    public abstract void load();

    public void preLoad() {}
}
