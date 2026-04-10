package com.comonier.xptweak.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

public class WorldGuardHook {

    public static StateFlag XPT_RAIN_FLAG;

    /**
     * Registers the custom 'xpt-rain' flag.
     * This method MUST be called during the plugin's onLoad() phase.
     */
    public void registerFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("xpt-rain", false);
            registry.register(flag);
            XPT_RAIN_FLAG = flag;
        } catch (FlagConflictException e) {
            // In case of a reload, retrieve the existing flag
            XPT_RAIN_FLAG = (StateFlag) registry.get("xpt-rain");
        }
    }

    /**
     * Checks if a specific location has the 'xpt-rain' flag set to ALLOW.
     * @param loc The location to check.
     * @return true if the region has the flag enabled.
     */
    public boolean isRainRegion(Location loc) {
        if (XPT_RAIN_FLAG == null) return false;
        
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        // Returns true if flag is ALLOW
        return query.testState(BukkitAdapter.adapt(loc), null, XPT_RAIN_FLAG);
    }
}
