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
    private String lastAnnouncedTime = "";

    public XPRainManager(XPTweak plugin) {
        this.plugin = plugin;
        startScheduler();
    }

    private void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now();
                int currentTotalMinute = now.getHour() * 60 + now.getMinute();
                List<String> scheduledTimes = plugin.getConfig().getStringList("xp-rain.times");

                for (String timeStr : scheduledTimes) {
                    try {
                        LocalTime rainTime = LocalTime.parse(timeStr, timeFormatter);
                        long diffMinutes = Duration.between(now.withSecond(0).withNano(0), rainTime).toMinutes();

                        if (diffMinutes < 0) diffMinutes += 1440;

                        if (diffMinutes >= 0 && diffMinutes <= 30) {
                            String announcementKey = timeStr + ":" + currentTotalMinute + ":" + diffMinutes;
                            
                            if (!lastAnnouncedTime.equals(announcementKey)) {
                                lastAnnouncedTime = announcementKey;
                                handleAnnouncements(diffMinutes);
                                
                                if (diffMinutes == 0) {
                                    String webhookUrl = plugin.getConfig().getString("webhooks.xp-rain");
                                    plugin.getDiscordWebhook().send(webhookUrl, plugin.getMessageRaw("webhook-rain-started"));
                                    
                                    // Executa a chuva usando os valores do config
                                    int configOrbs = plugin.getConfig().getInt("xp-rain.total-orbs", 300);
                                    executeRain(configOrbs, 10);
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }
        }.runTaskTimer(plugin, 20L, 200L);
    }

    private void handleAnnouncements(long minutesLeft) {
        String webhookUrl = plugin.getConfig().getString("webhooks.xp-rain");
        String instruction = plugin.getConfig().getString("xp-rain.location-instruction", "");
        
        String unit = plugin.getMessageRaw(minutesLeft >= 60 ? "time-unit-hours" : "time-unit-minutes");
        long displayValue = minutesLeft >= 60 ? minutesLeft / 60 : minutesLeft;
        
        String announcementMsg = plugin.getMessage("xp-rain-countdown")
                .replace("{time}", String.valueOf(displayValue))
                .replace("{unit}", unit)
                .replace("{instruction}", instruction);

        if (minutesLeft == 30 || minutesLeft == 15 || minutesLeft == 10 || minutesLeft == 5) {
            plugin.getDiscordWebhook().send(webhookUrl, announcementMsg);
        }

        if (plugin.getConfig().getBoolean("xp-rain.announcement-enabled", false)) {
            if (minutesLeft == 30 || minutesLeft == 15 || minutesLeft == 10 || 
                minutesLeft == 5 || minutesLeft == 4 || minutesLeft == 3 || 
                minutesLeft == 2 || minutesLeft == 1) {
                
                Bukkit.broadcastMessage(announcementMsg);
                
                if (minutesLeft == 1) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            String secMsg = plugin.getMessage("xp-rain-countdown")
                                    .replace("{time}", "30")
                                    .replace("{unit}", plugin.getMessageRaw("time-unit-seconds"))
                                    .replace("{instruction}", instruction);
                            Bukkit.broadcastMessage(secMsg);
                        }
                    }.runTaskLater(plugin, 600L);
                }
            }
        }
    }

    public String getTimeUntilNext() {
        LocalTime now = LocalTime.now();
        List<String> times = plugin.getConfig().getStringList("xp-rain.times");
        long shortestSeconds = -1;

        for (String t : times) {
            try {
                LocalTime rt = LocalTime.parse(t, timeFormatter);
                long diff = Duration.between(now, rt).getSeconds();
                if (diff <= 0) diff += 86400; 
                if (shortestSeconds == -1 || diff < shortestSeconds) {
                    shortestSeconds = diff;
                }
            } catch (Exception ignored) {}
        }

        if (shortestSeconds == -1) return "00h:00m:00s";
        
        long h = shortestSeconds / 3600;
        long m = (shortestSeconds % 3600) / 60;
        long s = shortestSeconds % 60;

        return String.format("%02dh:%02dm:%02ds", h, m, s);
    }

    /**
     * Lógica v1.2.3: Divide o total de XP configurado pela quantidade de orbs.
     */
    public void executeRain(int totalOrbs, int durationSeconds) {
        String worldName = plugin.getConfig().getString("xp-rain.world", "world");
        String regionName = plugin.getConfig().getString("xp-rain.region-name", "xp_arena");
        int totalXpPoints = plugin.getConfig().getInt("xp-rain.total-xp-points", 30000);
        
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(world));
        if (regions == null || !regions.hasRegion(regionName)) return;

        ProtectedRegion region = regions.getRegion(regionName);
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        // Calcula XP individual por orb
        int xpPerOrb = Math.max(1, totalXpPoints / totalOrbs);
        
        int totalTicks = durationSeconds * 20;
        int orbsPerTick = Math.max(1, totalOrbs / totalTicks);

        new BukkitRunnable() {
            int ticksElapsed = 0;
            int spawnedOrbs = 0;
            @Override
            public void run() {
                if (ticksElapsed >= totalTicks || spawnedOrbs >= totalOrbs) {
                    this.cancel();
                    return;
                }
                for (int i = 0; i < orbsPerTick; i++) {
                    if (spawnedOrbs >= totalOrbs) break;
                    int x = random.nextInt((max.getX() - min.getX()) + 1) + min.getX();
                    int z = random.nextInt((max.getZ() - min.getZ()) + 1) + min.getZ();
                    int y = world.getHighestBlockYAt(x, z) + 15;
                    
                    world.spawn(new Location(world, x, y, z), ExperienceOrb.class).setExperience(xpPerOrb);
                    spawnedOrbs++;
                }
                ticksElapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
