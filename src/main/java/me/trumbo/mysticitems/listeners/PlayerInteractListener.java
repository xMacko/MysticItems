package me.trumbo.mysticitems.listeners;

import me.trumbo.mysticitems.itemmanager.ItemHandler;
import me.trumbo.mysticitems.itemmanager.ItemType;
import me.trumbo.mysticitems.MysticItems;
import me.trumbo.mysticitems.itemmanager.items.BloodlettingHandler;
import me.trumbo.mysticitems.itemmanager.items.BombHandler;
import me.trumbo.mysticitems.itemmanager.items.LocketHandler;
import me.trumbo.mysticitems.itemmanager.items.SilenceHandler;
import me.trumbo.mysticitems.utils.HexUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class PlayerInteractListener implements Listener {

    private MysticItems main;

    private Map<String, ItemHandler> handlers;

    public PlayerInteractListener(MysticItems main) {
        this.main = main;
        this.handlers = new HashMap<>();

        this.handlers.put("Bomb", new BombHandler(main));
        this.handlers.put("Locket", new LocketHandler(main));
        this.handlers.put("Silence", new SilenceHandler(main));
        this.handlers.put("Bloodletting", new BloodlettingHandler(main));
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getPersistentDataContainer().has(new NamespacedKey(main, "Silence"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Silence.cannot-use-items")));
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

            handleItemUse(event, player, itemInMainHand, true);
            handleItemUse(event, player, itemInOffHand, false);
        }
    }

    private void handleItemUse(PlayerInteractEvent event, Player player, ItemStack item, boolean isMainHand) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        String itemType = ItemType.getItemType(itemMeta);
        ItemHandler handler = handlers.get(itemType);

        if (handler != null) {
            handler.handleUse(player, item, isMainHand);
        }
    }
}


