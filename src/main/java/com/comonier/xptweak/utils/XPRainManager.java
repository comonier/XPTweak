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
    private final Random random = new Random();

    public XPRainManager(XPTweak plugin) {
        this.plugin = plugin;
        startScheduler();
    }

    private void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                if (plugin.getConfig().getStringList("xp-rain.times").contains(currentTime)) {
                    // Discord Announcement (Always sent if URL is present)
                    String webhookUrl = plugin.getConfig().getString("webhooks.xp-rain");
                    plugin.getDiscordWebhook().send(webhookUrl, "🌧️ **XP Rain Incoming!** The event is starting in the designated arena.");
                    
                    executeRain(plugin.getConfig().getInt("xp-rain.total-orbs", 500), 10);
                }
            }
        }.runTaskTimer(plugin, 0L, 1200L);
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

        new BukkitRunnable() {
            int spawned = 0;
            @Override
            public void run() {
                if (spawned >= totalAmount) { this.cancel(); return; }
                for (int i = 0; i < 5; i++) {
                    int x = random.nextInt((max.getX() - min.getX()) + 1) + min.getX();
                    int z = random.nextInt((max.getZ() - min.getZ()) + 1) + min.getZ();
                    world.spawn(new Location(world, x, world.getHighestBlockYAt(x, z) + 15, z), ExperienceOrb.class).setExperience(10);
                    spawned++;
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
