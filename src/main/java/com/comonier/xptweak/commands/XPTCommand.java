package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPTCommand implements CommandExecutor {

    private final XPTweak plugin;
    private final StorageHandler storage;
    private final EconomyHandler economy;
    private final AdminHandler admin;

    public XPTCommand(XPTweak plugin) {
        this.plugin = plugin;
        this.storage = new StorageHandler(plugin);
        this.economy = new EconomyHandler(plugin);
        this.admin = new AdminHandler(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            admin.handleReload(sender);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use XP management commands.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "max", "all" -> storage.handleMax(player);
            case "lvl" -> storage.handleLvl(player, args);
            case "give" -> economy.handleGive(player, args);
            case "accept" -> plugin.getTransactionManager().acceptRequest(player);
            case "auc" -> economy.handleAuction(player, args);
            case "bid" -> economy.handleBid(player, args);
            case "rain" -> admin.handleManualRain(player, args);
            default -> player.sendMessage(plugin.getMessage("invalid-syntax").replace("{command}", "/xpt help"));
        }
        return true;
    }

    private void sendHelp(Player player) {
        for (String line : plugin.getConfig().getStringList("help-menu")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
