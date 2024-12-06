package me.trumbo.mysticitems.itemmanager;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public enum ItemType {
    BOMB("Bomb", new NamespacedKey("mysticitems", "bomb")),
    LOCKET("Locket", new NamespacedKey("mysticitems", "locket")),
    SILENCE("Silence", new NamespacedKey("mysticitems", "silence")),
    BLOODLETTING("Bloodletting", new NamespacedKey("mysticitems", "bloodletting"));

    private final String typeName;
    private final NamespacedKey key;

    ItemType(String typeName, NamespacedKey key) {
        this.typeName = typeName;
        this.key = key;
    }

    public String getTypeName() {
        return typeName;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public static ItemType fromName(String name) {
        try {
            return ItemType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String getItemType(ItemMeta itemMeta) {
        for (ItemType itemType : values()) {
            if (itemMeta.getPersistentDataContainer().has(itemType.getKey(), PersistentDataType.STRING)) {
                return itemType.getTypeName();
            }
        }
        return "Unknown";
    }
}

