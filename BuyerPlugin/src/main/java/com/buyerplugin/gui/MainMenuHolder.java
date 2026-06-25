package com.buyerplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public final class MainMenuHolder implements InventoryHolder {

    private final Inventory inventory;

    public MainMenuHolder(String title) {
        this.inventory = Bukkit.createInventory(this, 27, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
