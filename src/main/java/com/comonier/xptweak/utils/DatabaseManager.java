package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
                
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=false", user, pass);
            } else {
                File dbFile = new File(plugin.getDataFolder(), "database.db");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            }

            try (Statement stmt = connection.createStatement()) {
                // Tabela de logs
                stmt.execute("CREATE TABLE IF NOT EXISTS xpt_logs (id INTEGER PRIMARY KEY, player TEXT, action TEXT, amount INTEGER, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
                // Tabela de preferências (Silenciar leilão)
                stmt.execute("CREATE TABLE IF NOT EXISTS xpt_preferences (uuid VARCHAR(36) PRIMARY KEY, auction_silent BOOLEAN)");
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public void savePreference(UUID uuid, boolean silent) {
        String query = "REPLACE INTO xpt_preferences (uuid, auction_silent) VALUES (?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, uuid.toString());
            pstmt.setBoolean(2, silent);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<UUID> loadSilencedPlayers() {
        Set<UUID> silenced = new HashSet<>();
        String query = "SELECT uuid FROM xpt_preferences WHERE auction_silent = 1";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                silenced.add(UUID.fromString(rs.getString("uuid")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return silenced;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) setupDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
