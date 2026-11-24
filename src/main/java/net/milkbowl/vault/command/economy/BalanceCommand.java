package net.milkbowl.vault.command.economy;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.builtin.BuiltinEconomy;
import net.milkbowl.vault.util.VaultText;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BalanceCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        CommandSender sender = stack.getSender();
        Vault plugin = Vault.getInstance();
        if (plugin == null) return;

        BuiltinEconomy eco = plugin.getBuiltinEconomy();
        if (eco == null) {
            VaultText.send(sender, "<red>Built-in economy is currently disabled.</red>");
            return;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                VaultText.send(sender, "<red>Console must specify a player: /balance <player></red>");
                return;
            }
            double bal = eco.getBalance(player);
            VaultText.send(sender, "<gray>Your balance: <green><bold>" + eco.format(bal) + "</bold></green></gray>");
            return;
        }

        if (!sender.hasPermission("vault.economy.balance.others")) {
            VaultText.send(sender, "<red>You do not have permission to check others' balance.</red>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getName() == null && !target.hasPlayedBefore() && !target.isOnline()) {
            VaultText.send(sender, "<red>Player '" + args[0] + "' was not found.</red>");
            return;
        }

        double bal = eco.getBalance(target);
        VaultText.send(sender, "<gray>Balance of <yellow>" + target.getName() + "</yellow>: <green><bold>"
                + eco.format(bal) + "</bold></green></gray>");
    }

    @Override
    public @Nullable String permission() {
        return "vault.economy.balance";
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1 && stack.getSender().hasPermission("vault.economy.balance.others")) {
            String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return Collections.emptyList();
    }
}
