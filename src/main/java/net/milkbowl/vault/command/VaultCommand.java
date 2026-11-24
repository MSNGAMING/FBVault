package net.milkbowl.vault.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.util.FoliaScheduler;
import net.milkbowl.vault.util.VaultText;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Modern Paper native BasicCommand for Vault administration and diagnostics.
 * Integrates directly with Paper's Brigadier dispatcher and Lifecycle API.
 */
public class VaultCommand implements BasicCommand {

    private final AtomicBoolean isConverting = new AtomicBoolean(false);

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        CommandSender sender = stack.getSender();
        handleCommand(sender, "vault", args);
    }

    public boolean onLegacyCommand(CommandSender sender, String label, String[] args) {
        handleCommand(sender, label, args);
        return true;
    }

    private void handleCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission("vault.admin")) {
            VaultText.send(sender, "<red>You do not have permission to execute this command (vault.admin).</red>");
            return;
        }

        if (label.equalsIgnoreCase("vault-info") || (args.length == 0 || "info".equalsIgnoreCase(args[0]))) {
            showInfo(sender);
            return;
        }

        if ("convert".equalsIgnoreCase(args[0])) {
            if (args.length < 3) {
                VaultText.send(sender, "<yellow>Usage: <gray>/vault convert <fromEconomy> <toEconomy></gray></yellow>");
                return;
            }
            executeConvert(sender, args[1], args[2]);
            return;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            Vault plugin = Vault.getInstance();
            if (plugin != null) {
                plugin.reloadConfig();
                VaultText.send(sender, "<green>Vault configuration reloaded successfully.</green>");
            } else {
                VaultText.send(sender, "<red>Vault plugin instance is not available.</red>");
            }
            return;
        }

        showHelp(sender);
    }

    @Override
    public @Nullable String permission() {
        return "vault.admin";
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("vault.admin")) {
            return Collections.emptyList();
        }

        if (args.length <= 1) {
            String input = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> subcommands = List.of("info", "convert", "reload", "help");
            List<String> matches = new ArrayList<>(subcommands.size());
            for (String sub : subcommands) {
                if (sub.startsWith(input)) {
                    matches.add(sub);
                }
            }
            return matches;
        }

        if (args.length == 2 && "convert".equalsIgnoreCase(args[0])) {
            return matchEconomies(args[1]);
        }

        if (args.length == 3 && "convert".equalsIgnoreCase(args[0])) {
            return matchEconomies(args[2]);
        }

        return Collections.emptyList();
    }

    public void showHelp(CommandSender sender) {
        VaultText.send(sender, "<gold><bold>Vault Commands:</bold></gold>");
        VaultText.send(sender, "  <yellow>/vault info</yellow> <gray>- Displays registered services and status</gray>");
        VaultText.send(sender, "  <yellow>/vault convert <from> <to></yellow> <gray>- Asynchronously converts economy data</gray>");
        VaultText.send(sender, "  <yellow>/vault reload</yellow> <gray>- Reloads Vault configuration</gray>");
    }

    public void showInfo(CommandSender sender) {
        ServicesManager sm = Bukkit.getServicesManager();

        RegisteredServiceProvider<Economy> rspEcon = sm.getRegistration(Economy.class);
        RegisteredServiceProvider<Permission> rspPerm = sm.getRegistration(Permission.class);
        RegisteredServiceProvider<Chat> rspChat = sm.getRegistration(Chat.class);

        Collection<RegisteredServiceProvider<Economy>> econs = sm.getRegistrations(Economy.class);
        Collection<RegisteredServiceProvider<Permission>> perms = sm.getRegistrations(Permission.class);
        Collection<RegisteredServiceProvider<Chat>> chats = sm.getRegistrations(Chat.class);

        String activeEconName = rspEcon != null ? rspEcon.getProvider().getName() : "None";
        String activePermName = rspPerm != null ? rspPerm.getProvider().getName() : "None";
        String activeChatName = rspChat != null ? rspChat.getProvider().getName() : "None";

        String allEcons = formatProviders(econs);
        String allPerms = formatProviders(perms);
        String allChats = formatProviders(chats);

        String platform = FoliaScheduler.isFolia() ? "<aqua>Folia (Regionized Multithreading)</aqua>" : "<green>Paper / Purpur</green>";
        Vault plugin = Vault.getInstance();
        String version = plugin != null ? plugin.getDescription().getVersion() : "1.7.3-modern";

        VaultText.send(sender, "<dark_gray>----------------------------------------</dark_gray>");
        VaultText.send(sender, "<gold><bold>Vault v" + version + "</bold> <gray>[Native Paper Architecture]</gray></gold>");
        VaultText.send(sender, "<gray>Platform: </gray>" + platform);
        VaultText.send(sender, "<gray>Economy: </gray><green>" + activeEconName + "</green> <dark_gray>[" + allEcons + "]</dark_gray>");
        VaultText.send(sender, "<gray>Permission: </gray><green>" + activePermName + "</green> <dark_gray>[" + allPerms + "]</dark_gray>");
        VaultText.send(sender, "<gray>Chat: </gray><green>" + activeChatName + "</green> <dark_gray>[" + allChats + "]</dark_gray>");
        VaultText.send(sender, "<dark_gray>----------------------------------------</dark_gray>");
    }

    private <T> String formatProviders(Collection<RegisteredServiceProvider<T>> providers) {
        if (providers == null || providers.isEmpty()) {
            return "None registered";
        }
        List<String> names = new ArrayList<>(providers.size());
        for (RegisteredServiceProvider<T> rsp : providers) {
            names.add(rsp.getPlugin().getName());
        }
        return String.join(", ", names);
    }

    public void executeConvert(CommandSender sender, String fromName, String toName) {
        if (!isConverting.compareAndSet(false, true)) {
            VaultText.send(sender, "<red>An economy conversion is already in progress. Please wait.</red>");
            return;
        }

        ServicesManager sm = Bukkit.getServicesManager();
        Collection<RegisteredServiceProvider<Economy>> econs = sm.getRegistrations(Economy.class);

        Economy fromEconomy = null;
        Economy toEconomy = null;

        for (RegisteredServiceProvider<Economy> rsp : econs) {
            Economy eco = rsp.getProvider();
            String cleanedName = eco.getName().replace(" ", "");
            if (cleanedName.equalsIgnoreCase(fromName.replace(" ", ""))) {
                fromEconomy = eco;
            }
            if (cleanedName.equalsIgnoreCase(toName.replace(" ", ""))) {
                toEconomy = eco;
            }
        }

        if (fromEconomy == null) {
            isConverting.set(false);
            VaultText.send(sender, "<red>Source economy '" + fromName + "' was not found.</red>");
            return;
        }

        if (toEconomy == null) {
            isConverting.set(false);
            VaultText.send(sender, "<red>Target economy '" + toName + "' was not found.</red>");
            return;
        }

        if (fromEconomy == toEconomy) {
            isConverting.set(false);
            VaultText.send(sender, "<red>Source and target economies cannot be the same.</red>");
            return;
        }

        Vault plugin = Vault.getInstance();
        if (plugin == null) {
            isConverting.set(false);
            VaultText.send(sender, "<red>Vault plugin instance is not available for conversion.</red>");
            return;
        }

        final Economy src = fromEconomy;
        final Economy dst = toEconomy;

        VaultText.send(sender, "<gold>[Vault] Starting asynchronous conversion from <yellow>" + src.getName()
                + "</yellow> to <yellow>" + dst.getName() + "</yellow>...</gold>");

        final int batchSize = plugin.getConfig().getInt("conversion.batch-size", 100);
        final long batchDelayTicks = plugin.getConfig().getLong("conversion.batch-delay-ticks", 1L);

        FoliaScheduler.runAsync(plugin, () -> {
            long startTime = System.currentTimeMillis();
            OfflinePlayer[] allPlayers = Bukkit.getOfflinePlayers();
            int total = allPlayers.length;
            int converted = 0;
            int skipped = 0;
            int errors = 0;

            plugin.getLogger().info(String.format("Beginning async economy conversion of %d players...", total));

            for (int i = 0; i < total; i++) {
                OfflinePlayer op = allPlayers[i];
                try {
                    if (src.hasAccount(op)) {
                        if (!dst.hasAccount(op)) {
                            dst.createPlayerAccount(op);
                        }
                        double srcBal = src.getBalance(op);
                        double dstBal = dst.getBalance(op);
                        double diff = srcBal - dstBal;

                        if (diff > 0.0001) {
                            dst.depositPlayer(op, diff);
                            converted++;
                        } else if (diff < -0.0001) {
                            dst.withdrawPlayer(op, -diff);
                            converted++;
                        } else {
                            skipped++;
                        }
                    } else {
                        skipped++;
                    }
                } catch (Exception ex) {
                    errors++;
                    plugin.getLogger().warning("Error converting account for " + op.getName() + ": " + ex.getMessage());
                }

                if ((i + 1) % (batchSize * 5) == 0 || i == total - 1) {
                    double pct = ((i + 1) * 100.0) / total;
                    VaultText.send(sender, String.format("<gray>[Vault] Conversion progress: <yellow>%d/%d</yellow> (<gold>%.1f%%</gold>)</gray>",
                            (i + 1), total, pct));
                }

                if ((i + 1) % batchSize == 0 && batchDelayTicks > 0) {
                    try {
                        Thread.sleep(batchDelayTicks * 50L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;
            isConverting.set(false);

            VaultText.send(sender, "<green><bold>[Vault] Economy conversion finished!</bold></green>");
            VaultText.send(sender, String.format("<gray>Converted: <green>%d</green> | Skipped: <yellow>%d</yellow> | Errors: <red>%d</red> | Time: <aqua>%.2fs</aqua></gray>",
                    converted, skipped, errors, elapsed / 1000.0));
            plugin.getLogger().info(String.format("Async economy conversion completed in %.2fs. Converted: %d, Skipped: %d, Errors: %d",
                    elapsed / 1000.0, converted, skipped, errors));
        });
    }

    public List<String> matchEconomies(String prefix) {
        Collection<RegisteredServiceProvider<Economy>> econs = Bukkit.getServicesManager().getRegistrations(Economy.class);
        if (econs.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> matches = new ArrayList<>(econs.size());
        String lowerPrefix = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        for (RegisteredServiceProvider<Economy> rsp : econs) {
            String name = rsp.getProvider().getName().replace(" ", "");
            if (name.toLowerCase(Locale.ROOT).startsWith(lowerPrefix)) {
                matches.add(name);
            }
        }
        return matches;
    }
}
