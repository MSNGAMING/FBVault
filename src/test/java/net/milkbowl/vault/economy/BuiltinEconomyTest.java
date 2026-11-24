package net.milkbowl.vault.economy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import net.milkbowl.vault.economy.builtin.BuiltinEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BuiltinEconomyTest {

    private static Server mockServer;
    private BuiltinEconomy economy;

    @TempDir
    File tempFolder;

    @BeforeAll
    static void setUpBukkit() throws Exception {
        mockServer = mock(Server.class);
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
    void setUp() {
        economy = new BuiltinEconomy(tempFolder, Logger.getLogger("BuiltinEconomyTest"), null);
        economy.configure("$", "Dollar", "Dollars", 100.0, 2, true);
    }

    private OfflinePlayer createMockPlayer(String name, UUID uuid) {
        OfflinePlayer op = mock(OfflinePlayer.class);
        when(op.getName()).thenReturn(name);
        when(op.getUniqueId()).thenReturn(uuid);
        return op;
    }

    @Test
    void testInitialBalanceAndFormatting() {
        OfflinePlayer player = createMockPlayer("Player1", UUID.randomUUID());

        // Initial balance should be 100.0 (starting balance)
        assertEquals(100.0, economy.getBalance(player), 0.001);
        assertEquals("$100.00", economy.format(100.0));
        assertEquals("$1,500.50", economy.format(1500.5));
        assertEquals("Dollar", economy.currencyNameSingular());
        assertEquals("Dollars", economy.currencyNamePlural());
    }

    @Test
    void testDepositAndWithdraw() {
        OfflinePlayer player = createMockPlayer("Player2", UUID.randomUUID());

        // Deposit 50
        EconomyResponse dep = economy.depositPlayer(player, 50.0);
        assertTrue(dep.transactionSuccess());
        assertEquals(150.0, dep.balance, 0.001);
        assertEquals(150.0, economy.getBalance(player), 0.001);

        // Withdraw 70
        EconomyResponse with = economy.withdrawPlayer(player, 70.0);
        assertTrue(with.transactionSuccess());
        assertEquals(80.0, with.balance, 0.001);
        assertEquals(80.0, economy.getBalance(player), 0.001);

        // Try to withdraw more than available (80 balance, withdraw 100)
        EconomyResponse overWith = economy.withdrawPlayer(player, 100.0);
        assertFalse(overWith.transactionSuccess());
        assertEquals(ResponseType.FAILURE, overWith.type);
        assertEquals(80.0, overWith.balance, 0.001);
        assertEquals(80.0, economy.getBalance(player), 0.001);
    }

    @Test
    void testPersistenceSaveAndReload() {
        OfflinePlayer player = createMockPlayer("PersistentUser", UUID.randomUUID());

        economy.depositPlayer(player, 250.75);
        assertEquals(350.75, economy.getBalance(player), 0.001);

        // Save synchronously to disk
        economy.saveSync();

        File balancesFile = new File(tempFolder, "balances.json");
        assertTrue(balancesFile.exists());

        // Create fresh economy instance and load from disk
        BuiltinEconomy reloaded = new BuiltinEconomy(tempFolder, Logger.getLogger("ReloadedTest"), null);
        reloaded.configure("$", "Dollar", "Dollars", 100.0, 2, true);
        reloaded.load();

        assertEquals(350.75, reloaded.getBalance(player), 0.001);
    }

    @Test
    void testConcurrentAtomicTransactions() throws InterruptedException {
        OfflinePlayer player = createMockPlayer("ConcurrentUser", UUID.randomUUID());
        // Starting balance: 100.0

        int threads = 8;
        int operationsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        // Each thread deposits 10 and withdraws 5 -> net +5 per operation
        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        economy.depositPlayer(player, 10.0);
                        economy.withdrawPlayer(player, 5.0);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Expected balance: 100 + (8 * 50 * 5) = 100 + 2000 = 2100.0
        double expected = 100.0 + (threads * operationsPerThread * 5.0);
        assertEquals(expected, economy.getBalance(player), 0.001);
    }
}
