package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final XPTweak plugin;
    private Connection connection;

    public DatabaseManager(XPTweak plugin) {
        this.plugin = plugin;
        setupDatabase();
    }

    private void setupDatabase() {
        try {
            String type = plugin.getConfig().getString("database.type", "SQLITE").toUpperCase();
            
            if (type.equals("MYSQL")) {
                String host = plugin.getConfig().getString("database.host");
                int port = plugin.getConfig().getInt("database.port");
                String name = plugin.getConfig().getString("database.name");
                String user = plugin.getConfig().getString("database.user");
                String pass = plugin.getConfig().getString("database.password");
                
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name, user, pass);
            } else {
                File dbFile = new File(plugin.getDataFolder(), "database.db");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            }

            // Create basic tables (Example for future use like auction logs or player stats)
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS xpt_logs (id INTEGER PRIMARY KEY, player TEXT, action TEXT, amount INTEGER, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to Database! Check your config.");
            plugin.getLogger().severe("Error: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                setupDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
