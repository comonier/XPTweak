package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class StorageHandler {

    private final XPTweak plugin;

    public StorageHandler(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void handleMax(Player player, String[] args) {
        if (!player.hasPermission("xptweak.user.store")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }

        int levels = player.getLevel();
        if (levels <= 0) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return;
        }

        int totalPoints = plugin.getXpManager().getTotalExperience(player);
        int bottleCount = totalPoints / 7;
        if (bottleCount <= 0) bottleCount = 1;

        boolean forceDrop = args.length > 1 && args[1].equalsIgnoreCase("confirm");
        
        if (!hasInventorySpace(player, bottleCount) && !forceDrop) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            player.sendMessage(plugin.getMessage("inventory-full-confirm").replace("{command}", "/xpt max"));
            return;
        }

        player.setLevel(0);
        player.setExp(0);
        
        giveOrDropItems(player, new ItemStack(Material.EXPERIENCE_BOTTLE, bottleCount), forceDrop);
        sendStorageLogs(player, levels, totalPoints);
    }

    public void handleLvl(Player player, String[] args) {
        if (!player.hasPermission("xptweak.user.store")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        if (args.length < 2) {
            player.sendMessage(plugin.getMessage("syntax-lvl"));
            return;
        }

        try {
            int levelsToStore = Integer.parseInt(args[1]);
            if (player.getLevel() < levelsToStore) {
                player.sendMessage(plugin.getMessage("not-enough-xp"));
                return;
            }

            boolean forceDrop = args.length > 2 && args[2].equalsIgnoreCase("confirm");
            
            int currentPoints = plugin.getXpManager().getTotalExperience(player);
            int pointsAfter = plugin.getXpManager().getExpAtLevel(player.getLevel() - levelsToStore);
            int pointsToRemove = currentPoints - pointsAfter;

            int bottleCount = pointsToRemove / 7;
            if (bottleCount <= 0) bottleCount = 1;

            if (!hasInventorySpace(player, bottleCount) && !forceDrop) {
                player.sendMessage(plugin.getMessage("inventory-full"));
                player.sendMessage(plugin.getMessage("inventory-full-confirm").replace("{command}", "/xpt lvl " + levelsToStore));
                return;
            }

            player.setLevel(player.getLevel() - levelsToStore);
            giveOrDropItems(player, new ItemStack(Material.EXPERIENCE_BOTTLE, bottleCount), forceDrop);
            sendStorageLogs(player, levelsToStore, pointsToRemove);

        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }

    private boolean hasInventorySpace(Player player, int amount) {
        int freeSlots = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                freeSlots += 64;
            } else if (item.getType() == Material.EXPERIENCE_BOTTLE) {
                freeSlots += (64 - item.getAmount());
            }
        }
        return freeSlots >= amount;
    }

    private void giveOrDropItems(Player player, ItemStack items, boolean forceDrop) {
        if (forceDrop) {
            player.getWorld().dropItemNaturally(player.getLocation(), items);
        } else {
            Map<Integer, ItemStack> leftOver = player.getInventory().addItem(items);
            if (!leftOver.isEmpty()) {
                leftOver.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            }
        }
    }

    private void sendStorageLogs(Player player, int levels, int points) {
        String webhookUrl = plugin.getConfig().getString("webhooks.donations");
        plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-bottle-storage")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(levels))
                .replace("{points}", String.valueOf(points)));

        player.sendMessage(plugin.getMessage("xp-stored")
                .replace("{amount}", String.valueOf(levels))
                .replace("{points}", String.valueOf(points)));
    }
}
