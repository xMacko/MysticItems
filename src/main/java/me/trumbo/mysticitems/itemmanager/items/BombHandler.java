package me.trumbo.mysticitems.itemmanager.items;

import me.trumbo.mysticitems.MysticItems;
import me.trumbo.mysticitems.itemmanager.ItemHandler;
import me.trumbo.mysticitems.itemmanager.ItemUtils;
import me.trumbo.mysticitems.utils.HexUtil;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class BombHandler implements ItemHandler {
    private MysticItems main;

    public BombHandler(MysticItems main) {
        this.main = main;
    }

    @Override
    public void handleUse(Player player, ItemStack bomb, boolean isMainHand) {

        if (player.hasCooldown(bomb.getType())) {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Bomb.no-cooldown")));
            return;
        }

        double pushRadius = main.getConfig().getDouble("items.Bomb.push-radius");
        double pushStrength = main.getConfig().getDouble("items.Bomb.push-strength");
        String targetConfig = main.getConfig().getString("items.Bomb.target", "");
        boolean cancelIfNoEntities = main.getConfig().getBoolean("items.Bomb.cancel-if-no-entities", false);

        String[] targetTypes = targetConfig.split(",");

        List<LivingEntity> initialTargets = ItemUtils.getTargets(player, pushRadius, targetTypes);

        if (initialTargets.isEmpty() && cancelIfNoEntities) {
            player.sendMessage(HexUtil.translate(main.getConfig().getString("items.Bomb.no-entities")));
            return;
        }

        ItemUtils.updateItemInHand(player, bomb, isMainHand);

        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, player.getLocation(), 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        initialTargets.forEach(entity -> {
            Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            entity.setVelocity(direction.multiply(pushStrength));
        });

        ItemUtils.setCooldown(player, bomb, main, "items.Bomb");
    }
}
