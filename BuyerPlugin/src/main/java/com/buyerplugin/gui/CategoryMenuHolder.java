package com.buyerplugin.gui;

import com.buyerplugin.model.BuyerCategory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class CategoryMenuHolder implements InventoryHolder {

    private final BuyerCategory category;
    private final Inventory inventory;

    public CategoryMenuHolder(BuyerCategory category, String title) {
        this.category = category;
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public BuyerCategory getCategory() {
        return category;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
