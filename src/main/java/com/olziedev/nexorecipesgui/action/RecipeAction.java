package com.olziedev.nexorecipesgui.action;

import org.bukkit.entity.Player;

public class RecipeAction {

    private final ActionType actionType;
    private final String value;

    public RecipeAction(ActionType actionType, String value) {
        this.actionType = actionType;
        this.value = value;
    }

    public void execute(Player player) {
        if (this.actionType == null) return;

        this.actionType.execute(player, this.value);
    }
}
