package com.comonier.xptweak.commands;

import com.comonier.xptweak.XPTweak;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminHandler {

    private final XPTweak plugin;

    public AdminHandler(XPTweak plugin) {
        this.plugin = plugin;
    }

    /**
     * Comando /xpt reload
     * Recarrega todas as configurações e arquivos de tradução.
     */
    public void handleReload(CommandSender sender) {
        if (!sender.hasPermission("xptweak.admin.reload")) {
            sender.sendMessage(plugin.getMessage("no-permission"));
            return;
        }
        plugin.reloadPluginConfig();
        sender.sendMessage(plugin.getMessage("reload-success"));
    }

    /**
     * Comando /xpt rain <total_orbs> <duration_seconds>
     * Inicia uma chuva de XP manual na região definida.
     * O XP total distribuído é baseado no 'total-xp-points' do config.yml.
     */
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
            int orbs = Integer.parseInt(args[1]);
            int duration = Integer.parseInt(args[2]);
            
            // Executa a lógica de chuva (o XPRainManager cuidará da divisão do XP total)
            plugin.getXpRainManager().executeRain(orbs, duration);

            player.sendMessage(plugin.getMessage("manual-rain-started")
                    .replace("{amount}", String.valueOf(orbs))
                    .replace("{time}", String.valueOf(duration)));

            // Log informativo no console
            plugin.getLogger().info("Manual XP Rain started by " + player.getName() + ": " + orbs + " orbs.");

        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessage("invalid-number"));
        }
    }
}
