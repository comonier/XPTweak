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
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            return;
        }
        player.setLevel(0);
        player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, levels));
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
            int amount = Integer.parseInt(args[1]);
            if (player.getLevel() < amount) {
                player.sendMessage(plugin.getMessage("not-enough-xp"));
                return;
            }
            player.setLevel(player.getLevel() - amount);
            player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, amount));
            player.sendMessage(plugin.getMessage("xp-stored").replace("{amount}", String.valueOf(amount)));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }
}
