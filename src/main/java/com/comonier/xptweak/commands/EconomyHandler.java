package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EconomyHandler {

    private final XPTweak plugin;

    public EconomyHandler(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void handleGive(Player player, String[] args) {
        if (!player.hasPermission("xptweak.user.give")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("syntax-give"));
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(plugin.getMessage("player-not-found"));
            return;
        }
        try {
            int amount = Integer.parseInt(args[2]);
            // Chama o Manager que já revisamos com a lógica de pontos
            plugin.getTransactionManager().createRequest(player, target, amount);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }

    public void handleAuction(Player player, String[] args) {
        if (!player.hasPermission("xptweak.user.auction")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("syntax-auc"));
            return;
        }
        try {
            int lvls = Integer.parseInt(args[1]);
            double price = Double.parseDouble(args[2]);
            // Chama o Manager que já revisamos com a lógica de pontos e Vault
            plugin.getAuctionManager().startAuction(player, lvls, price);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }

    public void handleBid(Player player, String[] args) {
        if (!player.hasPermission("xptweak.user.auction")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        String type = (args.length > 1) ? args[1] : "x1";
        // Chama o Manager que já revisamos com a lógica de Vault
        plugin.getAuctionManager().placeBid(player, type);
    }
}
