package com.buyerplugin;

import com.buyerplugin.command.BuyerCommand;
import com.buyerplugin.config.BuyerConfig;
import com.buyerplugin.economy.VaultEconomyService;
import com.buyerplugin.gui.GuiListener;
import com.buyerplugin.service.SellService;
import org.bukkit.plugin.java.JavaPlugin;

public final class BuyerPlugin extends JavaPlugin {

    private BuyerConfig buyerConfig;
    private VaultEconomyService economyService;
    private SellService sellService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        buyerConfig = new BuyerConfig(this);
        reloadPluginConfig();

        economyService = new VaultEconomyService(this);
        if (!economyService.setup()) {
            getLogger().warning("BuyerPlugin enabled without economy. Selling will not work until Vault + economy plugin are installed.");
        }

        sellService = new SellService(this, buyerConfig, economyService);

        BuyerCommand buyerCommand = new BuyerCommand(this, buyerConfig);
        getCommand("buyer").setExecutor(buyerCommand);
        getCommand("buyer").setTabCompleter(buyerCommand);

        getServer().getPluginManager().registerEvents(new GuiListener(this, buyerConfig, sellService), this);

        getLogger().info("BuyerPlugin enabled with " + buyerConfig.getCategories().size() + " categories.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BuyerPlugin disabled.");
    }

    public void reloadPluginConfig() {
        buyerConfig.reload();
    }

    public BuyerConfig getBuyerConfig() {
        return buyerConfig;
    }

    public VaultEconomyService getEconomyService() {
        return economyService;
    }

    public SellService getSellService() {
        return sellService;
    }
}
