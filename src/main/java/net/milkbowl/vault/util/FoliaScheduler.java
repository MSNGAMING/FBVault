package net.milkbowl.vault.util;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Universal thread-safe scheduler abstraction supporting Folia, Paper, and Purpur.
 * Seamlessly routes asynchronous and global-region tasks while preventing UnsupportedOperationExceptions
 * on Folia's regionized multithreading architecture.
 */
public final class FoliaScheduler {

    private static final boolean IS_FOLIA;
    private static final boolean HAS_PAPER_ASYNC;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
            folia = false;
        }
        IS_FOLIA = folia;

        boolean paperAsync = false;
        try {
            Bukkit.getServer().getAsyncScheduler();
            paperAsync = true;
        } catch (Throwable ignored) {
            paperAsync = false;
        }
        HAS_PAPER_ASYNC = paperAsync;
    }

    private FoliaScheduler() {
    }

    /**
     * Checks if the server is running on a Folia-based regionized threading platform.
     *
     * @return true if running Folia
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }

    /**
     * Runs a task asynchronously.
     *
     * @param plugin plugin instance
     * @param task   runnable task
     */
    public static void runAsync(Plugin plugin, Runnable task) {
        if (HAS_PAPER_ASYNC) {
            Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * Runs a repeating task asynchronously.
     *
     * @param plugin      plugin instance
     * @param task        task consumer accepting scheduled task or runnable
     * @param delayTicks  delay before first run in ticks (50ms per tick)
     * @param periodTicks period between runs in ticks (50ms per tick)
     */
    public static void runAsyncTimer(Plugin plugin, Consumer<Object> task, long delayTicks, long periodTicks) {
        long delayMs = Math.max(1, delayTicks) * 50L;
        long periodMs = Math.max(1, periodTicks) * 50L;

        if (HAS_PAPER_ASYNC) {
            Bukkit.getAsyncScheduler().runAtFixedRate(
                    plugin,
                    scheduledTask -> task.accept(scheduledTask),
                    delayMs,
                    periodMs,
                    TimeUnit.MILLISECONDS
            );
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> task.accept(null), delayTicks, periodTicks);
        }
    }

    /**
     * Runs a delayed task asynchronously.
     *
     * @param plugin     plugin instance
     * @param task       runnable task
     * @param delayTicks delay in ticks
     */
    public static void runAsyncDelayed(Plugin plugin, Runnable task, long delayTicks) {
        long delayMs = Math.max(1, delayTicks) * 50L;
        if (HAS_PAPER_ASYNC) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> task.run(), delayMs, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
        }
    }

    /**
     * Runs a task on the global region / main thread.
     *
     * @param plugin plugin instance
     * @param task   runnable task
     */
    public static void runGlobal(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Cancels all scheduled tasks for this plugin across both Folia schedulers and legacy schedulers.
     *
     * @param plugin plugin instance
     */
    public static void cancelTasks(Plugin plugin) {
        if (HAS_PAPER_ASYNC) {
            try {
                Bukkit.getAsyncScheduler().cancelTasks(plugin);
            } catch (Throwable ignored) {
            }
        }
        if (IS_FOLIA) {
            try {
                Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
            } catch (Throwable ignored) {
            }
        } else {
            try {
                Bukkit.getScheduler().cancelTasks(plugin);
            } catch (Throwable ignored) {
            }
        }
    }
}
