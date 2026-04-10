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

    public void createRequest(Player sender, Player target, int amount) {
        pendingDonations.put(target.getUniqueId(), new PendingDonate(sender.getUniqueId(), amount));
        sender.sendMessage(plugin.getMessage("donate-sent").replace("{player}", target.getName()));
        target.sendMessage(plugin.getMessage("donate-received").replace("{player}", sender.getName()).replace("{amount}", String.valueOf(amount)));
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

        if (sender.getLevel() < request.amount()) {
            target.sendMessage(plugin.getMessage("not-enough-xp-sender"));
            return;
        }

        sender.setLevel(sender.getLevel() - request.amount());
        target.setLevel(target.getLevel() + request.amount());

        // Discord Report
        String webhookUrl = plugin.getConfig().getString("webhooks.donations");
        plugin.getDiscordWebhook().send(webhookUrl, "📜 **XP Donation**: " + sender.getName() + " donated " + request.amount() + " levels to " + target.getName());

        sender.sendMessage(plugin.getMessage("donate-success-sender").replace("{player}", target.getName()));
        target.sendMessage(plugin.getMessage("donate-success-target").replace("{player}", sender.getName()));
    }

    private record PendingDonate(UUID senderId, int amount) {}
}
