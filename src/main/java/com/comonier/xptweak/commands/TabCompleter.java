package com.comonier.xptweak.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();

        if (args.length == 1) {
            List<String> subs = Arrays.asList("max", "all", "lvl", "give", "accept", "auc", "bid", "rain", "reload");
            List<String> available = new ArrayList<>();
            for (String s : subs) {
                if (player.hasPermission("xptweak.user.base")) available.add(s);
            }
            return StringUtil.copyPartialMatches(args[0], available, new ArrayList<>());
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("give")) return null; // Sugere players
            if (sub.equals("bid")) return StringUtil.copyPartialMatches(args[1], Arrays.asList("x1", "x2"), new ArrayList<>());
            if (sub.equals("rain")) return Arrays.asList("1000", "5000");
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("rain")) return Arrays.asList("10", "30", "60");
        }

        return Collections.emptyList();
    }
}
