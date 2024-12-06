package me.trumbo.mysticitems.commands;

import me.trumbo.mysticitems.itemmanager.ItemType;
import me.trumbo.mysticitems.MysticItems;
import me.trumbo.mysticitems.utils.HexUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor {

    private final MysticItems main;

    public MainCommand(MysticItems main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("MITEMS.admin")) {
            sender.sendMessage(HexUtil.translate(main.getConfig().getString("messages.no-perm")));
            return true;
        }

        if (args.length == 0) {
            List<String> commandList = main.getConfig().getStringList("messages.command-list");
            sender.sendMessage(HexUtil.translate(String.join("\n", commandList)));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            main.reloadConfig();
            sender.sendMessage(HexUtil.translate(main.getConfig().getString("messages.config-reloaded")));
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 4) {
                sender.sendMessage(HexUtil.translate(main.getConfig().getString("messages.usage")));
                return true;
            }

            String playerName = args[1];
            Player player = main.getServer().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(HexUtil.translate(main.getConfig().getString("messages.player-not-found")));
                return true;
            }

            String itemTypeName = args[2];
            ItemType itemType = ItemType.fromName(itemTypeName);
            if (itemType == null) {
                sender.sendMessage(HexUtil.translate(main.getConfig().getString("messages.invalid-item-type")));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(HexUtil.translate(main.getConfig().getString("messages.invalid-amount")));
                return true;
            }

            if (amount <= 0) {
                sender.sendMessage(HexUtil.translate(main.getConfig().getString("messages.positive-amount")));
                return true;
            }

            String materialName = main.getConfig().getString("items." + itemType.getTypeName().toLowerCase() + ".material");
            Material material = Material.getMaterial(materialName.toUpperCase());
            if (material == null) {
                sender.sendMessage(HexUtil.translate("&c&lНеизвестный предмет"));
                return true;
            }

            ItemStack item = new ItemStack(material, amount);

            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String displayName = main.getConfig().getString("items." + itemType.getTypeName().toLowerCase() + ".display-name");
                meta.setDisplayName(HexUtil.translate(displayName));

                List<String> lore = main.getConfig().getStringList("items." + itemType.getTypeName().toLowerCase() + ".item-lore");
                meta.setLore(lore.stream().map(HexUtil::translate).collect(Collectors.toList()));

                meta.getPersistentDataContainer().set(new NamespacedKey(main, itemType.getTypeName()), PersistentDataType.STRING, "");
                item.setItemMeta(meta);
            }

            player.getInventory().addItem(item);

            String message = main.getConfig().getString("messages.player-given-item")
                    .replace("%player%", playerName)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%item_type%", itemType.getTypeName());
            sender.sendMessage(HexUtil.translate(message));

            return true;
        }

        return true;
    }
}


