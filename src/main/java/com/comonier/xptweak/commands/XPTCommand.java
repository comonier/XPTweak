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
        // Comando de Reload disponível para Console e Players com permissão
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            admin.handleReload(sender);
            return true;
        }

        // Restante dos comandos apenas para jogadores
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute XP management commands.");
            return true;
        }

        // Menu de ajuda se não houver argumentos ou digitar /xpt help
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
        for (String line : plugin.getConfig().getStringList("help-menu")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}
