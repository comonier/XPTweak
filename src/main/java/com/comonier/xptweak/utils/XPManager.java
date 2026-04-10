package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.List;

public class XPManager {

    private final XPTweak plugin;
    private final NamespacedKey xpKey;

    public XPManager(XPTweak plugin) {
        this.plugin = plugin;
        this.xpKey = new NamespacedKey(plugin, "stored_xp_points");
    }

    public ItemStack createCustomBottle(String playerName, int levels, int totalPoints) {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = bottle.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§bXP Bottle: §f" + playerName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Stored Levels: §e" + levels);
            lore.add("§8Value: §7" + totalPoints + " points");
            lore.add("");
            lore.add("§eRight-click to claim exact XP");
            meta.setLore(lore);
            
            meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, totalPoints);
            bottle.setItemMeta(meta);
        }
        return bottle;
    }

    public NamespacedKey getXpKey() {
        return xpKey;
    }

    public int getTotalExperience(Player player) {
        int level = player.getLevel();
        float exp = player.getExp();
        int res = getExpAtLevel(level);
        res += Math.round(getExpToNextLevel(level) * exp);
        return res;
    }

    public int getExpAtLevel(int level) {
        if (level <= 16) return (int) (Math.pow(level, 2) + 6 * level);
        if (level <= 31) return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360);
        return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220);
    }

    public int getExpToNextLevel(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }
}
