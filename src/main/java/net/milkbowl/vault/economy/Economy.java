package net.milkbowl.vault.economy;

import java.util.List;
import org.bukkit.OfflinePlayer;

/**
 * The main economy API for Vault.
 * Provides unified abstractions for balances, transactions, and bank operations.
 */
public interface Economy {

    /**
     * Checks if the economy implementation is enabled.
     *
     * @return true if enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Gets the name of the economy provider.
     *
     * @return provider name
     */
    String getName();

    /**
     * Returns true if the given implementation supports bank accounts.
     *
     * @return true if banks are supported
     */
    boolean hasBankSupport();

    /**
     * Returns the number of digits after the decimal point kept by this economy,
     * or -1 if no rounding occurs.
     *
     * @return number of fractional digits kept
     */
    int fractionalDigits();

    /**
     * Formats the given amount into a human-readable string using the economy's
     * formatting rules and currency symbol/name.
     *
     * @param amount amount to format
     * @return formatted amount string
     */
    String format(double amount);

    /**
     * Returns the name of the currency in plural form.
     *
     * @return plural currency name, or an empty string if unsupported
     */
    String currencyNamePlural();

    /**
     * Returns the name of the currency in singular form.
     *
     * @return singular currency name, or an empty string if unsupported
     */
    String currencyNameSingular();

    /**
     * Checks if this player has an account.
     *
     * @param playerName player name to check
     * @return true if an account exists
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)} instead.
     */
    @Deprecated
    boolean hasAccount(String playerName);

    /**
     * Checks if this player has an account.
     *
     * @param player player to check
     * @return true if an account exists
     */
    boolean hasAccount(OfflinePlayer player);

    /**
     * Checks if this player has an account on the specified world.
     *
     * @param playerName player name to check
     * @param worldName  world name
     * @return true if an account exists
     * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer, String)} instead.
     */
    @Deprecated
    boolean hasAccount(String playerName, String worldName);

    /**
     * Checks if this player has an account on the specified world.
     *
     * @param player    player to check
     * @param worldName world name
     * @return true if an account exists
     */
    boolean hasAccount(OfflinePlayer player, String worldName);

    /**
     * Gets the balance of the player.
     *
     * @param playerName player name
     * @return balance
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)} instead.
     */
    @Deprecated
    double getBalance(String playerName);

    /**
     * Gets the balance of the player.
     *
     * @param player player
     * @return balance
     */
    double getBalance(OfflinePlayer player);

    /**
     * Gets the balance of the player on the specified world.
     *
     * @param playerName player name
     * @param world      world name
     * @return balance
     * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)} instead.
     */
    @Deprecated
    double getBalance(String playerName, String world);

    /**
     * Gets the balance of the player on the specified world.
     *
     * @param player player
     * @param world  world name
     * @return balance
     */
    double getBalance(OfflinePlayer player, String world);

    /**
     * Checks if the player has at least the specified amount.
     *
     * @param playerName player name
     * @param amount     amount to check
     * @return true if the player has sufficient funds
     * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)} instead.
     */
    @Deprecated
    boolean has(String playerName, double amount);

    /**
     * Checks if the player has at least the specified amount.
     *
     * @param player player
     * @param amount amount to check
     * @return true if the player has sufficient funds
     */
    boolean has(OfflinePlayer player, double amount);

    /**
     * Checks if the player has at least the specified amount on the given world.
     *
     * @param playerName player name
     * @param worldName  world name
     * @param amount     amount to check
     * @return true if the player has sufficient funds
     * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, String, double)} instead.
     */
    @Deprecated
    boolean has(String playerName, String worldName, double amount);

    /**
     * Checks if the player has at least the specified amount on the given world.
     *
     * @param player    player
     * @param worldName world name
     * @param amount    amount to check
     * @return true if the player has sufficient funds
     */
    boolean has(OfflinePlayer player, String worldName, double amount);

    /**
     * Withdraws the specified amount from the player's account.
     *
     * @param playerName player name
     * @param amount     amount to withdraw
     * @return EconomyResponse detailing transaction outcome
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, double)} instead.
     */
    @Deprecated
    EconomyResponse withdrawPlayer(String playerName, double amount);

    /**
     * Withdraws the specified amount from the player's account.
     *
     * @param player player
     * @param amount amount to withdraw
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse withdrawPlayer(OfflinePlayer player, double amount);

    /**
     * Withdraws the specified amount from the player's account on the given world.
     *
     * @param playerName player name
     * @param worldName  world name
     * @param amount     amount to withdraw
     * @return EconomyResponse detailing transaction outcome
     * @deprecated As of VaultAPI 1.4 use {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
     */
    @Deprecated
    EconomyResponse withdrawPlayer(String playerName, String worldName, double amount);

