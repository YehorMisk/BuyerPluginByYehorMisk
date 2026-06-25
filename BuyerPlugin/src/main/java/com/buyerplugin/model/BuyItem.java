package com.buyerplugin.model;

import com.buyerplugin.service.PriceCalculator;
import org.bukkit.Material;

public final class BuyItem {

    private final Material material;
    private final double unitPrice;
    private final String displayName;

    public BuyItem(Material material, double unitPrice, String displayName) {
        this.material = material;
        this.unitPrice = unitPrice;
        this.displayName = displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getStackPrice() {
        return PriceCalculator.stackPrice(unitPrice);
    }

    public double totalPrice(int amount) {
        return PriceCalculator.totalPrice(unitPrice, amount);
    }
}
