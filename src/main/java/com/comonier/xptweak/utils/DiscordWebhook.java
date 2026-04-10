package com.comonier.xptweak.utils;

import com.comonier.xptweak.XPTweak;
import org.bukkit.Bukkit;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {

    private final XPTweak plugin;

    public DiscordWebhook(XPTweak plugin) {
        this.plugin = plugin;
    }

    public void send(String urlString, String content) {
        if (urlString == null || urlString.isEmpty() || urlString.equals("YOUR_WEBHOOK_URL")) return;

        // Executa de forma assíncrona para não lagar o servidor/Folia
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = "{\"content\": \"" + content + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                connection.getResponseCode(); // Trigger request
                connection.disconnect();
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to send Discord Webhook: " + e.getMessage());
            }
        });
    }
}
