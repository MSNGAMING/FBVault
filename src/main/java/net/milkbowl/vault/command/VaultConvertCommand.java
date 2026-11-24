package net.milkbowl.vault.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.milkbowl.vault.util.VaultText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Modern Paper native BasicCommand for /vault-convert.
 */
public class VaultConvertCommand implements BasicCommand {

    private final VaultCommand parent;

    public VaultConvertCommand(VaultCommand parent) {
        this.parent = parent;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("vault.admin")) {
            VaultText.send(stack.getSender(), "<red>You do not have permission to execute this command (vault.admin).</red>");
            return;
        }

        if (args.length < 2) {
            VaultText.send(stack.getSender(), "<yellow>Usage: <gray>/vault-convert <fromEconomy> <toEconomy></gray></yellow>");
            return;
        }

        parent.executeConvert(stack.getSender(), args[0], args[1]);
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

        if (args.length == 1) {
            return parent.matchEconomies(args[0]);
        }

        if (args.length == 2) {
            return parent.matchEconomies(args[1]);
        }

        return Collections.emptyList();
    }
}
