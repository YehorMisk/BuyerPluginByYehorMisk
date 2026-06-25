package com.buyerplugin.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BuyerCategory {

    private final String id;
    private final String displayName;
    private final Material icon;
    private final int slot;
    private final Map<Material, BuyItem> items;

    public BuyerCategory(
            String id,
            String displayName,
            Material icon,
            int slot,
            Map<Material, BuyItem> items
    ) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
        this.slot = slot;
        this.items = Collections.unmodifiableMap(new LinkedHashMap<>(items));
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public int getSlot() {
        return slot;
    }

    public Map<Material, BuyItem> getItems() {
        return items;
    }

    public BuyItem getItem(Material material) {
        return items.get(material);
    }

    public boolean contains(Material material) {
        return items.containsKey(material);
    }

    public ItemStack createIconItem() {
        return new ItemStack(icon);
    }
}
