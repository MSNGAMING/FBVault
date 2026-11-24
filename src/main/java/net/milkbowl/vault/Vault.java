package net.milkbowl.vault;

import java.util.logging.Logger;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.command.VaultCommand;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.builtin.BuiltinEconomy;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;
import net.milkbowl.vault.util.FoliaScheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Modern 1:1 drop-in replacement for the legacy Vault plugin.
 * Built as a Native Paper Plugin for high-performance Paper, Purpur, and Folia servers.
 */
public class Vault extends JavaPlugin implements Listener {

    private static Vault instance;
    private static Logger log;

    private ServicesManager servicesManager;
    private VaultCommand commandHandler;
    private BuiltinEconomy builtinEconomy;

    @Override
    public void onEnable() {
        instance = this;
        log = getLogger();
        this.servicesManager = getServer().getServicesManager();
        this.commandHandler = new VaultCommand();

        saveDefaultConfig();

        // 1. Register SuperPerms as default fallback permission system
        Permission superPerms = new Permission_SuperPerms(this);
        this.servicesManager.register(Permission.class, superPerms, this, ServicePriority.Lowest);
        log.info("[Permission] SuperPermissions registered as base permission provider.");

        // 2. Register Built-in Economy if enabled
        if (getConfig().getBoolean("builtin-economy.enabled", true)) {
            this.builtinEconomy = new BuiltinEconomy(this);
            this.builtinEconomy.load();
            this.servicesManager.register(Economy.class, this.builtinEconomy, this, ServicePriority.Low);
            long interval = getConfig().getLong("builtin-economy.save-interval-seconds", 60L);
            this.builtinEconomy.startAutoSave(interval);
            log.info("[Economy] Built-in Vault Economy enabled and registered (ServicePriority.Low).");
        }

        // 3. Register Service Lifecycle Listener
        getServer().getPluginManager().registerEvents(this, this);

        // 4. Startup Banner & Platform Details
        String platform = FoliaScheduler.isFolia() ? "Folia (Regionized Multithreading)" : "Paper / Purpur";
        log.info(String.format("Vault v%s enabled successfully as a Native Paper Plugin on %s.",
                getDescription().getVersion(), platform));
    }

    @Override
    public void onDisable() {
        // Flush any dirty economy balances to disk synchronously before shutdown
        if (this.builtinEconomy != null) {
            this.builtinEconomy.saveSync();
        }

        // Unregister all services registered by this plugin
        if (this.servicesManager != null) {
            this.servicesManager.unregisterAll(this);
        }

        // Cleanly cancel all scheduled tasks across Folia and legacy schedulers
        FoliaScheduler.cancelTasks(this);

        log.info(String.format("Vault v%s disabled.", getDescription().getVersion()));
        instance = null;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (this.builtinEconomy != null) {
            this.builtinEconomy.reloadSettings();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (this.commandHandler != null) {
            return this.commandHandler.onLegacyCommand(sender, label, args);
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (!getConfig().getBoolean("log-service-changes", true)) {
            return;
        }
        RegisteredServiceProvider<?> provider = event.getProvider();
        Class<?> service = provider.getService();
        if (service == Economy.class || service == Permission.class || service == Chat.class) {
            log.info(String.format("[%s] Registered provider: %s (Plugin: %s, Priority: %s)",
                    service.getSimpleName(),
                    provider.getProvider().getClass().getSimpleName(),
                    provider.getPlugin().getName(),
                    provider.getPriority()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServiceUnregister(ServiceUnregisterEvent event) {
        if (!getConfig().getBoolean("log-service-changes", true)) {
            return;
        }
        RegisteredServiceProvider<?> provider = event.getProvider();
        Class<?> service = provider.getService();
        if (service == Economy.class || service == Permission.class || service == Chat.class) {
            log.info(String.format("[%s] Unregistered provider: %s (Plugin: %s)",
                    service.getSimpleName(),
                    provider.getProvider().getClass().getSimpleName(),
                    provider.getPlugin().getName()));
        }
    }

    /**
     * Gets the singleton instance of the Vault plugin.
     *
     * @return active Vault instance
     */
    public static Vault getInstance() {
        return instance;
    }

    /**
     * Gets the active Built-in Economy instance, or null if disabled.
     *
     * @return builtin economy instance
     */
    public BuiltinEconomy getBuiltinEconomy() {
        return this.builtinEconomy;
    }

    /**
     * Legacy update check placeholder. Retained for binary compatibility with legacy hooks.
     *
     * @param currentVersion current version numeric
     * @return currentVersion
     */
    public double updateCheck(double currentVersion) {
        return currentVersion;
    }
}
