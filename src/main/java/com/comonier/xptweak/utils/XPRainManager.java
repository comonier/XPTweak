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

import java.time.Duration;
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
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                List<String> scheduledTimes = plugin.getConfig().getStringList("xp-rain.times");

                for (String timeStr : scheduledTimes) {
                    try {
                        LocalTime rainTime = LocalTime.parse(timeStr, timeFormatter);
                        long diffMinutes = Duration.between(now, rainTime).toMinutes();

                        if (diffMinutes < 0) diffMinutes += 1440;

                        handleAnnouncements(diffMinutes);
                        
                        if (diffMinutes == 0) {
                            String webhookUrl = plugin.getConfig().getString("webhooks.xp-rain");
                            plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-rain-started"));
                            executeRain(plugin.getConfig().getInt("xp-rain.total-orbs", 500), 10);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Invalid time format in config: " + timeStr);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
    }

    private void handleAnnouncements(long minutesLeft) {
        String webhookUrl = plugin.getConfig().getString("webhooks.xp-rain");
        String timeDisplay = formatMinutes(minutesLeft);

        // DISCORD ANNOUNCEMENTS (30, 15, 10, 5)
        if (minutesLeft == 30 || minutesLeft == 15 || minutesLeft == 10 || minutesLeft == 5) {
            String discordMsg = plugin.getMessageRaw("webhook-rain-announcement").replace("{time}", timeDisplay);
            plugin.getDiscordWebhook().send(webhookUrl, discordMsg);
        }

        // IN-GAME ANNOUNCEMENTS (30, 15, 10, 5, 4, 3, 2, 1, 30s)
        if (plugin.getConfig().getBoolean("xp-rain.announcement-enabled", false)) {
            if (minutesLeft == 30 || minutesLeft == 15 || minutesLeft == 10 || 
                minutesLeft == 5 || minutesLeft == 4 || minutesLeft == 3 || 
                minutesLeft == 2 || minutesLeft == 1) {
                
                // Correção: plugin.getMessage() já traz o prefixo. Não concatenar outro manualmente.
                Bukkit.broadcastMessage(plugin.getMessage("in-game-rain-announcement").replace("{time}", timeDisplay));
                
                if (minutesLeft == 1) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.broadcastMessage(plugin.getMessage("in-game-rain-announcement").replace("{time}", "30 seconds"));
                        }
                    }.runTaskLater(plugin, 600L);
                }
            }
        }
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

    private String formatMinutes(long minutes) {
        if (minutes == 1) return "1 minute";
        return minutes + " minutes";
    }
}
