package com.olziedev.nexorecipesgui.managers;

import com.olziedev.nexorecipesgui.NexoRecipesGUI;

public abstract class Manager {

    public final NexoRecipesGUI plugin;

    public Manager(NexoRecipesGUI plugin) {
        this.plugin = plugin;
    }

    public abstract void load();

    public abstract void setup();

    public void close() {} // default
}
