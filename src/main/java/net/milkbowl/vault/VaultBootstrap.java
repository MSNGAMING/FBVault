package net.milkbowl.vault;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.List;
import net.milkbowl.vault.command.VaultCommand;
import net.milkbowl.vault.command.VaultConvertCommand;
import net.milkbowl.vault.command.economy.BalanceCommand;
import net.milkbowl.vault.command.economy.EcoCommand;
import net.milkbowl.vault.command.economy.PayCommand;
import org.jetbrains.annotations.NotNull;

/**
 * Native Paper plugin bootstrapper for Vault.
 * Initializes the plugin during Paper's bootstrap phase and registers Brigadier commands
 * using Paper's modern Lifecycle API.
 */
public class VaultBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        VaultCommand vaultCommand = new VaultCommand();
        VaultConvertCommand convertCommand = new VaultConvertCommand(vaultCommand);
        BalanceCommand balanceCommand = new BalanceCommand();
        PayCommand payCommand = new PayCommand();
        EcoCommand ecoCommand = new EcoCommand();

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registrar = event.registrar();

            registrar.register(
                    "vault",
                    "Vault diagnostic and administrative commands",
                    List.of("vault-info"),
                    vaultCommand
            );

            registrar.register(
                    "vault-convert",
                    "Converts data from economy1 to economy2 asynchronously",
                    List.of(),
                    convertCommand
            );

            registrar.register(
                    "balance",
                    "Checks your account balance",
                    List.of("bal", "money"),
                    balanceCommand
            );

            registrar.register(
                    "pay",
                    "Pays money to another player",
                    List.of(),
                    payCommand
            );

            registrar.register(
                    "eco",
                    "Economy administrative management",
                    List.of("economy"),
                    ecoCommand
            );
        });
    }
}
