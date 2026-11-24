package net.milkbowl.vault.economy.builtin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import net.milkbowl.vault.util.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

/**
 * High-performance, zero-dependency, Folia-safe built-in Economy provider.
 * <p>
 * Provides O(1) in-memory balance lookups and lock-free atomic transaction operations
 * backed by asynchronous JSON persistence.
 */
public class BuiltinEconomy extends AbstractEconomy {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final File dataFolder;
    private final Logger logger;
    private final @Nullable Vault plugin;

    private final ConcurrentHashMap<UUID, Double> balances = new ConcurrentHashMap<>(64, 0.75f, 4);
    private final ConcurrentHashMap<String, UUID> nameToUuid = new ConcurrentHashMap<>(64, 0.75f, 4);
    private final AtomicBoolean dirty = new AtomicBoolean(false);

    private double startingBalance = 100.0;
    private String currencySymbol = "$";
    private String currencySingular = "Dollar";
    private String currencyPlural = "Dollars";
    private int fractionalDigits = 2;
    private boolean autoCreate = true;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public BuiltinEconomy(Vault plugin) {
        this(plugin.getDataFolder(), plugin.getLogger(), plugin);
        reloadSettings();
    }

    public BuiltinEconomy(File dataFolder, Logger logger, @Nullable Vault plugin) {
        this.dataFolder = dataFolder;
        this.logger = logger != null ? logger : Logger.getLogger("BuiltinEconomy");
        this.plugin = plugin;
    }

    public void configure(
            String currencySymbol,
            String currencySingular,
            String currencyPlural,
            double startingBalance,
            int fractionalDigits,
            boolean autoCreate
    ) {
        this.currencySymbol = currencySymbol;
        this.currencySingular = currencySingular;
        this.currencyPlural = currencyPlural;
        this.startingBalance = startingBalance;
        this.fractionalDigits = fractionalDigits;
        this.autoCreate = autoCreate;

        StringBuilder pattern = new StringBuilder("#,##0");
        if (fractionalDigits > 0) {
            pattern.append(".").append("0".repeat(fractionalDigits));
        }
        this.decimalFormat = new DecimalFormat(pattern.toString());
    }

    public void reloadSettings() {
        if (plugin != null) {
            configure(
                    plugin.getConfig().getString("builtin-economy.currency-symbol", "$"),
                    plugin.getConfig().getString("builtin-economy.currency-name-singular", "Dollar"),
                    plugin.getConfig().getString("builtin-economy.currency-name-plural", "Dollars"),
                    plugin.getConfig().getDouble("builtin-economy.starting-balance", 100.0),
                    plugin.getConfig().getInt("builtin-economy.fractional-digits", 2),
                    plugin.getConfig().getBoolean("builtin-economy.auto-create-accounts", true)
            );
        }
    }

    // =========================================================================
    // Core Economy Properties
    // =========================================================================

    @Override
    public boolean isEnabled() {
        return plugin == null || plugin.isEnabled();
    }

    @Override
    public String getName() {
        return "VaultBuiltinEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return this.fractionalDigits;
    }

    @Override
    public String format(double amount) {
        return this.currencySymbol + this.decimalFormat.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return this.currencyPlural;
    }

    @Override
    public String currencyNameSingular() {
        return this.currencySingular;
    }

    // =========================================================================
    // Account Checks & Creation
    // =========================================================================

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        if (player == null) {
            return false;
        }
        rememberPlayer(player);
        if (balances.containsKey(player.getUniqueId())) {
            return true;
        }
        return autoCreate;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (player == null) {
            return false;
        }
        rememberPlayer(player);
        UUID uuid = player.getUniqueId();
        if (balances.putIfAbsent(uuid, this.startingBalance) == null) {
            dirty.set(true);
            return true;
        }
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    // =========================================================================
    // Balance Operations
    // =========================================================================

    @Override
    public double getBalance(OfflinePlayer player) {
        if (player == null) {
            return 0.0;
        }
        rememberPlayer(player);
        UUID uuid = player.getUniqueId();
        Double bal = balances.get(uuid);
        if (bal != null) {
            return bal;
        }
        if (autoCreate) {
            createPlayerAccount(player);
            return this.startingBalance;
        }
        return 0.0;
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        if (player == null || amount < 0) {
            return false;
        }
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    // =========================================================================
    // Atomic Transactions
    // =========================================================================

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(amount, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0.0, getBalance(player), ResponseType.FAILURE, "Cannot withdraw negative amount.");
        }
        rememberPlayer(player);
        UUID uuid = player.getUniqueId();

