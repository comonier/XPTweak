package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransactionManager {

    private final XPTweak plugin;
    private final Map<UUID, PendingDonate> pendingDonations = new HashMap<>();

    public TransactionManager(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void createRequest(Player sender, Player target, int levelsToGive) {
        int currentXp = plugin.getXpManager().getTotalExperience(sender);
        int xpAfterSubtraction = plugin.getXpManager().getExpAtLevel(sender.getLevel() - levelsToGive);
        int pointsToTransfer = currentXp - xpAfterSubtraction;

        if (pointsToTransfer <= 0) {
            sender.sendMessage(plugin.getMessage("not-enough-xp"));
            return;
        }

        pendingDonations.put(target.getUniqueId(), new PendingDonate(sender.getUniqueId(), levelsToGive, pointsToTransfer));
        
        sender.sendMessage(plugin.getMessage("donate-sent").replace("{player}", target.getName()));
        target.sendMessage(plugin.getMessage("donate-received")
                .replace("{player}", sender.getName())
                .replace("{amount}", String.valueOf(levelsToGive)));
    }

    public void acceptRequest(Player target) {
        PendingDonate request = pendingDonations.remove(target.getUniqueId());
        
        if (request == null) {
            target.sendMessage(plugin.getMessage("no-pending-donate"));
            return;
        }

        Player sender = plugin.getServer().getPlayer(request.senderId());
        if (sender == null || !sender.isOnline()) {
            target.sendMessage(plugin.getMessage("player-offline"));
            return;
        }

        if (sender.getLevel() < request.levels()) {
            target.sendMessage(plugin.getMessage("not-enough-xp-sender"));
            return;
        }

        // Transfere pontos exatos para evitar inflação de níveis
        sender.setLevel(sender.getLevel() - request.levels());
        target.giveExp(request.points());

        // Report Discord
        String webhookUrl = plugin.getConfig().getString("webhooks.donations");
        plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-donate-report")
                .replace("{sender}", sender.getName())
                .replace("{amount}", String.valueOf(request.levels()))
                .replace("{target}", target.getName())
                .replace("{points}", String.valueOf(request.points())));

        sender.sendMessage(plugin.getMessage("donate-success-sender").replace("{player}", target.getName()));
        target.sendMessage(plugin.getMessage("donate-success-target").replace("{player}", sender.getName()));
    }

    private record PendingDonate(UUID senderId, int levels, int points) {}
}
