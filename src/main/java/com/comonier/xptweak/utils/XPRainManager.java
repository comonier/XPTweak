package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class XPRainManager {

    private final XPTweak plugin;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Random random = new Random();

    public XPRainManager(XPTweak plugin) {
        this.plugin = plugin;
        startScheduler();
    }

    private void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                String currentTime = LocalTime.now().format(timeFormatter);
                List<String> scheduledTimes = plugin.getConfig().getStringList("xp-rain.times");

                if (scheduledTimes.contains(currentTime)) {
                    startCountdown();
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
    }

    private void startCountdown() {
        int[] intervals = {1800, 900, 600, 300, 240, 180, 120, 60, 30, 10, 5, 4, 3, 2, 1};
        String webhookUrl = plugin.getConfig().getString("webhooks.xp-rain");

        for (int seconds : intervals) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    String timeStr = formatTime(seconds);
                    
                    // Alerta no In-Game (se ativado)
                    if (plugin.getConfig().getBoolean("xp-rain.announcement-enabled", false)) {
                        String rawMsg = plugin.getConfig().getString("xp-rain.announcement-message", "XP RAIN IN {time}");
                        Bukkit.broadcastMessage(plugin.getMessage("prefix") + rawMsg.replace("{time}", timeStr));
                    }

                    // Alerta no Discord (Sempre tenta enviar se houver Webhook)
                    String discordMsg = plugin.getMessageRaw("webhook-rain-announcement")
                            .replace("{time}", timeStr);
                    plugin.getDiscordWebhook().send(webhookUrl, discordMsg);
                }
            }.runTaskLater(plugin, (1800 - seconds) * 20L);
        }

        // Início da Chuva
        new BukkitRunnable() {
            @Override
            public void run() {
                // Mensagem de início no Discord
                plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-rain-started"));
                
                executeRain(plugin.getConfig().getInt("xp-rain.total-orbs", 500), 10);
            }
        }.runTaskLater(plugin, 1800 * 20L);
    }

    public void executeRain(int totalAmount, int durationSeconds) {
        String worldName = plugin.getConfig().getString("xp-rain.world", "world");
        String regionName = plugin.getConfig().getString("xp-rain.region-name", "xp_arena");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(world));
        
        if (regions == null || !regions.hasRegion(regionName)) return;

        ProtectedRegion region = regions.getRegion(regionName);
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        int totalTicks = durationSeconds * 20;
        int orbsPerTick = Math.max(1, totalAmount / totalTicks);

        new BukkitRunnable() {
            int ticksElapsed = 0;
            int spawnedOrbs = 0;

            @Override
            public void run() {
                if (ticksElapsed >= totalTicks || spawnedOrbs >= totalAmount) {
                    this.cancel();
                    return;
                }

                for (int i = 0; i < orbsPerTick; i++) {
                    if (spawnedOrbs >= totalAmount) break;
                    int x = random.nextInt((max.getX() - min.getX()) + 1) + min.getX();
                    int z = random.nextInt((max.getZ() - min.getZ()) + 1) + min.getZ();
                    int y = world.getHighestBlockYAt(x, z) + 15;
                    world.spawn(new Location(world, x, y, z), ExperienceOrb.class).setExperience(10);
                    spawnedOrbs++;
                }
                ticksElapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private String formatTime(int seconds) {
        if (seconds >= 60) return (seconds / 60) + " minutes";
        return seconds + " seconds";
    }
}
