package com.comonier.xptweak.events;

import com.comonier.xptweak.XPTweak;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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

        // Tenta ler o XP armazenado no NBT da garrafa customizada
        Integer storedXp = item.getItemMeta().getPersistentDataContainer()
                .get(plugin.getXpManager().getXpKey(), PersistentDataType.INTEGER);

        if (storedXp != null) {
            event.setCancelled(true); // Cancela o arremesso padrão da garrafa
            event.getPlayer().setLevel(event.getPlayer().getLevel() + storedXp);
            
            // Remove uma unidade do inventário
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                event.getPlayer().getInventory().setItemInMainHand(null);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Verifica se a função de drop está ativa na config
        if (!plugin.getConfig().getBoolean("drop-xp-bottle-on-death", true)) return;
        
        int levels = event.getEntity().getLevel();
        if (levels > 0) {
            // Cria a garrafa customizada com o nome do jogador morto
            ItemStack deathBottle = plugin.getXpManager().createCustomBottle(event.getEntity().getName(), levels);
            event.getDrops().add(deathBottle);
            
            // Remove o drop natural de XP do Minecraft para não duplicar
            event.setDroppedExp(0);
            event.getEntity().setLevel(0);
            event.getEntity().setExp(0);
        }
    }
}
