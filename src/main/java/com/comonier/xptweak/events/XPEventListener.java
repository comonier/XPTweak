package com.comonier.xptweak.events;

import com.comonier.xptweak.XPTweak;
import org.bukkit.entity.Player;
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

        // Lê os pontos totais salvos no NBT da garrafa customizada
        Integer storedPoints = item.getItemMeta().getPersistentDataContainer()
                .get(plugin.getXpManager().getXpKey(), PersistentDataType.INTEGER);

        if (storedPoints != null) {
            // Cancela o arremesso para o jogador consumir o XP instantaneamente
            event.setCancelled(true);
            
            // Adiciona os pontos exatos ao jogador
            event.getPlayer().giveExp(storedPoints);
            
            // Remove uma unidade do item no inventário
            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                event.getPlayer().getInventory().setItemInMainHand(null);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Verifica se a função de dropar garrafa ao morrer está ativa
        if (!plugin.getConfig().getBoolean("drop-xp-bottle-on-death", true)) return;
        
        Player player = event.getEntity();
        
        // Calcula o XP TOTAL em pontos que o jogador tinha ao morrer
        int totalPoints = plugin.getXpManager().getTotalExperience(player);
        int currentLevel = player.getLevel();

        if (totalPoints > 0) {
            // Cria uma garrafa customizada com os pontos exatos
            ItemStack deathBottle = plugin.getXpManager().createCustomBottle(player.getName(), currentLevel, totalPoints);
            
            // Adiciona a garrafa aos drops da morte
            event.getDrops().add(deathBottle);
            
            // Zera o drop de orbs natural do Minecraft para evitar duplicação (Exploit Fix)
            event.setDroppedExp(0);
            
            // Envia log para o Discord se configurado
            String webhookUrl = plugin.getConfig().getString("webhooks.donations");
            plugin.getDiscordWebhook().send(webhookUrl, "💀 **Death Recovery**: " + player.getName() + 
                " dropped a bottle with " + currentLevel + " levels (" + totalPoints + " points).");
        }
    }
}
