package com.olziedev.nexorecipesgui.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.addons.ItemParserAddon;
import com.olziedev.nexorecipesgui.managers.AddonManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ItemFactory {

    static ItemFactory getInstance() {
        return new ItemFactory() {};
    }

    default ItemStack createItem(ConfigurationSection section) {
        return createItem(section, null, null);
    }

    default ItemStack createItem(ConfigurationSection section, Function<String, String> nameReplacements, Function<List<String>, List<String>> loreReplacements) {
        if (section == null) return null; // If the section is null, return null.

        try {
            String upper = Configuration.getString(section, "material");
            ItemStack overridenStack = null;
            AddonManager expansionRegistry = NexoRecipesGUI.getAddonManager();
            for (ItemParserAddon parserExpansion : expansionRegistry.getAddons(ItemParserAddon.class)) {
                overridenStack = parserExpansion.getItemStack(upper);
            }
            if (overridenStack == null) {
                Material material = Material.getMaterial(nameReplacements == null ? upper.toUpperCase() : nameReplacements.apply(upper)); // Get the material from the config.
                if (material == getSkullMaterial() && (section.getString("owner") != null || section.getString("texture") != null)) // If the material is a skull and the owner or texture is set, create a skull.
                    return createSkull(section, nameReplacements, loreReplacements);

                if (material == null || material == Material.AIR) {
                    return null;
                }
                overridenStack = new ItemStack(material, section.getInt("amount", 1), (short) section.getInt("data")); // Create the item stack.
            }
            return addBaseItemMeta(overridenStack, overridenStack.getItemMeta(), section, nameReplacements, loreReplacements, false); // Add the base item meta.
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    default ItemStack createSkull(ConfigurationSection section, Function<String, String> nameReplacements, Function<List<String>, List<String>> loreReplacements) {
        if (section == null) return null;

        ItemStack itemStack = new ItemStack(getSkullMaterial(), section.getInt("amount", 1), (short) section.getInt("data")); // Create the item stack.
        return addBaseItemMeta(itemStack, applyBasicSkull(itemStack, section, nameReplacements), section, nameReplacements, loreReplacements, false); // Add the base item meta.
    }

    default SkullMeta applyBasicSkull(ItemStack itemStack, ConfigurationSection section, Function<String, String> nameReplacements) {
        SkullMeta im = (SkullMeta) itemStack.getItemMeta(); // Get the item meta.
        String owner = Configuration.getString(section, "owner"); // Get the owner from the config.
        if (!owner.isEmpty()) im.setOwner(nameReplacements == null ? owner : nameReplacements.apply(owner)); // If the owner is not empty, set the owner.

        String texture = Configuration.getString(section, "texture"); // Get the texture from the config.
        if (!texture.isEmpty()) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null); // Create a new game profile.
            profile.getProperties().put("textures", new Property("textures", texture)); // Set the texture.

            try {
                Field profileField = im.getClass().getDeclaredField("profile"); // Get the profile field.
                profileField.setAccessible(true); // Set the field to accessible.
                profileField.set(im, profile); // Set the profile.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return im; // Return the item meta.
    }

    default ItemStack addBaseItemMeta(ItemStack itemStack, ItemMeta im, ConfigurationSection section, Function<String, String> nameReplacements, Function<List<String>, List<String>> loreReplacements, boolean item) {
        if (item && nameReplacements == null && loreReplacements == null) return itemStack;
        if (section == null || itemStack.getAmount() <= 0) return null; // If the section is null or the amount is 0, return null.

        String name = Utils.color(section.getString("name")); // Get the name from the config.
        if (!name.isEmpty()) im.setDisplayName(nameReplacements == null ? name : nameReplacements.apply(name)); // Set the display name.

        List<String> lore = section.getStringList("lore"); // Get the lore from the config.
        List<String> currentLore = im.hasLore() ? im.getLore() : null; // Get the current lore.
        if (currentLore != null && item) currentLore.forEach(s -> lore.add(s));

        im.setLore((loreReplacements == null ? lore : loreReplacements.apply(lore).stream().flatMap(x -> Arrays.stream(x.split("\n"))).toList()).stream().map(x -> {
            return x;
        }).filter(x -> !x.contains("\b")).map(Utils::color).collect(Collectors.toList())); // Set the lore.

        if (section.getBoolean("glowing")) { // If the item is glowing,
            im.addEnchant(Enchantment.UNBREAKING, 1, true); // Add the durability enchantment.
            try {
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS); // Add the hide enchants flag.
            } catch (NoClassDefFoundError ignored) {
            }
        }
        try {
            section.getStringList("item-flags").forEach(x -> im.addItemFlags(ItemFlag.valueOf(x))); // Add the item flags.
        } catch (NoClassDefFoundError ignored) {
        }
        try {
            section.getStringList("enchantments").forEach(x -> im.addEnchant(Enchantment.getByName(x.split(":")[0].toUpperCase()), Integer.parseInt(x.split(":")[1]), true)); // Add the enchantments.
        } catch (NoClassDefFoundError ignored) {
        }
        try {
            int customModelData = section.getInt("custom-model-data", -1); // Get the custom model data.
            if (customModelData != -1) im.setCustomModelData(customModelData); // Set the custom model data.
        } catch (Throwable ignored) {}
        itemStack.setItemMeta(im); // Set the item meta.
        return itemStack; // Return the item stack.
    }

    default Material getSkullMaterial() {
        Material icon = Material.getMaterial("PLAYER_HEAD"); // Get the entites head material.
        if (icon == null) return Material.getMaterial("SKULL_ITEM"); // If the entites head material is null, get the skull item material.
        return icon;
    }

    default ItemStack makeGlow(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }
}
