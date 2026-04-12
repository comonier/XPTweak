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

        // Primeiro argumento: Lista todos os subcomandos disponíveis
        if (args.length == 1) {
            List<String> subs = Arrays.asList("max", "all", "inv", "lvl", "give", "accept", "auc", "bid", "time", "inspect", "rain", "reload");
            List<String> available = new ArrayList<>();
            for (String s : subs) {
                // Verificação básica: se tem a permissão base, mostra as opções no tab
                if (player.hasPermission("xptweak.user.base")) available.add(s);
            }
            return StringUtil.copyPartialMatches(args[0], available, new ArrayList<>());
        }

        // Segundo argumento: Sugestões específicas baseadas no subcomando anterior
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            
            // Sugere jogadores online para Give e Inspect
            if (sub.equals("give") || sub.equals("inspect")) return null; 
            
            // Sugere confirmação para comandos de esvaziamento total
            if (sub.equals("max") || sub.equals("all")) {
                return StringUtil.copyPartialMatches(args[1], Collections.singletonList("confirm"), new ArrayList<>());
            }
            
            if (sub.equals("auc")) {
                return StringUtil.copyPartialMatches(args[1], Collections.singletonList("list"), new ArrayList<>());
            }
            
            if (sub.equals("bid")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("x1", "x2"), new ArrayList<>());
            }
            
            if (sub.equals("rain")) return Arrays.asList("500", "1000", "5000");
            
            if (sub.equals("lvl")) return Arrays.asList("1", "5", "10", "30");
        }

        // Terceiro argumento: Confirmações ou durações
        if (args.length == 3) {
            String sub = args[0].toLowerCase();
            
            if (sub.equals("lvl")) {
                return StringUtil.copyPartialMatches(args[2], Collections.singletonList("confirm"), new ArrayList<>());
            }
            
            if (sub.equals("rain")) return Arrays.asList("10", "30", "60");
        }

        return Collections.emptyList();
    }
}
