package me.trumbo.mysticitems.itemmanager;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemHandler {
    void handleUse(Player player, ItemStack item, boolean isMainHand);
}
