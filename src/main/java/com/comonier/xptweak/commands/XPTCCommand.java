package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class XPTCCommand implements CommandExecutor {

    private final XPTweak plugin;

    public XPTCCommand(XPTweak plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can create custom XP bottles.");
            return true;
        }

        // Check permission for custom bottles
        if (!player.hasPermission("xptweak.user.custom")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("invalid-syntax").replace("{command}", "/xptc <max|lvl>"));
            return true;
        }

        int levelsToStore = 0;
        String sub = args[0].toLowerCase();

        if (sub.equals("max") || sub.equals("all")) {
            levelsToStore = player.getLevel();
        } else if (sub.equals("lvl")) {
            if (args.length < 2) {
                player.sendMessage(plugin.getMessage("syntax-lvl").replace("/xpt", "/xptc"));
                return true;
            }
            try {
                levelsToStore = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getMessage("invalid-number"));
                return true;
            }
        } else {
            player.sendMessage(plugin.getMessage("invalid-syntax").replace("{command}", "/xptc help"));
            return true;
        }

        // Validation
        if (levelsToStore <= 0) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return true;
        }

        if (player.getLevel() < levelsToStore) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return true;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            return true;
        }

        // Create and give the bottle
        ItemStack customBottle = plugin.getXpManager().createCustomBottle(player.getName(), levelsToStore);
        player.setLevel(player.getLevel() - levelsToStore);
        player.getInventory().addItem(customBottle);
        
        player.sendMessage(plugin.getMessage("xp-custom-bottle")
                .replace("{amount}", String.valueOf(levelsToStore)));

        return true;
    }
}
