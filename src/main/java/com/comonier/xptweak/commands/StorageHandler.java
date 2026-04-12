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

    /**
     * Comando /xpt max ou /xpt all
     * Zera o XP do jogador e converte em garrafas. 
     * Se o inventário estiver cheio, exige 'confirm' para dropar no chão.
     */
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

        // CORREÇÃO: Verifica se o array tem tamanho suficiente antes de acessar o índice 1
        boolean forceDrop = args.length > 1 && args[1].equalsIgnoreCase("confirm");
        
        if (!hasInventorySpace(player, bottleCount) && !forceDrop) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            player.sendMessage(plugin.getMessage("inventory-full-confirm").replace("{command}", "/xpt max"));
            return;
        }

        // Zera o XP do jogador completamente
        player.setLevel(0);
        player.setExp(0);
        
        giveOrDropItems(player, new ItemStack(Material.EXPERIENCE_BOTTLE, bottleCount), forceDrop);
        sendStorageLogs(player, levels, totalPoints);
    }

    /**
     * NOVO Comando /xpt inv
     * Preenche apenas o espaço disponível no inventário e mantém o restante na barra.
     */
    public void handleInv(Player player) {
        if (!player.hasPermission("xptweak.user.store")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }

        int totalPoints = plugin.getXpManager().getTotalExperience(player);
        if (totalPoints < 7) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return;
        }

        int capacityInBottles = getFreeBottleCapacity(player);
        if (capacityInBottles <= 0) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            return;
        }

        int bottlesToGive = Math.min(totalPoints / 7, capacityInBottles);
        int pointsToRemove = bottlesToGive * 7;

        int oldLevel = player.getLevel();
        
        // Remove os pontos e atualiza a barra
        takePointsAndUpdateBar(player, pointsToRemove);
        
        player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, bottlesToGive));

        int levelsStored = oldLevel - player.getLevel();
        
        player.sendMessage(plugin.getMessage("inventory-partial-info")
                .replace("{stored}", String.valueOf(levelsStored))
                .replace("{remaining}", String.valueOf(player.getLevel())));
    }

    /**
     * Comando /xpt lvl <qtd>
     * Converte níveis específicos em garrafas.
     */
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

            // CORREÇÃO: Verifica se o array tem tamanho suficiente antes de acessar o índice 2
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

    private int getFreeBottleCapacity(Player player) {
        int count = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                count += 64;
            } else if (item.getType() == Material.EXPERIENCE_BOTTLE) {
                count += (64 - item.getAmount());
            }
        }
        return count;
    }

    private void takePointsAndUpdateBar(Player player, int points) {
        int currentPoints = plugin.getXpManager().getTotalExperience(player);
        int newTotal = Math.max(0, currentPoints - points);
        player.setLevel(0);
        player.setExp(0);
        player.giveExp(newTotal);
    }

    private boolean hasInventorySpace(Player player, int amount) {
        return getFreeBottleCapacity(player) >= amount;
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
