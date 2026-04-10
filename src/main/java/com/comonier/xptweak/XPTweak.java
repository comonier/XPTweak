package com.comonier.xptweak;

import com.comonier.xptweak.commands.*;
import com.comonier.xptweak.events.XPEventListener;
import com.comonier.xptweak.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class XPTweak extends JavaPlugin {

    private FileConfiguration messages;
    private XPManager xpManager;
    private TransactionManager transactionManager;
    private AuctionManager auctionManager;
    private XPRainManager xpRainManager;
    private WorldGuardHook wgHook;
    private DatabaseManager databaseManager;
    private DiscordWebhook discordWebhook;
    private static Economy econ = null;

    @Override
    public void onLoad() {
        this.wgHook = new WorldGuardHook();
        this.wgHook.registerFlag();
    }

    @Override
    public void onEnable() {
        // Inicializa Economia (Vault)
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        reloadPluginConfig();

        // Inicializa Managers
        this.databaseManager = new DatabaseManager(this);
        this.discordWebhook = new DiscordWebhook(this);
        this.xpManager = new XPManager(this);
        this.transactionManager = new TransactionManager(this);
        this.auctionManager = new AuctionManager(this);
        this.xpRainManager = new XPRainManager(this);

        // Registro de Comandos
        XPTCommand xptCmd = new XPTCommand(this);
        getCommand("xpt").setExecutor(xptCmd);
        getCommand("xpt").setTabCompleter(new TabCompleter());
        getCommand("xptc").setExecutor(new XPTCCommand(this));
        
        // Registro de Eventos
        getServer().getPluginManager().registerEvents(new XPEventListener(this), this);

        getLogger().info("XPTweak 1.0 enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public void reloadPluginConfig() {
        try {
            saveDefaultConfig();
            getConfig().load(new File(getDataFolder(), "config.yml"));
            
            saveResource("messages_en.yml", false);
            saveResource("messages_pt.yml", false);
            saveResource("messages_es.yml", false);
            saveResource("messages_ru.yml", false);
            
            loadLanguage();
            
            if (xpRainManager != null) {
                this.xpRainManager = new XPRainManager(this);
            }
        } catch (IOException | InvalidConfigurationException e) {
            getLogger().severe("!!! CONFIGURATION ERROR !!!");
            getLogger().severe("Your config.yml is corrupted. Error: " + e.getMessage());
        }
    }

    public void loadLanguage() {
        String lang = getConfig().getString("language", "en");
        File langFile = new File(getDataFolder(), "messages_" + lang + ".yml");
        if (!langFile.exists()) langFile = new File(getDataFolder(), "messages_en.yml");
        messages = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path) {
        String msg = messages.getString(path, "Message missing: " + path);
        String prefix = messages.getString("prefix", "&8[&aXPTweak&8] ");
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    // Getters necessários para os Handlers e Managers
    public static Economy getEconomy() { return econ; }
    public XPManager getXpManager() { return xpManager; }
    public TransactionManager getTransactionManager() { return transactionManager; }
    public AuctionManager getAuctionManager() { return auctionManager; }
    public XPRainManager getXpRainManager() { return xpRainManager; }
    public WorldGuardHook getWgHook() { return wgHook; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public DiscordWebhook getDiscordWebhook() { return discordWebhook; }
}
