package com.buyerplugin.gui;

import com.buyerplugin.BuyerPlugin;
import com.buyerplugin.config.BuyerConfig;
import com.buyerplugin.model.BuyItem;
import com.buyerplugin.model.BuyerCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class MenuFactory {

    private static final int MAX_ITEMS = 21;
    private static final int[] CONTENT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private static final int SLOT_BACK = 49;

    private MenuFactory() {
    }

    public static void openMainMenu(Player player, BuyerConfig config, BuyerPlugin plugin) {
        MainMenuHolder holder = new MainMenuHolder(config.getMainMenuTitle());
        Inventory inventory = holder.getInventory();

        fillBorder(inventory, Material.GRAY_STAINED_GLASS_PANE);

        int autoSlot = 11;
        for (BuyerCategory category : config.getCategories().values()) {
            int slot = category.getSlot() >= 0 ? category.getSlot() : autoSlot++;
            inventory.setItem(slot, createCategoryButton(category, plugin));
        }

        player.openInventory(inventory);
    }

    public static void openCategoryMenu(Player player, BuyerCategory category, BuyerConfig config) {
        CategoryMenuHolder holder = new CategoryMenuHolder(category, config.getCategoryMenuTitle(category));
        Inventory inventory = holder.getInventory();

        fillBorder(inventory, Material.BLACK_STAINED_GLASS_PANE);

        List<BuyItem> items = new ArrayList<>(category.getItems().values());
        int count = Math.min(items.size(), MAX_ITEMS);
        for (int i = 0; i < count; i++) {
            inventory.setItem(CONTENT_SLOTS[i], createBuyItemStack(items.get(i)));
        }

        inventory.setItem(SLOT_BACK, createBackButton());
        player.openInventory(inventory);
    }

    public static int getBackSlot() {
        return SLOT_BACK;
    }

    private static ItemStack createCategoryButton(BuyerCategory category, BuyerPlugin plugin) {
        ItemStack item = category.createIconItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(category.getDisplayName());
            meta.setLore(List.of(
                    BuyerConfig.colorize("&7Нажмите, чтобы открыть"),
                    BuyerConfig.colorize("&7Предметов: &f" + category.getItems().size())
            ));
            meta.getPersistentDataContainer().set(GuiKeys.categoryId(plugin), PersistentDataType.STRING, category.getId());
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createBuyItemStack(BuyItem buyItem) {
        ItemStack item = new ItemStack(buyItem.getMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(buyItem.getDisplayName());
            meta.setLore(buildItemLore(buyItem));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static List<String> buildItemLore(BuyItem buyItem) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(BuyerConfig.colorize("&7Цена за 1 шт.: &a$" + com.buyerplugin.service.PriceCalculator.format(buyItem.getUnitPrice())));
        lore.add(BuyerConfig.colorize("&7Цена за 64 шт.: &a$" + com.buyerplugin.service.PriceCalculator.format(buyItem.getStackPrice())));
        lore.add("");
        lore.add(BuyerConfig.colorize("&eЛКМ &7— продать &f1 &7шт."));
        lore.add(BuyerConfig.colorize("&eПКМ &7— продать &f64 &7шт."));
        lore.add(BuyerConfig.colorize("&eСКМ &7— продать &fвсё"));
        lore.add(BuyerConfig.colorize("&7Shift+ЛКМ — продать всё &8(альтернатива)"));
        return lore;
    }

    private static ItemStack createBackButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(BuyerConfig.colorize("&cНазад"));
            meta.setLore(List.of(BuyerConfig.colorize("&7Вернуться к категориям")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static void fillBorder(Inventory inventory, Material pane) {
        ItemStack filler = new ItemStack(pane);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            int row = slot / 9;
            int col = slot % 9;
            if (row == 0 || row == inventory.getSize() / 9 - 1 || col == 0 || col == 8) {
                inventory.setItem(slot, filler);
            }
        }
    }
}
