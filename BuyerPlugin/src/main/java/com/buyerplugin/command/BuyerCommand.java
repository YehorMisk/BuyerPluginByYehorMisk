package com.buyerplugin.command;

import com.buyerplugin.BuyerPlugin;
import com.buyerplugin.config.BuyerConfig;
import com.buyerplugin.gui.MenuFactory;
import com.buyerplugin.model.BuyerCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class BuyerCommand implements CommandExecutor, TabCompleter {

    private final BuyerPlugin plugin;
    private final BuyerConfig config;

    public BuyerCommand(BuyerPlugin plugin, BuyerConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("buyerplugin.admin")) {
                sender.sendMessage(BuyerConfig.colorize("&cУ вас нет прав для этой команды."));
                return true;
            }
            plugin.reloadPluginConfig();
            sender.sendMessage(config.formatMessage("reload-success", Map.of()));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(BuyerConfig.colorize("&cЭту команду может использовать только игрок."));
            return true;
        }

        if (args.length == 0) {
            MenuFactory.openMainMenu(player, config, plugin);
            return true;
        }

        config.getCategory(args[0]).ifPresentOrElse(
                category -> MenuFactory.openCategoryMenu(player, category, config),
                () -> player.sendMessage(config.formatMessage("unknown-category", Map.of(
                        "category", args[0]
                )))
        );
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("buyerplugin.admin")) {
                completions.add("reload");
            }
            completions.addAll(config.getCategories().keySet());
        }

        String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
        return completions.stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(prefix))
                .toList();
    }
}
