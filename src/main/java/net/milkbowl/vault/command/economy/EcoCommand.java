package net.milkbowl.vault.command.economy;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.builtin.BuiltinEconomy;
import net.milkbowl.vault.util.VaultText;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EcoCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        CommandSender sender = stack.getSender();
        if (!sender.hasPermission("vault.economy.admin")) {
            VaultText.send(sender, "<red>You do not have permission to execute economy administrative commands.</red>");
            return;
        }

        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        Vault plugin = Vault.getInstance();
        if (plugin == null) return;
        BuiltinEconomy eco = plugin.getBuiltinEconomy();
        if (eco == null) {
            VaultText.send(sender, "<red>Built-in economy is currently disabled.</red>");
            return;
        }

        String action = args[0].toLowerCase(Locale.ROOT);
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if ("reset".equals(action)) {
            double startBal = plugin.getConfig().getDouble("builtin-economy.starting-balance", 100.0);
            eco.setBalance(target, startBal);
            VaultText.send(sender, "<green>Reset balance of <yellow>" + target.getName() + "</yellow> to "
                    + eco.format(startBal) + ".</green>");
            return;
        }

        if (args.length < 3) {
            sendUsage(sender);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            VaultText.send(sender, "<red>Invalid amount: " + args[2] + "</red>");
            return;
        }

        if (amount < 0) {
            VaultText.send(sender, "<red>Amount cannot be negative.</red>");
            return;
        }

        switch (action) {
            case "give", "deposit" -> {
                EconomyResponse resp = eco.depositPlayer(target, amount);
                if (resp.transactionSuccess()) {
                    VaultText.send(sender, "<green>Deposited <bold>" + eco.format(amount) + "</bold> to <yellow>"
                            + target.getName() + "</yellow>. New balance: <bold>" + eco.format(resp.balance) + "</bold>.</green>");
                } else {
                    VaultText.send(sender, "<red>Deposit failed: " + resp.errorMessage + "</red>");
                }
            }
            case "take", "withdraw" -> {
                EconomyResponse resp = eco.withdrawPlayer(target, amount);
                if (resp.transactionSuccess()) {
                    VaultText.send(sender, "<green>Withdrew <bold>" + eco.format(amount) + "</bold> from <yellow>"
                            + target.getName() + "</yellow>. New balance: <bold>" + eco.format(resp.balance) + "</bold>.</green>");
                } else {
                    VaultText.send(sender, "<red>Withdraw failed: " + resp.errorMessage + "</red>");
                }
            }
            case "set" -> {
                EconomyResponse resp = eco.setBalance(target, amount);
                if (resp.transactionSuccess()) {
                    VaultText.send(sender, "<green>Set balance of <yellow>" + target.getName() + "</yellow> to <bold>"
                            + eco.format(resp.balance) + "</bold>.</green>");
                } else {
                    VaultText.send(sender, "<red>Failed to set balance: " + resp.errorMessage + "</red>");
                }
            }
            default -> sendUsage(sender);
        }
    }

    private void sendUsage(CommandSender sender) {
        VaultText.send(sender, "<gold><bold>Economy Admin Commands:</bold></gold>");
        VaultText.send(sender, "  <yellow>/eco give <player> <amount></yellow> <gray>- Gives money to player</gray>");
        VaultText.send(sender, "  <yellow>/eco take <player> <amount></yellow> <gray>- Takes money from player</gray>");
        VaultText.send(sender, "  <yellow>/eco set <player> <amount></yellow> <gray>- Sets player balance</gray>");
        VaultText.send(sender, "  <yellow>/eco reset <player></yellow> <gray>- Resets player balance to starting balance</gray>");
    }

    @Override
    public @Nullable String permission() {
        return "vault.economy.admin";
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("vault.economy.admin")) {
            return Collections.emptyList();
        }

        if (args.length <= 1) {
            String input = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> actions = List.of("give", "take", "set", "reset");
            List<String> matches = new ArrayList<>();
            for (String a : actions) {
                if (a.startsWith(input)) matches.add(a);
            }
            return matches;
        }

        if (args.length == 2) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    names.add(p.getName());
                }
            }
            return names;
        }

        if (args.length == 3 && !args[0].equalsIgnoreCase("reset")) {
            return List.of("10", "100", "500", "1000");
        }

        return Collections.emptyList();
    }
}
