package com.olziedev.nexorecipesgui.entites;

import java.util.List;
import java.util.UUID;

public class GUIPlayer {

    private final UUID uuid;

    private String search;
    private String title;
    private boolean isThirdParty;
    private List<NexoRecipe> nexoRecipes;

    public GUIPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getSearch() {
        return this.search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NexoRecipe> getNexoRecipes() {
        return this.nexoRecipes;
    }

    public void setNexoRecipes(List<NexoRecipe> nexoRecipes) {
        this.nexoRecipes = nexoRecipes;
    }

    public boolean isThirdParty() {
        return isThirdParty;
    }

    public void setThirdParty(boolean thirdParty) {
        isThirdParty = thirdParty;
    }
}
