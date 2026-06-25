package com.buyerplugin.gui;

import com.buyerplugin.BuyerPlugin;
import org.bukkit.NamespacedKey;

public final class GuiKeys {

    private static NamespacedKey categoryId;

    private GuiKeys() {
    }

    public static NamespacedKey categoryId(BuyerPlugin plugin) {
        if (categoryId == null) {
            categoryId = new NamespacedKey(plugin, "category_id");
        }
        return categoryId;
    }
}
