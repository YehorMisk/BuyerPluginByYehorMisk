package com.buyerplugin.economy;

import com.buyerplugin.BuyerPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultEconomyService {

    private final BuyerPlugin plugin;
    private Economy economy;

    public VaultEconomyService(BuyerPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().severe("Vault not found. Economy features are disabled.");
            return false;
        }

        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            plugin.getLogger().severe("No economy provider found. Install EssentialsX or another Vault-compatible economy plugin.");
            return false;
        }

        economy = provider.getProvider();
        plugin.getLogger().info("Hooked into economy provider: " + economy.getName());
        return true;
    }

    public boolean isAvailable() {
        return economy != null;
    }

    public boolean deposit(Player player, double amount) {
        if (!isAvailable() || amount <= 0) {
            return false;
        }
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public String format(double amount) {
        if (!isAvailable()) {
            return String.valueOf(amount);
        }
        return economy.format(amount);
    }

    public Economy getEconomy() {
        return economy;
    }
}
