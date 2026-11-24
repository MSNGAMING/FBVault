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

public class PayCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        CommandSender sender = stack.getSender();
        if (!(sender instanceof Player player)) {
            VaultText.send(sender, "<red>Only players can pay other players.</red>");
            return;
        }

        if (args.length < 2) {
            VaultText.send(player, "<yellow>Usage: <gray>/pay <player> <amount></gray></yellow>");
            return;
        }

        Vault plugin = Vault.getInstance();
        if (plugin == null) return;
        BuiltinEconomy eco = plugin.getBuiltinEconomy();
        if (eco == null) {
            VaultText.send(player, "<red>Built-in economy is currently disabled.</red>");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            VaultText.send(player, "<red>Invalid amount specified: " + args[1] + "</red>");
            return;
        }

        if (amount <= 0.001) {
            VaultText.send(player, "<red>Payment amount must be greater than zero.</red>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getUniqueId().equals(player.getUniqueId())) {
            VaultText.send(player, "<red>You cannot pay yourself.</red>");
            return;
        }

        if (target.getName() == null && !target.hasPlayedBefore() && !target.isOnline()) {
            VaultText.send(player, "<red>Player '" + args[0] + "' was not found.</red>");
            return;
        }

        EconomyResponse withdrawResp = eco.withdrawPlayer(player, amount);
        if (!withdrawResp.transactionSuccess()) {
            VaultText.send(player, "<red>Transaction failed: " + withdrawResp.errorMessage + "</red>");
            return;
        }

        EconomyResponse depositResp = eco.depositPlayer(target, amount);
        if (!depositResp.transactionSuccess()) {
            // Refund sender in the rare event of deposit failure
            eco.depositPlayer(player, amount);
            VaultText.send(player, "<red>Payment failed: " + depositResp.errorMessage + ". Refunded.</red>");
            return;
        }

        String formatted = eco.format(amount);
        VaultText.send(player, "<green>You sent <bold>" + formatted + "</bold> to <yellow>" + target.getName() + "</yellow>.</green>");

        if (target.isOnline() && target.getPlayer() != null) {
            VaultText.send(target.getPlayer(), "<green>You received <bold>" + formatted + "</bold> from <yellow>"
                    + player.getName() + "</yellow>.</green>");
        }
    }

    @Override
    public @Nullable String permission() {
        return "vault.economy.pay";
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> names = new ArrayList<>();
            CommandSender sender = stack.getSender();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(sender)) continue;
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        if (args.length == 2) {
            return List.of("10", "50", "100", "500");
        }
        return Collections.emptyList();
    }
}
