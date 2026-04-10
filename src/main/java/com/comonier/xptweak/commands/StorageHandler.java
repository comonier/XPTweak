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

        // Calcula o XP TOTAL em pontos (não apenas níveis)
        int totalExperience = getTotalExperience(player);
        
        // Uma garrafa vanilla de XP dropa em média 7 de XP (3 a 11)
        // Dividimos o XP total por 7 para saber quantas garrafas dar.
        int bottleCount = totalExperience / 7;

        if (bottleCount <= 0) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            return;
        }

        // Reseta o XP do jogador
        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);

        // Entrega as garrafas vanilla
        ItemStack bottles = new ItemStack(Material.EXPERIENCE_BOTTLE, bottleCount);
        player.getInventory().addItem(bottles);

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

            // Calcula quanto XP (em pontos) representam esses níveis específicos
            int xpPoints = getExpToLevel(levelsToStore);
            int bottleCount = xpPoints / 7;

            if (bottleCount <= 0) bottleCount = 1;

            player.setLevel(player.getLevel() - levelsToStore);
            player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, bottleCount));
            player.sendMessage(plugin.getMessage("xp-stored").replace("{amount}", String.valueOf(levelsToStore)));
            
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }

    // Fórmula oficial do Minecraft para calcular XP Total
    private int getTotalExperience(Player player) {
        int level = player.getLevel();
        float exp = player.getExp();
        int res = getExpAtLevel(level);
        res += Math.round(getExpToNextLevel(level) * exp);
        return res;
    }

    private int getExpAtLevel(int level) {
        if (level <= 16) return (int) (Math.pow(level, 2) + 6 * level);
        if (level <= 31) return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
    }

    private int getExpToNextLevel(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    private int getExpToLevel(int level) {
        return getExpAtLevel(level);
    }
}
