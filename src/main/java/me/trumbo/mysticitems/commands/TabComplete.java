package me.trumbo.mysticitems.commands;

import me.trumbo.mysticitems.MysticItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TabComplete implements TabCompleter {

    private MysticItems main;

    public TabComplete(MysticItems main) {

        this.main = main;

    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1) {
            return Arrays.asList("give", "reload");
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
            switch (args.length) {
                case 2:
                    return main.getServer().getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                case 3:
                    String input = args[2].toLowerCase();
                    return Arrays.stream(new String[]{"Bomb", "Locket", "Silence", "Bloodletting"})
                            .filter(tntName -> tntName.toLowerCase().startsWith(input))
                            .collect(Collectors.toList());
                case 4:
                    return Arrays.asList("1", "2", "3");
            }
        }

        return Collections.emptyList();
    }
}
