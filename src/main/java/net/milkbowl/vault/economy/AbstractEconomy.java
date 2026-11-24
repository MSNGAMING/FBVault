package net.milkbowl.vault.economy;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Modern abstract implementation of {@link Economy}.
 * <p>
 * Seamlessly bridges modern UUID-backed {@link OfflinePlayer} operations and legacy
 * String-based player name methods using {@link Bukkit#getOfflinePlayer(String)} while
 * providing built-in recursion protection to prevent stack overflows with legacy or incomplete implementations.
 */
@SuppressWarnings("deprecation")
public abstract class AbstractEconomy implements Economy {

    private static final ThreadLocal<Boolean> RECURSION_GUARD = ThreadLocal.withInitial(() -> Boolean.FALSE);

    // =========================================================================
    // Account Check Operations
    // =========================================================================

    @Override
    public boolean hasAccount(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return hasAccount(Bukkit.getOfflinePlayer(playerName.trim()));
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        if (player == null) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return hasAccount(name);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return hasAccount(Bukkit.getOfflinePlayer(playerName.trim()), worldName);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        if (player == null) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return hasAccount(name, worldName);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    // =========================================================================
    // Balance Operations
    // =========================================================================

    @Override
    public double getBalance(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return 0.0;
        }
        if (RECURSION_GUARD.get()) {
            return 0.0;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return getBalance(Bukkit.getOfflinePlayer(playerName.trim()));
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (player == null) {
            return 0.0;
        }
        if (RECURSION_GUARD.get()) {
            return 0.0;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return 0.0;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return getBalance(name);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public double getBalance(String playerName, String world) {
        if (playerName == null || playerName.isBlank()) {
            return 0.0;
        }
        if (RECURSION_GUARD.get()) {
            return 0.0;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return getBalance(Bukkit.getOfflinePlayer(playerName.trim()), world);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        if (player == null) {
            return 0.0;
        }
        if (RECURSION_GUARD.get()) {
            return 0.0;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return 0.0;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return getBalance(name, world);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    // =========================================================================
    // Fund Check Operations
    // =========================================================================

    @Override
    public boolean has(String playerName, double amount) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return has(Bukkit.getOfflinePlayer(playerName.trim()), amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        if (player == null) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return has(name, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return has(Bukkit.getOfflinePlayer(playerName.trim()), worldName, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        if (player == null) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return has(name, worldName, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    // =========================================================================
    // Withdraw Operations
    // =========================================================================

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name cannot be null or empty.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return withdrawPlayer(Bukkit.getOfflinePlayer(playerName.trim()), amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name could not be resolved.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return withdrawPlayer(name, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name cannot be null or empty.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return withdrawPlayer(Bukkit.getOfflinePlayer(playerName.trim()), worldName, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        if (player == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name could not be resolved.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return withdrawPlayer(name, worldName, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    // =========================================================================
    // Deposit Operations
    // =========================================================================

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name cannot be null or empty.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return depositPlayer(Bukkit.getOfflinePlayer(playerName.trim()), amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name could not be resolved.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return depositPlayer(name, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name cannot be null or empty.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return depositPlayer(Bukkit.getOfflinePlayer(playerName.trim()), worldName, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        if (player == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name could not be resolved.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return depositPlayer(name, worldName, amount);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    // =========================================================================
    // Bank Operations
    // =========================================================================

    @Override
    public EconomyResponse createBank(String name, String player) {
        if (player == null || player.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name cannot be null or empty.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return createBank(name, Bukkit.getOfflinePlayer(player.trim()));
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        if (player == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        String playerName = player.getName();
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name could not be resolved.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return createBank(name, playerName);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name cannot be null or empty.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return isBankOwner(name, Bukkit.getOfflinePlayer(playerName.trim()));
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        if (player == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        String playerName = player.getName();
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name could not be resolved.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return isBankOwner(name, playerName);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name cannot be null or empty.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return isBankMember(name, Bukkit.getOfflinePlayer(playerName.trim()));
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        if (player == null) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player cannot be null.");
        }
        if (RECURSION_GUARD.get()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Method not implemented.");
        }
        String playerName = player.getName();
        if (playerName == null || playerName.isBlank()) {
            return new EconomyResponse(0.0, 0.0, ResponseType.FAILURE, "Player name could not be resolved.");
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return isBankMember(name, playerName);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    // =========================================================================
    // Account Creation Operations
    // =========================================================================

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return createPlayerAccount(Bukkit.getOfflinePlayer(playerName.trim()));
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (player == null) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return createPlayerAccount(name);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return createPlayerAccount(Bukkit.getOfflinePlayer(playerName.trim()), worldName);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        if (player == null) {
            return false;
        }
        if (RECURSION_GUARD.get()) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        RECURSION_GUARD.set(Boolean.TRUE);
        try {
            return createPlayerAccount(name, worldName);
        } finally {
            RECURSION_GUARD.set(Boolean.FALSE);
        }
    }
}
