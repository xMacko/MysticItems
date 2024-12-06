package me.trumbo.mysticitems.itemmanager.items;

import me.trumbo.mysticitems.MysticItems;
import me.trumbo.mysticitems.itemmanager.ItemHandler;
import me.trumbo.mysticitems.itemmanager.ItemUtils;
import me.trumbo.mysticitems.utils.HexUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BloodlettingHandler implements ItemHandler {
    private MysticItems main;
    private Set<UUID> affectedEntities = new HashSet<>();
    private BukkitRunnable bloodTask;

    public BloodlettingHandler(MysticItems main) {
        this.main = main;
    }

    @Override
    public void handleUse(Player player, ItemStack blood, boolean isMainHand) {

        if (player.hasCooldown(blood.getType())) {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Bloodletting.no-cooldown")));
            return;
        }

        double bloodRadius = main.getConfig().getDouble("items.Bloodletting.blood-radius");
        int bloodDuration = main.getConfig().getInt("items.Bloodletting.duration") * 20;
        int bloodDamage = main.getConfig().getInt("items.Bloodletting.damage");
        String targetConfig = main.getConfig().getString("items.Bloodletting.target", "");
        boolean cancelIfNoEntities = main.getConfig().getBoolean("items.Bloodletting.cancel-if-no-entities", false);
        String actionBarMessage = HexUtil.translate(main.getConfig().getString("items.Bloodletting.action-bar-message"));
        int damageInterval = main.getConfig().getInt("items.Bloodletting.damage-interval") * 20;
        String bloodedCountMessage = main.getConfig().getString("items.Bloodletting.blood-message");

        String[] targetTypes = targetConfig.split(",");

        List<LivingEntity> initialTargets = ItemUtils.getTargets(player, bloodRadius, targetTypes);

        if (initialTargets.isEmpty() && cancelIfNoEntities) {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Bloodletting.no-entities")));
            return;
        }

        if (initialTargets.isEmpty()) {
            if (cancelIfNoEntities) {
                player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Bloodletting.no-entities")));
                return;
            }
        }

        String bloodMessage = bloodedCountMessage.replace("%count%", String.valueOf(initialTargets.size()));
        player.sendMessage(HexUtil.translate(bloodMessage));

        for (LivingEntity entity : initialTargets) {
            affectedEntities.add(entity.getUniqueId());
        }

        ItemUtils.updateItemInHand(player, blood, isMainHand);

        ItemUtils.setCooldown(player, blood, main, "items.Bloodletting");

        bloodTask = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= bloodDuration) {
                    cancel();
                    return;
                }

                if (ticks % damageInterval == 0) {
                    for (UUID uuid : affectedEntities) {
                        LivingEntity entity = (LivingEntity) main.getServer().getEntity(uuid);
                        if (entity != null) {
                            if (entity instanceof Player) {
                                Player targetPlayer = (Player) entity;
                                targetPlayer.sendActionBar(actionBarMessage);
                            }
                            entity.damage(bloodDamage);
                        }
                    }
                }

                ticks++;
            }
        };

        bloodTask.runTaskTimer(main, 0, 1);
    }
}