        final double[] balanceTracker = new double[2]; // [0] = oldBalance, [1] = newBalance
        final boolean[] success = new boolean[1];

        balances.compute(uuid, (key, current) -> {
            double cur = (current == null ? (autoCreate ? startingBalance : 0.0) : current);
            balanceTracker[0] = cur;
            if (cur >= amount) {
                double updated = round(cur - amount);
                balanceTracker[1] = updated;
                success[0] = true;
                return updated;
            } else {
                balanceTracker[1] = cur;
                success[0] = false;
                return cur;
            }
        });

        if (!success[0]) {
            return new EconomyResponse(0.0, balanceTracker[0], ResponseType.FAILURE, "Insufficient funds.");
        }

        dirty.set(true);
        return new EconomyResponse(amount, balanceTracker[1], ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(amount, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0.0, getBalance(player), ResponseType.FAILURE, "Cannot deposit negative amount.");
        }
        rememberPlayer(player);
        UUID uuid = player.getUniqueId();

        double newBalance = balances.compute(uuid, (key, current) -> {
            double cur = (current == null ? (autoCreate ? startingBalance : 0.0) : current);
            return round(cur + amount);
        });

        dirty.set(true);
        return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    public EconomyResponse setBalance(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(amount, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (amount < 0) {
            return new EconomyResponse(0.0, getBalance(player), ResponseType.FAILURE, "Balance cannot be negative.");
        }
        rememberPlayer(player);
        UUID uuid = player.getUniqueId();
        double rounded = round(amount);
        balances.put(uuid, rounded);
        dirty.set(true);
        return new EconomyResponse(amount, rounded, ResponseType.SUCCESS, null);
    }

    // =========================================================================
    // Bank Operations (Unsupported by Default)
    // =========================================================================

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks not supported.");
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    // =========================================================================
    // Persistence & Utilities
    // =========================================================================

    public void rememberPlayer(OfflinePlayer player) {
        if (player == null) return;
        String name = player.getName();
        if (name != null && !name.isBlank()) {
            nameToUuid.put(name.toLowerCase(Locale.ROOT), player.getUniqueId());
        }
    }

    public UUID getUuidByName(String name) {
        if (name == null || name.isBlank()) return null;
        UUID cached = nameToUuid.get(name.toLowerCase(Locale.ROOT));
        if (cached != null) return cached;
        OfflinePlayer op = Bukkit.getOfflinePlayer(name.trim());
        if (op.hasPlayedBefore() || op.isOnline() || op.getName() != null) {
            rememberPlayer(op);
            return op.getUniqueId();
        }
        return null;
    }

    private double round(double value) {
        if (fractionalDigits <= 0) {
            return Math.floor(value);
        }
        double factor = Math.pow(10, fractionalDigits);
        return Math.round(value * factor) / factor;
    }

    public synchronized void load() {
        File file = new File(dataFolder, "balances.json");
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("accounts")) {
                JsonObject accounts = root.getAsJsonObject("accounts");
                for (String key : accounts.keySet()) {
                    try {
                        UUID uuid = UUID.fromString(key);
                        JsonObject userObj = accounts.getAsJsonObject(key);
                        double balance = userObj.get("balance").getAsDouble();
                        balances.put(uuid, balance);
                        if (userObj.has("name")) {
                            String name = userObj.get("name").getAsString();
                            nameToUuid.put(name.toLowerCase(Locale.ROOT), uuid);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            logger.info("Loaded " + balances.size() + " accounts into Built-in Vault Economy.");
        } catch (Exception ex) {
            logger.severe("Failed to load balances.json: " + ex.getMessage());
        }
    }

    public synchronized void saveSync() {
        if (!dirty.getAndSet(false)) {
            return;
        }

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File file = new File(dataFolder, "balances.json");
        File tempFile = new File(dataFolder, "balances.json.tmp");

        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        JsonObject accounts = new JsonObject();

        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            JsonObject userObj = new JsonObject();
            userObj.addProperty("balance", entry.getValue());
            accounts.add(entry.getKey().toString(), userObj);
        }
        root.add("accounts", accounts);

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                GSON.toJson(root, writer);
                writer.flush();
            }
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            logger.severe("Failed to save balances.json: " + ex.getMessage());
            dirty.set(true); // retry next time
        }
    }

    public void startAutoSave(long intervalSeconds) {
        if (plugin == null) return;
        long ticks = Math.max(10, intervalSeconds) * 20L;
        FoliaScheduler.runAsyncTimer(plugin, task -> {
            if (dirty.get()) {
                saveSync();
            }
        }, ticks, ticks);
    }
}
