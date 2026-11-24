package net.milkbowl.vault.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractEconomyTest {

    private static Server mockServer;
    private static final Map<String, OfflinePlayer> offlinePlayerCache = new HashMap<>();

    @BeforeAll
    static void setUpBukkit() throws Exception {
        mockServer = mock(Server.class);
        when(mockServer.getOfflinePlayer(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return offlinePlayerCache.computeIfAbsent(name, n -> {
                OfflinePlayer op = mock(OfflinePlayer.class);
                when(op.getName()).thenReturn(n);
                when(op.getUniqueId()).thenReturn(UUID.nameUUIDFromBytes(n.getBytes()));
                return op;
            });
        });

        // Inject mockServer into Bukkit.server static field
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, mockServer);
    }

    @AfterAll
    static void tearDownBukkit() throws Exception {
        Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, null);
    }

    @BeforeEach
    void clearCache() {
        offlinePlayerCache.clear();
    }

    /**
     * Modern Economy implementation implementing only OfflinePlayer methods.
     */
    static class ModernUUIDEconomy extends AbstractEconomy {
        private final Map<UUID, Double> balances = new HashMap<>();

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public String getName() {
            return "ModernUUIDEconomy";
        }

        @Override
        public boolean hasBankSupport() {
            return false;
        }

        @Override
        public int fractionalDigits() {
            return 2;
        }

        @Override
        public String format(double amount) {
            return String.format("$%.2f", amount);
        }

        @Override
        public String currencyNamePlural() {
            return "Dollars";
        }

        @Override
        public String currencyNameSingular() {
            return "Dollar";
        }

        @Override
        public boolean hasAccount(OfflinePlayer player) {
            return player != null && balances.containsKey(player.getUniqueId());
        }

        @Override
        public boolean hasAccount(OfflinePlayer player, String worldName) {
            return hasAccount(player);
        }

        @Override
        public double getBalance(OfflinePlayer player) {
            return player == null ? 0.0 : balances.getOrDefault(player.getUniqueId(), 0.0);
        }

        @Override
        public double getBalance(OfflinePlayer player, String world) {
            return getBalance(player);
        }

        @Override
        public boolean has(OfflinePlayer player, double amount) {
            return getBalance(player) >= amount;
        }

        @Override
        public boolean has(OfflinePlayer player, String worldName, double amount) {
            return has(player, amount);
        }

        @Override
        public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
            if (player == null) {
                return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Player null");
            }
            double bal = getBalance(player);
            if (bal < amount) {
                return new EconomyResponse(0.0, bal, EconomyResponse.ResponseType.FAILURE, "Insufficient funds");
            }
            double newBal = bal - amount;
            balances.put(player.getUniqueId(), newBal);
            return new EconomyResponse(amount, newBal, EconomyResponse.ResponseType.SUCCESS, null);
        }

        @Override
        public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
            return withdrawPlayer(player, amount);
        }

        @Override
        public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
            if (player == null) {
                return new EconomyResponse(amount, 0.0, EconomyResponse.ResponseType.FAILURE, "Player null");
            }
            double newBal = getBalance(player) + amount;
            balances.put(player.getUniqueId(), newBal);
            return new EconomyResponse(amount, newBal, EconomyResponse.ResponseType.SUCCESS, null);
        }

        @Override
        public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
            return depositPlayer(player, amount);
        }

        @Override
        public boolean createPlayerAccount(OfflinePlayer player) {
            if (player == null) return false;
            balances.putIfAbsent(player.getUniqueId(), 0.0);
            return true;
        }

        @Override
        public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
            return createPlayerAccount(player);
        }

        @Override
        public EconomyResponse createBank(String name, OfflinePlayer player) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public EconomyResponse deleteBank(String name) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public EconomyResponse bankBalance(String name) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public EconomyResponse bankHas(String name, double amount) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public EconomyResponse bankWithdraw(String name, double amount) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public EconomyResponse bankDeposit(String name, double amount) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public EconomyResponse isBankMember(String name, OfflinePlayer player) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "No banks");
        }

        @Override
        public List<String> getBanks() {
            return List.of();
        }
    }

    /**
     * Completely empty implementation overriding neither String nor OfflinePlayer methods
     * to test recursion protection.
     */
    static class BareEconomy extends AbstractEconomy {
        @Override
        public boolean isEnabled() { return true; }
        @Override
        public String getName() { return "Bare"; }
        @Override
        public boolean hasBankSupport() { return false; }
        @Override
        public int fractionalDigits() { return 0; }
        @Override
        public String format(double amount) { return ""; }
        @Override
        public String currencyNamePlural() { return ""; }
        @Override
        public String currencyNameSingular() { return ""; }
        @Override
        public EconomyResponse deleteBank(String name) { return null; }
        @Override
        public EconomyResponse bankBalance(String name) { return null; }
        @Override
        public EconomyResponse bankHas(String name, double amount) { return null; }
        @Override
        public EconomyResponse bankWithdraw(String name, double amount) { return null; }
        @Override
        public EconomyResponse bankDeposit(String name, double amount) { return null; }
        @Override
        public List<String> getBanks() { return List.of(); }
    }

    @Test
    void testLegacyStringMethodDelegatesSafelyToModernUUID() {
        ModernUUIDEconomy economy = new ModernUUIDEconomy();

        // 1. Create account using legacy String method
        assertTrue(economy.createPlayerAccount("Steve"));
        assertTrue(economy.hasAccount("Steve"));

        // 2. Deposit using legacy String method
        EconomyResponse dep = economy.depositPlayer("Steve", 150.0);
        assertTrue(dep.transactionSuccess());
        assertEquals(150.0, dep.balance, 0.001);

        // 3. Balance check using legacy String method
        assertEquals(150.0, economy.getBalance("Steve"), 0.001);
        assertTrue(economy.has("Steve", 100.0));
        assertFalse(economy.has("Steve", 200.0));

        // 4. Withdraw using legacy String method
        EconomyResponse with = economy.withdrawPlayer("Steve", 50.0);
        assertTrue(with.transactionSuccess());
        assertEquals(100.0, with.balance, 0.001);
        assertEquals(100.0, economy.getBalance("Steve"), 0.001);

        // 5. Query through modern OfflinePlayer directly - state should match exactly!
        OfflinePlayer op = Bukkit.getOfflinePlayer("Steve");
        assertEquals(100.0, economy.getBalance(op), 0.001);
    }

    @Test
    void testNullAndBlankStringSafety() {
        ModernUUIDEconomy economy = new ModernUUIDEconomy();

        // Must not throw NullPointerException or IllegalArgumentException
        assertFalse(economy.hasAccount((String) null));
        assertFalse(economy.hasAccount(""));
        assertFalse(economy.hasAccount("   "));

        assertEquals(0.0, economy.getBalance((String) null));
        assertEquals(0.0, economy.getBalance(""));
        assertEquals(0.0, economy.getBalance("   "));

        assertFalse(economy.has((String) null, 50.0));
        assertFalse(economy.has("", 50.0));

        EconomyResponse depNull = economy.depositPlayer((String) null, 50.0);
        assertFalse(depNull.transactionSuccess());

        EconomyResponse withNull = economy.withdrawPlayer((String) null, 50.0);
        assertFalse(withNull.transactionSuccess());

        assertFalse(economy.createPlayerAccount((String) null));
        assertFalse(economy.createPlayerAccount("   "));
    }

    @Test
    void testRecursionGuardPreventsStackOverflow() {
        BareEconomy bare = new BareEconomy();

        // When neither method is overridden, calling either must safely return defaults without StackOverflowError
        assertDoesNotThrow(() -> {
            assertFalse(bare.hasAccount("Alex"));
            assertEquals(0.0, bare.getBalance("Alex"));
            assertFalse(bare.has("Alex", 10.0));

            EconomyResponse dep = bare.depositPlayer("Alex", 10.0);
            assertFalse(dep.transactionSuccess());

            EconomyResponse with = bare.withdrawPlayer("Alex", 10.0);
            assertFalse(with.transactionSuccess());

            assertFalse(bare.createPlayerAccount("Alex"));
        });
    }
}
