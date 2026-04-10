package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class AuctionManager {

    private final XPTweak plugin;
    private UUID auctioneer;
    private int levelsToSell;
    private int pointsToTransfer;
    private double currentBid;
    private UUID highestBidder;
    private int timeLeft;
    private BukkitTask task;
    private boolean isActive = false;

    public AuctionManager(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void startAuction(Player player, int levels, double startingPrice) {
        if (isActive) {
            player.sendMessage(plugin.getMessage("auction-already-running"));
            return;
        }

        int currentXp = plugin.getXpManager().getTotalExperience(player);
        int xpAfterSubtraction = plugin.getXpManager().getExpAtLevel(player.getLevel() - levels);
        this.pointsToTransfer = currentXp - xpAfterSubtraction;

        if (this.pointsToTransfer <= 0) {
            player.sendMessage(plugin.getMessage("not-enough-xp"));
            return;
        }

        this.auctioneer = player.getUniqueId();
        this.levelsToSell = levels;
        this.currentBid = startingPrice;
        this.highestBidder = null;
        this.timeLeft = plugin.getConfig().getInt("auction-time", 10);
        this.isActive = true;

        player.setLevel(player.getLevel() - levels);

        Bukkit.broadcastMessage(plugin.getMessage("auction-started")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(levels))
                .replace("{price}", String.format("%.2f", startingPrice)));
        runTask();
    }

    private void runTask() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (timeLeft <= 0) { finishAuction(); return; }
            if (timeLeft <= 5) Bukkit.broadcastMessage(plugin.getMessage("auction-ending-soon").replace("{time}", String.valueOf(timeLeft)));
            timeLeft--;
        }, 20L, 20L);
    }

    public void placeBid(Player bidder, String type) {
        if (!isActive) return;
        double nextBid = type.equalsIgnoreCase("x2") ? currentBid * 2 : currentBid * 1.10;
        if (!XPTweak.getEconomy().has(bidder, nextBid)) {
            bidder.sendMessage(plugin.getMessage("not-enough-money"));
            return;
        }
        currentBid = nextBid;
        highestBidder = bidder.getUniqueId();
        if (plugin.getConfig().getBoolean("auction-regressive", true) && timeLeft > 2) timeLeft--;
        Bukkit.broadcastMessage(plugin.getMessage("auction-new-bid").replace("{player}", bidder.getName()).replace("{price}", String.format("%.2f", currentBid)));
    }

    private void finishAuction() {
        task.cancel();
        isActive = false;

        if (highestBidder == null) {
            Player seller = Bukkit.getPlayer(auctioneer);
            if (seller != null) seller.giveExp(pointsToTransfer);
            Bukkit.broadcastMessage(plugin.getMessage("auction-no-winner"));
        } else {
            Player winner = Bukkit.getPlayer(highestBidder);
            Player seller = Bukkit.getPlayer(auctioneer);

            if (winner != null && XPTweak.getEconomy().has(winner, currentBid)) {
                XPTweak.getEconomy().withdrawPlayer(winner, currentBid);
                if (seller != null) XPTweak.getEconomy().depositPlayer(seller, currentBid);
                
                winner.giveExp(pointsToTransfer);

                // Discord Report
                String webhookUrl = plugin.getConfig().getString("webhooks.auctions");
                plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-auction-report")
                        .replace("{player}", seller != null ? seller.getName() : "Unknown")
                        .replace("{amount}", String.valueOf(levelsToSell))
                        .replace("{price}", String.format("%.2f", currentBid))
                        .replace("{winner}", winner.getName())
                        .replace("{points}", String.valueOf(pointsToTransfer)));

                // Mensagens Chat
                winner.sendMessage(plugin.getMessage("auction-win")
                        .replace("{amount}", String.valueOf(levelsToSell))
                        .replace("{points}", String.valueOf(pointsToTransfer)));
                
                if (seller != null) {
                    seller.sendMessage(plugin.getMessage("auction-sold")
                        .replace("{price}", String.format("%.2f", currentBid))
                        .replace("{points}", String.valueOf(pointsToTransfer)));
                }

                Bukkit.broadcastMessage(plugin.getMessage("auction-finished")
                        .replace("{player}", winner.getName())
                        .replace("{price}", String.format("%.2f", currentBid))
                        .replace("{points}", String.valueOf(pointsToTransfer)));
            }
        }
    }

    public boolean isActive() { return isActive; }
}
