package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StorageHandler {

    private final XPTweak plugin;

    public StorageHandler(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void handleMax(Player player) {
        if (!player.hasPermission("xptweak.user.store")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }

        int levels = player.getLevel();
        if (levels <= 0) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return;
        }

        // Calcula o XP TOTAL em pontos que o jogador possui no momento
        int totalPoints = plugin.getXpManager().getTotalExperience(player);
        
        // Uma garrafa vanilla de XP dropa em média 7 de XP (range 3-11)
        int bottleCount = totalPoints / 7;

        if (bottleCount <= 0) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            return;
        }

        // Reseta o XP do jogador completamente
        player.setLevel(0);
        player.setExp(0);

        // Entrega as garrafas vanilla comuns
        player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, bottleCount));

        // Report para o Discord (Utilizando o canal de doações/logs gerais)
        String webhookUrl = plugin.getConfig().getString("webhooks.donations");
        plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-bottle-storage")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(levels))
                .replace("{points}", String.valueOf(totalPoints)));

        player.sendMessage(plugin.getMessage("xp-stored").replace("{amount}", String.valueOf(levels)));
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

            // Calcula a diferença de pontos entre o nível atual e o nível após a remoção
            int currentPoints = plugin.getXpManager().getTotalExperience(player);
            int pointsAfter = plugin.getXpManager().getExpAtLevel(player.getLevel() - levelsToStore);
            int pointsToRemove = currentPoints - pointsAfter;

            int bottleCount = pointsToRemove / 7;
            if (bottleCount <= 0) bottleCount = 1;

            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(plugin.getMessage("inventory-full"));
                return;
            }

            player.setLevel(player.getLevel() - levelsToStore);
            player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, bottleCount));

            // Report para o Discord
            String webhookUrl = plugin.getConfig().getString("webhooks.donations");
            plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-bottle-storage")
                    .replace("{player}", player.getName())
                    .replace("{amount}", String.valueOf(levelsToStore))
                    .replace("{points}", String.valueOf(pointsToRemove)));

            player.sendMessage(plugin.getMessage("xp-stored").replace("{amount}", String.valueOf(levelsToStore)));
            
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }
}