    /**
     * Withdraws the specified amount from the player's account on the given world.
     *
     * @param player    player
     * @param worldName world name
     * @param amount    amount to withdraw
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount);

    /**
     * Deposits the specified amount into the player's account.
     *
     * @param playerName player name
     * @param amount     amount to deposit
     * @return EconomyResponse detailing transaction outcome
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, double)} instead.
     */
    @Deprecated
    EconomyResponse depositPlayer(String playerName, double amount);

    /**
     * Deposits the specified amount into the player's account.
     *
     * @param player player
     * @param amount amount to deposit
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse depositPlayer(OfflinePlayer player, double amount);

    /**
     * Deposits the specified amount into the player's account on the given world.
     *
     * @param playerName player name
     * @param worldName  world name
     * @param amount     amount to deposit
     * @return EconomyResponse detailing transaction outcome
     * @deprecated As of VaultAPI 1.4 use {@link #depositPlayer(OfflinePlayer, String, double)} instead.
     */
    @Deprecated
    EconomyResponse depositPlayer(String playerName, String worldName, double amount);

    /**
     * Deposits the specified amount into the player's account on the given world.
     *
     * @param player    player
     * @param worldName world name
     * @param amount    amount to deposit
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount);

    /**
     * Creates a bank account with the given owner.
     *
     * @param name   bank name
     * @param player owner name
     * @return EconomyResponse detailing transaction outcome
     * @deprecated As of VaultAPI 1.4 use {@link #createBank(String, OfflinePlayer)} instead.
     */
    @Deprecated
    EconomyResponse createBank(String name, String player);

    /**
     * Creates a bank account with the given owner.
     *
     * @param name   bank name
     * @param player owner
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse createBank(String name, OfflinePlayer player);

    /**
     * Deletes a bank account with the specified name.
     *
     * @param name bank name
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse deleteBank(String name);

    /**
     * Returns the balance of the specified bank.
     *
     * @param name bank name
     * @return EconomyResponse detailing balance
     */
    EconomyResponse bankBalance(String name);

    /**
     * Checks if the bank account has at least the specified amount.
     *
     * @param name   bank name
     * @param amount amount to check
     * @return EconomyResponse detailing outcome
     */
    EconomyResponse bankHas(String name, double amount);

    /**
     * Withdraws the specified amount from the bank account.
     *
     * @param name   bank name
     * @param amount amount to withdraw
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse bankWithdraw(String name, double amount);

    /**
     * Deposits the specified amount into the bank account.
     *
     * @param name   bank name
     * @param amount amount to deposit
     * @return EconomyResponse detailing transaction outcome
     */
    EconomyResponse bankDeposit(String name, double amount);

    /**
     * Checks if the player is an owner of the bank.
     *
     * @param name       bank name
     * @param playerName player name
     * @return EconomyResponse detailing ownership
     * @deprecated As of VaultAPI 1.4 use {@link #isBankOwner(String, OfflinePlayer)} instead.
     */
    @Deprecated
    EconomyResponse isBankOwner(String name, String playerName);

    /**
     * Checks if the player is an owner of the bank.
     *
     * @param name   bank name
     * @param player player
     * @return EconomyResponse detailing ownership
     */
    EconomyResponse isBankOwner(String name, OfflinePlayer player);

    /**
     * Checks if the player is a member of the bank.
     *
     * @param name       bank name
     * @param playerName player name
     * @return EconomyResponse detailing membership
     * @deprecated As of VaultAPI 1.4 use {@link #isBankMember(String, OfflinePlayer)} instead.
     */
    @Deprecated
    EconomyResponse isBankMember(String name, String playerName);

    /**
     * Checks if the player is a member of the bank.
     *
     * @param name   bank name
     * @param player player
     * @return EconomyResponse detailing membership
     */
    EconomyResponse isBankMember(String name, OfflinePlayer player);

    /**
     * Returns an unmodifiable list of all known bank names.
     *
     * @return list of bank names
     */
    List<String> getBanks();

    /**
     * Creates an account for the given player name.
     *
     * @param playerName player name
     * @return true if account creation succeeded
     * @deprecated As of VaultAPI 1.4 use {@link #createPlayerAccount(OfflinePlayer)} instead.
     */
    @Deprecated
    boolean createPlayerAccount(String playerName);

    /**
     * Creates an account for the given player.
     *
     * @param player player
     * @return true if account creation succeeded
     */
    boolean createPlayerAccount(OfflinePlayer player);

    /**
     * Creates an account for the given player name on the specified world.
     *
     * @param playerName player name
     * @param worldName  world name
     * @return true if account creation succeeded
     * @deprecated As of VaultAPI 1.4 use {@link #createPlayerAccount(OfflinePlayer, String)} instead.
     */
    @Deprecated
    boolean createPlayerAccount(String playerName, String worldName);

    /**
     * Creates an account for the given player on the specified world.
     *
     * @param player    player
     * @param worldName world name
     * @return true if account creation succeeded
     */
    boolean createPlayerAccount(OfflinePlayer player, String worldName);
}
