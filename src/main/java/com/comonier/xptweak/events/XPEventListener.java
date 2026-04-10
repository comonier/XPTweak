package com.comonier.xptweak.events;

import com.comonier.xptweak.XPTweak;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class XPEventListener implements Listener {

    private final XPTweak plugin;

    public XPEventListener(XPTweak plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBottleUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        // Lê os pontos totais salvos no NBT
        Integer storedPoints = item.getItemMeta().getPersistentDataContainer()
                .get(plugin.getXpManager().getXpKey(), PersistentDataType.INTEGER);

        if (storedPoints != null) {
            event.setCancelled(true);
            
            // Adiciona os pontos exatos e o Bukkit recalcula o nível automaticamente
            event.getPlayer().giveExp(storedPoints);
            
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                event.getPlayer().getInventory().setItemInMainHand(null);
            }
        }
    }
}
