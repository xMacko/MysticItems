package me.trumbo.mysticitems.itemmanager.items;

import me.trumbo.mysticitems.MysticItems;
import me.trumbo.mysticitems.itemmanager.ItemHandler;
import me.trumbo.mysticitems.itemmanager.ItemUtils;
import me.trumbo.mysticitems.utils.HexUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class LocketHandler implements ItemHandler {

    private MysticItems main;

    public LocketHandler(MysticItems main) {
        this.main = main;
    }

    @Override
    public void handleUse(Player player, ItemStack locket, boolean isMainHand) {

        if (player.hasCooldown(locket.getType())) {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Locket.no-cooldown")));
            return;
        }

        double healRadius = main.getConfig().getDouble("items.Locket.heal-radius");
        double healAmount = main.getConfig().getDouble("items.Locket.heal-amount");
        String targetConfig = main.getConfig().getString("items.Locket.target", "");
        boolean cancelIfNoEntities = main.getConfig().getBoolean("items.Locket.cancel-if-no-entities", false);
        boolean cancelIfAllFullHealth = main.getConfig().getBoolean("items.Locket.cancel-if-all-full-health", false);
        boolean healSelf = main.getConfig().getBoolean("items.Locket.heal-self", true);

        String[] targetTypes = targetConfig.split(",");

        List<LivingEntity> initialTargets = ItemUtils.getTargets(player, healRadius, targetTypes);

        if (initialTargets.isEmpty() && cancelIfNoEntities) {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Locket.no-entities")));
            return;
        }

        if (initialTargets.isEmpty()) {
            if (cancelIfNoEntities) {
                player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Locket.no-entities")));
                return;
            }
        } else {
            List<LivingEntity> entitiesToHeal = initialTargets.stream()
                    .filter(entity -> entity.getHealth() < entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())
                    .collect(Collectors.toList());

            if (entitiesToHeal.isEmpty() && cancelIfAllFullHealth) {
                player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Locket.all-full-health")));
                return;
            }

            final int[] healedCount = {0};
            entitiesToHeal.forEach(entity -> {
                double newHealth = Math.min(entity.getHealth() + healAmount, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                entity.setHealth(newHealth);
                healedCount[0]++;
            });

            String message = main.getConfig().getString("items.Locket.heal-message");
            if (message != null) {
                message = message.replace("%healed_count%", String.valueOf(healedCount[0]));
                player.sendMessage(HexUtil.translate(message));
            }
        }

        if (healSelf) {
            double newHealth = Math.min(player.getHealth() + healAmount, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            player.setHealth(newHealth);
        }

        ItemUtils.updateItemInHand(player, locket, isMainHand);

        ItemUtils.setCooldown(player, locket, main, "items.locket");
    }
}
