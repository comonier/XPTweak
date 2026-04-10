package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
        this.xpKey = new NamespacedKey(plugin, "stored_xp");
    }

    // Creates a custom bottle with NBT data
    public ItemStack createCustomBottle(String playerName, int levels) {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = bottle.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName("§bXP Bottle: §f" + playerName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Stored Levels: §e" + levels);
            lore.add("");
            lore.add("§8Right-click to claim XP");
            meta.setLore(lore);
            
            // Store the exact XP in NBT for accuracy
            meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, levels);
            bottle.setItemMeta(meta);
        }
        return bottle;
    }

    public NamespacedKey getXpKey() {
        return xpKey;
    }
}
