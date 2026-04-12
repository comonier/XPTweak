package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EconomyHandler {

    private final XPTweak plugin;

    public EconomyHandler(XPTweak plugin) {
        this.plugin = plugin;
    }

    /**
     * NOVO Comando /xpt inspect <player>
     * Mostra os níveis e pontos exatos de XP de um jogador alvo.
     */
    public void handleInspect(Player admin, String[] args) {
        if (!admin.hasPermission("xptweak.admin.inspect")) {
            admin.sendMessage(plugin.getMessage("no-permission"));
            return;
        }

        if (args.length < 2) {
            admin.sendMessage("§cUsage: /xpt inspect <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            admin.sendMessage(plugin.getMessage("player-not-found"));
            return;
        }

        int levels = target.getLevel();
        int points = plugin.getXpManager().getTotalExperience(target);

        admin.sendMessage(plugin.getMessage("inspect-info")
                .replace("{player}", target.getName())
                .replace("{level}", String.valueOf(levels))
                .replace("{points}", String.valueOf(points)));
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
            plugin.getTransactionManager().createRequest(player, target, amount);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }

    public void handleAccept(Player player) {
        if (!player.hasPermission("xptweak.user.give")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        plugin.getTransactionManager().acceptRequest(player);
    }

    public void handleAuction(Player player, String[] args) {
        if (!player.hasPermission("xptweak.user.auction")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }

        if (args.length >= 2 && args[1].equalsIgnoreCase("list")) {
            plugin.getAuctionManager().toggleMessages(player);
            return;
        }

        if (args.length < 3) {
            player.sendMessage(plugin.getMessage("syntax-auc"));
            return;
        }
        try {
            int lvls = Integer.parseInt(args[1]);
            double price = Double.parseDouble(args[2]);
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
        plugin.getAuctionManager().placeBid(player, type);
    }
}
