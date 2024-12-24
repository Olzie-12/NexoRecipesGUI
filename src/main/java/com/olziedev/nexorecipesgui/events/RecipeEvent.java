package com.olziedev.nexorecipesgui.events;

import com.nexomc.nexo.api.NexoItems;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.action.ActionType;
import com.olziedev.nexorecipesgui.action.RecipeAction;
import com.olziedev.nexorecipesgui.utils.Configuration;
import com.olziedev.nexorecipesgui.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeEvent implements Listener {

    private final Map<String, List<RecipeAction>> commands = new ConcurrentHashMap<>();
    private final NamespacedKey key = new NamespacedKey(NexoRecipesGUI.getInstance(), "remove");

    public RecipeEvent() {
        ConfigurationSection section = Configuration.getConfig().getConfigurationSection("commands");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            List<RecipeAction> actions = new ArrayList<>();
            for (String action : section.getStringList(key)) {
                Matcher actionMatcher = Pattern.compile("\\[(.*?)\\]").matcher(action);
                if (!actionMatcher.find()) continue;

                String matcher = actionMatcher.group();
                ActionType actionType = ActionType.parse(matcher.replaceAll("[\\[\\]]", ""));
                actions.add(new RecipeAction(actionType, Utils.color(action.replace(matcher, "")).trim()));
            }
            commands.put(key, actions);
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null) return;

        String id = NexoItems.idFromItem(result);
        if (id == null || !commands.containsKey(id)) return;

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "true");
        result.setItemMeta(meta);
        event.getInventory().setResult(result);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null) return;

        String id = NexoItems.idFromItem(result);
        if (id == null || !commands.containsKey(id)) return;

        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        Bukkit.getScheduler().runTaskLater(NexoRecipesGUI.getInstance(), () -> {
            List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getInventory().getContents()));
            items.add(player.getItemOnCursor());
            for (ItemStack item : items) {
                if (item == null || item.getType() == Material.AIR) continue;

                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta == null) continue;

                if (itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                    commands.get(id).forEach(x -> {
                        for (int i = 0; i < item.getAmount(); i++) {
                            x.execute(player);
                        }
                    });
                    item.setAmount(0);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
