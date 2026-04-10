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
        // Handle Reload (Available for Console and Players with permission)
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            admin.handleReload(sender);
            return true;
        }

        // From here on, only Players can execute the subcommands
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use XP management commands.");
            return true;
        }

        // Show help menu if no arguments are provided
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "max":
            case "all":
                storage.handleMax(player);
                break;
                
            case "lvl":
                storage.handleLvl(player, args);
                break;
                
            case "give":
                economy.handleGive(player, args);
                break;
                
            case "accept":
                if (!player.hasPermission("xptweak.user.give")) {
                    player.sendMessage(plugin.getMessage("no-permission"));
                    return true;
                }
                plugin.getTransactionManager().acceptRequest(player);
                break;
                
            case "auc":
                economy.handleAuction(player, args);
                break;
                
            case "bid":
                economy.handleBid(player, args);
                break;
                
            case "rain":
                admin.handleManualRain(player, args);
                break;
                
            default:
                player.sendMessage(plugin.getMessage("invalid-syntax").replace("{command}", "/xpt help"));
                break;
        }
        
        return true;
    }

    private void sendHelp(Player player) {
        // Loads the customizable help menu from config.yml
        for (String line : plugin.getConfig().getStringList("help-menu")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
