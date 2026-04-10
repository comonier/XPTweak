package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminHandler {

    private final XPTweak plugin;

    public AdminHandler(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void handleReload(CommandSender sender) {
        if (!sender.hasPermission("xptweak.admin.reload")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        plugin.reloadPluginConfig();
        // Agora usa a chave de mensagem com o prefixo cinza padronizado
        sender.sendMessage(plugin.getMessage("reload-success"));
    }

    public void handleManualRain(Player player, String[] args) {
        if (!player.hasPermission("xptweak.admin.rain")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        if (args.length < 3) {
            player.sendMessage("§cUsage: /xpt rain <total_orbs> <duration_seconds>");
            return;
        }
        try {
            int amount = Integer.parseInt(args[1]);
            int duration = Integer.parseInt(args[2]);
            plugin.getXpRainManager().executeRain(amount, duration);
            player.sendMessage(plugin.getMessage("manual-rain-started")
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{time}", String.valueOf(duration)));
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }
}
