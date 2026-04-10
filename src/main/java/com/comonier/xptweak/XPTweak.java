package com.comonier.xptweak;

import com.comonier.xptweak.commands.*;
import com.comonier.xptweak.events.XPEventListener;
import com.comonier.xptweak.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
        if (!setupEconomy()) {
            getLogger().severe("Vault dependency not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        saveDefaultMessages();
        
        this.databaseManager = new DatabaseManager(this);
        this.discordWebhook = new DiscordWebhook(this);
        this.xpManager = new XPManager(this);
        this.transactionManager = new TransactionManager(this);
        this.auctionManager = new AuctionManager(this);
        
        reloadPluginConfig();

        XPTCommand xptCmd = new XPTCommand(this);
        getCommand("xpt").setExecutor(xptCmd);
        getCommand("xpt").setTabCompleter(new TabCompleter());
        getCommand("xptc").setExecutor(new XPTCCommand(this));
        
        getServer().getPluginManager().registerEvents(new XPEventListener(this), this);

        getLogger().info("XPTweak has been enabled! Precision XP engine active.");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        if (databaseManager != null) databaseManager.closeConnection();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    private void saveDefaultMessages() {
        String[] langs = {"en", "pt", "es", "ru"};
        for (String lang : langs) {
            File file = new File(getDataFolder(), "messages_" + lang + ".yml");
            if (!file.exists()) saveResource("messages_" + lang + ".yml", false);
        }
    }

    public void reloadPluginConfig() {
        try {
            reloadConfig();
            loadLanguage();
            
            if (xpRainManager != null) {
                getServer().getScheduler().cancelTasks(this);
            }
            this.xpRainManager = new XPRainManager(this);
            
        } catch (Exception e) {
            getLogger().severe("Critical error reloading config: " + e.getMessage());
        }
    }

    public void loadLanguage() {
        String lang = getConfig().getString("language", "en");
        File langFile = new File(getDataFolder(), "messages_" + lang + ".yml");
        if (!langFile.exists()) langFile = new File(getDataFolder(), "messages_en.yml");
        messages = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getMessage(String path) {
        String msg = messages.getString(path, "Missing message: " + path);
        if (path.equals("prefix")) return ChatColor.translateAlternateColorCodes('&', msg);
        String prefix = messages.getString("prefix", "&8[&aXPTweak&8] ");
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public String getMessageRaw(String path) {
        if (messages == null) return "Messages not loaded";
        return messages.getString(path, "Missing message: " + path);
    }

    // Getter necessário para o XPTCommand acessar a lista de ajuda
    public FileConfiguration getMessagesConfig() {
        return messages;
    }

    public static Economy getEconomy() { return econ; }
    public XPManager getXpManager() { return xpManager; }
    public TransactionManager getTransactionManager() { return transactionManager; }
    public AuctionManager getAuctionManager() { return auctionManager; }
    public XPRainManager getXpRainManager() { return xpRainManager; }
    public WorldGuardHook getWgHook() { return wgHook; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public DiscordWebhook getDiscordWebhook() { return discordWebhook; }
}
