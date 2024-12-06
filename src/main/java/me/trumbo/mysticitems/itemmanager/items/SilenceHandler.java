package me.trumbo.mysticitems.itemmanager.items;

import me.trumbo.mysticitems.MysticItems;
import me.trumbo.mysticitems.itemmanager.ItemHandler;
import me.trumbo.mysticitems.itemmanager.ItemUtils;
import me.trumbo.mysticitems.utils.HexUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class SilenceHandler implements ItemHandler {
    private MysticItems main;

    public SilenceHandler(MysticItems main) {
        this.main = main;
    }

    @Override
    public void handleUse(Player player, ItemStack silence, boolean isMainHand) {

        if (player.hasCooldown(silence.getType())) {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Silence.no-cooldown")));
            return;
        }

        double silenceRadius = main.getConfig().getDouble("items.Silence.silence-radius");
        int silenceDuration = main.getConfig().getInt("items.Silence.duration") * 20;

        Player closestPlayer = player.getWorld().getPlayers().stream()
                .filter(target -> target != player && target.getLocation().distance(player.getLocation()) <= silenceRadius)
                .min((p1, p2) -> Double.compare(
                        p1.getLocation().distance(player.getLocation()),
                        p2.getLocation().distance(player.getLocation())
                )).orElse(null);

        if (closestPlayer != null) {

            PersistentDataContainer container = closestPlayer.getPersistentDataContainer();
            container.set(new NamespacedKey(main, "Silence"), PersistentDataType.STRING, "true");

            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Silence.success"))
                    .replace("%target%", closestPlayer.getName()));

            main.getServer().getScheduler().runTaskLater(main, () -> {
                container.remove(new NamespacedKey(main, "Silence"));
                closestPlayer.sendMessage(HexUtil.translate(main.getConfig().getString("items.Silence.removed")));
            }, silenceDuration);
        } else {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Silence.no-target")));
            return;
        }

        ItemUtils.updateItemInHand(player, silence, isMainHand);

        ItemUtils.setCooldown(player, silence, main, "items.Silence");
    }
}
