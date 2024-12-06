package me.trumbo.mysticitems.itemmanager;

import me.trumbo.mysticitems.MysticItems;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtils {
    public static boolean useEntity(Entity entity, String[] targetTypes) {

        if (Arrays.asList(targetTypes).contains("PLAYER") && entity instanceof Player) {
            return true;
        }

        if (Arrays.asList(targetTypes).contains("MOBS") && !(entity instanceof Player)) {
            return true;
        }

        EntityType entityType = entity.getType();
        for (String type : targetTypes) {
            if (type.equalsIgnoreCase(entityType.name())) {
                return true;
            }
        }

        return false;
    }

    public static void updateItemInHand(Player player, ItemStack item, boolean isMainHand) {
        item.setAmount(item.getAmount() - 1);
        if (isMainHand) {
            player.getInventory().setItemInMainHand(item);
        } else {
            player.getInventory().setItemInOffHand(item);
        }
    }

    public static void setCooldown(Player player, ItemStack item, MysticItems main, String configPath) {
        int cooldown = main.getConfig().getInt(configPath + ".cooldown");
        player.setCooldown(item.getType(), cooldown * 20);
    }

    public static List<LivingEntity> getTargets(Player player, double radius, String[] targetTypes) {
        return player.getWorld().getEntitiesByClass(LivingEntity.class).stream()
                .filter(entity -> entity != player && entity.getLocation().distance(player.getLocation()) <= radius)
                .filter(entity -> useEntity(entity, targetTypes))
                .collect(Collectors.toList());
    }
}

