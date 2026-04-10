package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class AuctionManager {

    private final XPTweak plugin;
    private UUID auctioneer;
    private int amount;
    private double currentBid;
    private UUID highestBidder;
    private int timeLeft;
    private BukkitTask task;
    private boolean isActive = false;

    public AuctionManager(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void startAuction(Player player, int amount, double startingPrice) {
        if (isActive) {
            player.sendMessage(plugin.getMessage("auction-already-running"));
            return;
        }
        this.auctioneer = player.getUniqueId();
        this.amount = amount;
        this.currentBid = startingPrice;
        this.highestBidder = null;
        this.timeLeft = plugin.getConfig().getInt("auction-time", 10);
        this.isActive = true;

        Bukkit.broadcastMessage(plugin.getMessage("auction-started")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(amount))
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
        if (!isActive) { bidder.sendMessage(plugin.getMessage("no-active-auction")); return; }
        if (bidder.getUniqueId().equals(auctioneer)) { bidder.sendMessage(plugin.getMessage("auction-bid-self")); return; }

        double nextBid = type.equalsIgnoreCase("x2") ? currentBid * 2 : currentBid * 1.10;
        
        // Verifica se o licitante tem saldo no Vault
        if (!XPTweak.getEconomy().has(bidder, nextBid)) {
            bidder.sendMessage(plugin.getMessage("not-enough-money"));
            return;
        }

        currentBid = nextBid;
        highestBidder = bidder.getUniqueId();
        if (plugin.getConfig().getBoolean("auction-regressive", true) && timeLeft > 2) timeLeft--;

        Bukkit.broadcastMessage(plugin.getMessage("auction-new-bid")
                .replace("{player}", bidder.getName())
                .replace("{price}", String.format("%.2f", currentBid)));
    }

    private void finishAuction() {
        task.cancel();
        isActive = false;

        if (highestBidder == null) {
            Bukkit.broadcastMessage(plugin.getMessage("auction-no-winner"));
            Player seller = Bukkit.getPlayer(auctioneer);
            if (seller != null) seller.setLevel(seller.getLevel() + amount);
        } else {
            Player winner = Bukkit.getPlayer(highestBidder);
            Player seller = Bukkit.getPlayer(auctioneer);

            // Transação final via Vault
            if (winner != null && XPTweak.getEconomy().has(winner, currentBid)) {
                XPTweak.getEconomy().withdrawPlayer(winner, currentBid);
                if (seller != null) XPTweak.getEconomy().depositPlayer(seller, currentBid);
                
                winner.setLevel(winner.getLevel() + amount);
                
                Bukkit.broadcastMessage(plugin.getMessage("auction-finished")
                        .replace("{player}", winner.getName())
                        .replace("{price}", String.format("%.2f", currentBid)));
            } else {
                // Se o vencedor ficou sem dinheiro no último segundo
                Bukkit.broadcastMessage("§c[XPTweak] Auction cancelled: Winner has insufficient funds.");
                if (seller != null) seller.setLevel(seller.getLevel() + amount);
            }
        }
    }

    public boolean isActive() { return isActive; }
}
