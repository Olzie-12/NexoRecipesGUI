package com.olziedev.nexorecipesgui.addons;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.olziedev.nexorecipesgui.NexoRecipesGUI;
import com.olziedev.nexorecipesgui.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

public class ProtocolLibAddon extends Addon {

    private static Map<UUID, SignEditor> inputReceivers;

    public ProtocolLibAddon(NexoRecipesGUI plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        inputReceivers = new HashMap<>();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return; // apparently entites can be null now wtf?

                SignEditor menu = inputReceivers.remove(player.getUniqueId());
                if (menu == null) return;

                event.setCancelled(true);
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    boolean success;
                    try {
                        success = menu.response.test(event.getPacket().getStringArrays().read(0));
                    } catch (FieldAccessException ex) {
                        success = menu.response.test(Arrays.stream(event.getPacket().getChatComponentArrays().read(0)).map(x -> TextComponent.toLegacyText(ComponentSerializer.parse(x.getJson()))).toArray(String[]::new));
                    }
                    if (!success) {
                        Bukkit.getScheduler().runTaskLater(plugin, s2 -> menu.open(player, menu.material), 2L);
                        return;
                    }
                    Location location = menu.position.toLocation(player.getWorld());
                    Bukkit.getScheduler().runTask(plugin, t -> menu.blockChange(player, location, menu.block, null));
                });
            }
        });
    }

    public static void newSignEditor(List<String> list, int index, Player player, Predicate<String[]> lines, Material material) {
        list.set(index, "");

        NexoRecipesGUI.getAddonManager().getAddon(ProtocolLibAddon.class).newSignEditor(list).response(lines).open(player, material);
    }

    public SignEditor newSignEditor(List<String> text) {
        return new SignEditor(text);
    }

    public static class SignEditor {

        private final List<String> text;
        private Predicate<String[]> response;
        private BlockPosition position;
        public Block block;
        public Material material;

        SignEditor(List<String> text) {
            this.text = text;
        }

        public SignEditor response(Predicate<String[]> response) {
            this.response = response;
            return this;
        }

        public void open(Player player, Material material) {
            Location location = player.getLocation();
            boolean lookingUp = location.getPitch() < -45;
            this.position = new BlockPosition(location.getBlockX(), location.getBlockY() + (!lookingUp ? 7 : -7), location.getBlockZ());
            Location blockLocation = this.position.toLocation(player.getWorld());
            this.block = blockLocation.getBlock();

            PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
            try {
                while (text.size() < 4) text.add("");

                this.blockChange(player, blockLocation, null, material);
                openSign.getBlockPositionModifier().write(0, this.position);
                try {
                    openSign.getBooleans().write(0, true);
                } catch (Throwable ignored) {}

                player.sendSignChange(blockLocation, text.stream().map(Utils::color).toArray(String[]::new));
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            inputReceivers.put(player.getUniqueId(), this);
        }

        @SuppressWarnings("deprecation")
        private void blockChange(Player player, Location location, Block block, Material material) {
            if (material == null) material = Material.getMaterial("OAK_SIGN");
            if (material == null) material = Material.getMaterial("SIGN_POST");

            try {
                player.sendBlockChange(location, block == null ? material.createBlockData() : block.getBlockData());
            } catch (Throwable ignored) {
                player.sendBlockChange(location, block == null ? material : block.getType(), block == null ? (byte) 0 : block.getData());
            }
        }
    }
}
