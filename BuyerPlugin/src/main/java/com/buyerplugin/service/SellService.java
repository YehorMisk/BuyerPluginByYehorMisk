package com.buyerplugin.service;

import com.buyerplugin.BuyerPlugin;
import com.buyerplugin.config.BuyerConfig;
import com.buyerplugin.economy.VaultEconomyService;
import com.buyerplugin.model.BuyItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public final class SellService {

    public enum SellMode {
        ONE(1),
        STACK(PriceCalculator.STACK_SIZE),
        ALL(-1);

        private final int amount;

        SellMode(int amount) {
            this.amount = amount;
        }

        public int resolveAmount(int available) {
            if (this == ALL) {
                return available;
            }
            return Math.min(amount, available);
        }
    }

    public enum SellResult {
        SUCCESS,
        NO_ITEMS,
        ECONOMY_UNAVAILABLE
    }

    private final BuyerPlugin plugin;
    private final BuyerConfig config;
    private final VaultEconomyService economyService;

    public SellService(BuyerPlugin plugin, BuyerConfig config, VaultEconomyService economyService) {
        this.plugin = plugin;
        this.config = config;
        this.economyService = economyService;
    }

    public SellResult sell(Player player, BuyItem buyItem, SellMode mode) {
        if (!economyService.isAvailable()) {
            player.sendMessage(config.formatMessage("economy-disabled", Map.of()));
            return SellResult.ECONOMY_UNAVAILABLE;
        }

        int available = countItems(player, buyItem.getMaterial());
        if (available <= 0) {
            player.sendMessage(config.formatMessage("no-items", Map.of()));
            return SellResult.NO_ITEMS;
        }

        int sellAmount = mode.resolveAmount(available);
        if (sellAmount <= 0) {
            player.sendMessage(config.formatMessage("no-items", Map.of()));
            return SellResult.NO_ITEMS;
        }

        removeItems(player, buyItem.getMaterial(), sellAmount);

        double payout = buyItem.totalPrice(sellAmount);
        economyService.deposit(player, payout);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("amount", String.valueOf(sellAmount));
        placeholders.put("item", buyItem.getDisplayName());
        placeholders.put("price", economyService.format(payout));

        player.sendMessage(config.formatMessage("sold", placeholders));
        return SellResult.SUCCESS;
    }

    public int countItems(Player player, Material material) {
        int total = 0;
        PlayerInventory inventory = player.getInventory();
        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack == null || stack.getType() != material) {
                continue;
            }
            total += stack.getAmount();
        }
        return total;
    }

    private void removeItems(Player player, Material material, int amount) {
        int remaining = amount;
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getStorageContents();

        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack stack = contents[slot];
            if (stack == null || stack.getType() != material) {
                continue;
            }

            int remove = Math.min(remaining, stack.getAmount());
            stack.setAmount(stack.getAmount() - remove);
            if (stack.getAmount() <= 0) {
                contents[slot] = null;
            }
            remaining -= remove;
            if (remaining <= 0) {
                break;
            }
        }

        inventory.setStorageContents(contents);
    }
}
