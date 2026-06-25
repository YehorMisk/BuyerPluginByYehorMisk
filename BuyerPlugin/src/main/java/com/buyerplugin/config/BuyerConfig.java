package com.buyerplugin.config;

import com.buyerplugin.BuyerPlugin;
import com.buyerplugin.model.BuyItem;
import com.buyerplugin.model.BuyerCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class BuyerConfig {

    private final BuyerPlugin plugin;
    private final Map<String, String> messages = new LinkedHashMap<>();
    private final Map<String, BuyerCategory> categories = new LinkedHashMap<>();
    private String mainMenuTitle;
    private String categoryMenuTitle;

    public BuyerConfig(BuyerPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        messages.clear();
        categories.clear();

        ConfigurationSection messagesSection = plugin.getConfig().getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                messages.put(key, colorize(messagesSection.getString(key, "")));
            }
        }

        mainMenuTitle = colorize(plugin.getConfig().getString("gui.main-title", "&8Скупщики"));
        categoryMenuTitle = colorize(plugin.getConfig().getString("gui.category-title", "&8{category}"));

        ConfigurationSection categoriesSection = plugin.getConfig().getConfigurationSection("categories");
        if (categoriesSection == null) {
            plugin.getLogger().warning("No categories section found in config.yml");
            return;
        }

        for (String categoryId : categoriesSection.getKeys(false)) {
            ConfigurationSection categorySection = categoriesSection.getConfigurationSection(categoryId);
            if (categorySection == null) {
                continue;
            }

            String displayName = colorize(categorySection.getString("display-name", categoryId));
            Material icon = parseMaterial(categorySection.getString("icon", "CHEST"), categoryId + ".icon", true);
            int slot = categorySection.getInt("slot", -1);

            Map<Material, BuyItem> items = new LinkedHashMap<>();
            ConfigurationSection itemsSection = categorySection.getConfigurationSection("items");
            if (itemsSection != null) {
                for (String materialKey : itemsSection.getKeys(false)) {
                    Material material = parseMaterial(materialKey, categoryId + ".items." + materialKey, false);
                    if (material == null || material == Material.AIR) {
                        continue;
                    }

                    ParsedItem parsed = parseItemEntry(itemsSection, materialKey, categoryId);
                    if (parsed == null) {
                        continue;
                    }

                    items.put(material, new BuyItem(material, parsed.price(), parsed.displayName()));
                }
            }

            if (items.isEmpty()) {
                plugin.getLogger().warning("Category '" + categoryId + "' has no valid items and was skipped.");
                continue;
            }

            categories.put(categoryId.toLowerCase(Locale.ROOT), new BuyerCategory(
                    categoryId.toLowerCase(Locale.ROOT),
                    displayName,
                    icon,
                    slot,
                    items
            ));
        }
    }

    public Map<String, BuyerCategory> getCategories() {
        return categories;
    }

    public Optional<BuyerCategory> getCategory(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(categories.get(id.toLowerCase(Locale.ROOT)));
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "");
    }

    public String formatMessage(String key, Map<String, String> placeholders) {
        String message = getMessage(key);
        if (message.isEmpty()) {
            return "";
        }
        String prefix = getMessage("prefix");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return prefix + message;
    }

    public String getMainMenuTitle() {
        return mainMenuTitle;
    }

    public String getCategoryMenuTitle(BuyerCategory category) {
        return categoryMenuTitle.replace("{category}", category.getDisplayName());
    }

    public static String colorize(String input) {
        if (input == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private Material parseMaterial(String name, String context, boolean fallback) {
        if (name == null || name.isBlank()) {
            plugin.getLogger().warning("Missing material for " + context);
            return fallback ? Material.CHEST : null;
        }

        Material material = Material.matchMaterial(name.trim().toUpperCase(Locale.ROOT));
        if (material == null) {
            plugin.getLogger().warning("Unknown material '" + name + "' for " + context);
            return fallback ? Material.CHEST : null;
        }
        return material;
    }

    /**
     * Поддерживает два формата в config.yml:
     *   SUGAR_CANE: 60.0
     *   SUGAR_CANE:
     *     price: 60.0
     *     name: "Тростник"
     */
    private ParsedItem parseItemEntry(ConfigurationSection itemsSection, String materialKey, String categoryId) {
        if (itemsSection.isConfigurationSection(materialKey)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(materialKey);
            if (itemSection == null) {
                return null;
            }
            if (!itemSection.contains("price")) {
                plugin.getLogger().warning("Missing price for " + categoryId + ".items." + materialKey);
                return null;
            }
            double price = itemSection.getDouble("price");
            String name = itemSection.getString("name", materialKey);
            return new ParsedItem(price, colorize(name));
        }

        if (!itemsSection.isDouble(materialKey) && !itemsSection.isInt(materialKey)) {
            plugin.getLogger().warning("Invalid item entry for " + categoryId + ".items." + materialKey);
            return null;
        }

        double price = itemsSection.getDouble(materialKey);
        return new ParsedItem(price, colorize("&f" + materialKey));
    }

    private record ParsedItem(double price, String displayName) {
    }
}
