package com.olziedev.nexorecipesgui.managers;


import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.addons.Addon;
import com.olziedev.nexorecipesgui.addons.NexoAddon;
import com.olziedev.nexorecipesgui.addons.ProtocolLibAddon;

import java.util.ArrayList;
import java.util.List;

public class AddonManager extends Manager {

    private final List<Addon> addons;

    public AddonManager(NexoRecipesGUI plugin) {
        super(plugin);
        this.addons = new ArrayList<>();
    }

    @Override
    public void setup() {
        addons.add(new NexoAddon(plugin));
        addons.add(new ProtocolLibAddon(plugin));
        addons.forEach(Addon::preLoad);
    }

    @Override
    public void load() {
        addons.forEach(Addon::load);
    }

    @SuppressWarnings("unchecked")
    public <T extends Addon> T getAddon(Class<T> clazz) {
        return addons.stream().filter(x -> x.getClass().equals(clazz)).map(x -> (T) x).findFirst().orElse(null);
    }

    public <T extends Addon> List<T> getAddons(Class<T> clazz) {
        return addons.stream().filter(x -> clazz.isAssignableFrom(x.getClass())).map(x -> (T) x).toList();
    }
}
