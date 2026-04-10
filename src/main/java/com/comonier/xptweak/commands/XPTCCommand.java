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
        if (!(sender instanceof Player player)) return true;

        if (!player.hasPermission("xptweak.user.custom")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.getMessage("invalid-syntax").replace("{command}", "/xptc <max|lvl>"));
            return true;
        }

        int levelsToStore = 0;
        if (args[0].equalsIgnoreCase("max") || args[0].equalsIgnoreCase("all")) {
            levelsToStore = player.getLevel();
        } else if (args[0].equalsIgnoreCase("lvl") && args.length > 1) {
            try {
                levelsToStore = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.getMessage("invalid-number"));
                return true;
            }
        }

        if (levelsToStore <= 0 || player.getLevel() < levelsToStore) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return true;
        }

        int currentTotalXp = plugin.getXpManager().getTotalExperience(player);
        int xpAfterSubtraction = plugin.getXpManager().getExpAtLevel(player.getLevel() - levelsToStore);
        int finalPoints = currentTotalXp - xpAfterSubtraction;

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessage("inventory-full"));
            return true;
        }

        ItemStack customBottle = plugin.getXpManager().createCustomBottle(player.getName(), levelsToStore, finalPoints);
        player.setLevel(player.getLevel() - levelsToStore);
        player.getInventory().addItem(customBottle);

        // Discord Webhook
        String webhookUrl = plugin.getConfig().getString("webhooks.donations");
        plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-bottle-storage")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(levelsToStore))
                .replace("{points}", String.valueOf(finalPoints)));
        
        // Chat com Pontos
        player.sendMessage(plugin.getMessage("xp-custom-bottle")
                .replace("{amount}", String.valueOf(levelsToStore))
                .replace("{points}", String.valueOf(finalPoints)));

        return true;
    }
}
