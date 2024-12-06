package me.trumbo.mysticitems.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexUtil {

    public static String translate(String message) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

        for (Matcher matcher = pattern.matcher(message); matcher.find(); matcher = pattern.matcher(message)) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, "" + ChatColor.of(color.replace("&", "")));
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
