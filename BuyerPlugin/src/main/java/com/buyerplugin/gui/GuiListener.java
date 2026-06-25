package com.buyerplugin.gui;

import com.buyerplugin.BuyerPlugin;
import com.buyerplugin.config.BuyerConfig;
import com.buyerplugin.model.BuyItem;
import com.buyerplugin.model.BuyerCategory;
import com.buyerplugin.service.SellService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class GuiListener implements Listener {

    private final BuyerPlugin plugin;
    private final BuyerConfig config;
    private final SellService sellService;

    public GuiListener(BuyerPlugin plugin, BuyerConfig config, SellService sellService) {
        this.plugin = plugin;
        this.config = config;
        this.sellService = sellService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        InventoryHolder holder = top.getHolder();

        if (holder instanceof MainMenuHolder) {
            handleMainMenuClick(event);
        } else if (holder instanceof CategoryMenuHolder categoryHolder) {
            handleCategoryMenuClick(event, categoryHolder);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) {
            return;
        }

        String categoryId = meta.getPersistentDataContainer().get(GuiKeys.categoryId(plugin), PersistentDataType.STRING);
        if (categoryId == null) {
            return;
        }

        config.getCategory(categoryId).ifPresent(category -> MenuFactory.openCategoryMenu(player, category, config));
    }

    private void handleCategoryMenuClick(InventoryClickEvent event, CategoryMenuHolder holder) {
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (event.getSlot() == MenuFactory.getBackSlot() && clicked.getType() == Material.BARRIER) {
            MenuFactory.openMainMenu(player, config, plugin);
            return;
        }

        if (clicked.getType() == Material.BARRIER || clicked.getType().name().endsWith("_STAINED_GLASS_PANE")) {
            return;
        }

        BuyerCategory category = holder.getCategory();
        BuyItem buyItem = category.getItem(clicked.getType());
        if (buyItem == null) {
            return;
        }

        SellService.SellMode mode = resolveSellMode(event.getClick());
        if (mode == null) {
            return;
        }

        sellService.sell(player, buyItem, mode);
    }

    private SellService.SellMode resolveSellMode(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> SellService.SellMode.ONE;
            case RIGHT -> SellService.SellMode.STACK;
            case MIDDLE -> SellService.SellMode.ALL;
            case SHIFT_LEFT, SHIFT_RIGHT -> SellService.SellMode.ALL;
            default -> null;
        };
    }
}
