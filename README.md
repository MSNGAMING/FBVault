# Vault (Native Paper & Folia Plugin Architecture)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%20to%2026.3-brightgreen.svg)](#)
[![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Purpur%20%7C%20Folia-blue.svg)](#)
[![Plugin Type](https://img.shields.io/badge/Plugin%20Type-Native%20Paper%20Plugin%20(paper--plugin.yml)-purple.svg)](#)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](#)
[![License](https://img.shields.io/badge/License-LGPLv3-lightgrey.svg)](#)

A high-performance, 1:1 drop-in replacement for the legacy **Vault** plugin designed and packaged as a **Native Paper Plugin** (`paper-plugin.yml`) for **Paper**, **Purpur**, and **Folia** servers.

It provides identical package paths, class names, method signatures, and service registrations (`net.milkbowl.vault.*`) so that **any plugin compiling against or depending on legacy Vault runs out of the box with zero modifications**, while gaining the performance, classloader isolation, and modern lifecycle architecture of native Paper plugins.

---

## Native Paper Plugin Features

### 1. Paper Lifecycle API & Plugin Bootstrap
* **`paper-plugin.yml`**: Uses Paper's modern plugin descriptor system with `provides: [Vault]` and `folia-supported: true`.
* **Early Lifecycle Bootstrapper (`PluginBootstrap`)**: `VaultBootstrap` executes during Paper's bootstrap phase before standard plugins, ensuring early availability.
* **Brigadier Commands (`BasicCommand`)**: Commands (`/vault`, `/vault-info`, `/vault-convert`) are registered via Paper's `LifecycleEvents.COMMANDS` with the Brigadier command registrar, giving players client-side suggestion tooltips and datapack compatibility without relying on legacy `plugin.yml` mappings.

### 1. Folia Native Multithreading
* **`folia-supported: true`**: Flags the plugin for native Folia execution without compatibility layers or warnings.
* **Regional & Async Scheduling**: Replaces legacy `Bukkit.getScheduler()` calls with `FoliaScheduler`, cleanly routing between Folia's `AsyncScheduler`, `GlobalRegionScheduler`, and Paper's scheduler without throwing `UnsupportedOperationException`.
* **Lock-Free Concurrency**: All internal service lookup references, event handlers, and data structures use thread-safe and atomic primitives (`AtomicBoolean`, `AtomicReference`, `ConcurrentHashMap`) to support simultaneous access across ticked regions and async threads (e.g., DiscordSRV, shop plugins).

### 2. Built-in Lightweight Economy Engine
* **Out-of-the-Box Economy**: If no external economy plugin (like EssentialsX) is installed, Vault automatically provides a high-performance, Folia-safe built-in economy engine (`VaultBuiltinEconomy`) registered at `ServicePriority.Low`.
* **Zero Race Conditions**: Uses atomic `compute()` on concurrent maps so simultaneous transactions across Folia's region threads and async threads never cause double-spending or data races.
* **Asynchronous Persistence**: In-memory O(1) balance operations backed by asynchronous JSON file persistence (`plugins/Vault/balances.json`) with safe atomic disk flushes.
* **Seamless Supersession**: Because it registers with `ServicePriority.Low`, if you ever install a dedicated economy plugin (like EssentialsX or CMI) later, the external plugin will automatically supersede it without requiring configuration edits.
* **Built-in Player Commands**:
  * `/balance` (aliases: `/bal`, `/money`) - View balance
  * `/pay <player> <amount>` - Transfer funds between players
  * `/eco <give|take|set|reset> <player> [amount]` - Administrator economy management

### 3. Safe Legacy String-Based Name Fallbacks
* **UUID-First with Safe Fallback**: In `AbstractEconomy`, legacy String methods (e.g. `getBalance(String)`, `hasAccount(String)`, `depositPlayer(String, double)`) safely forward to the modern `OfflinePlayer` variants via `Bukkit.getOfflinePlayer(name.trim())`.
* **Zero NullPointerExceptions**: Thorough input validation ensures null or blank player names return safe responses (`0.0`, `false`, `ResponseType.FAILURE`) instead of crashing the server.
* **Bi-directional Recursion Guard**: Implements thread-local recursion guards so incomplete or custom economy implementations that fail to override one of the overloads will never throw a `StackOverflowError`.

### 3. Asynchronous Economy Conversion
* **No Server Freezes**: Unlike legacy Vault which loops through all offline player data on the main thread and freezes server ticks, `/vault-convert` executes completely asynchronously on the `AsyncScheduler`.
* **Configurable Batching**: Configurable batch sizes and tick delays yield execution time to prevent starving database connection pools.
* **Real-time Progress Reporting**: Live MiniMessage progress updates in chat and console with detailed summary metrics upon completion.

### 4. Built-in SuperPerms Base Provider
* Automatically registers `Permission_SuperPerms` with `ServicePriority.Lowest`. If LuckPerms or another permissions manager is installed, its higher priority takes precedence; if no permission plugin is installed, dependent plugins querying `Permission.class` will receive a valid, functioning SuperPerms bridge.

### 5. Adventure MiniMessage Support
* Built-in Adventure Component and MiniMessage support for modern formatted command output and logs, with automated legacy color code translation where necessary.

---

## Commands & Permissions

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/vault` or `/vault info` | `vault.admin` | Displays active Economy, Permission, and Chat providers, platform status, and diagnostics |
| `/vault-info` | `vault.admin` | Legacy command alias for `/vault info` |
| `/vault convert <from> <to>` | `vault.admin` | Asynchronously converts all account balances from one economy provider to another |
| `/vault-convert <from> <to>` | `vault.admin` | Legacy command alias for `/vault convert` |
| `/vault reload` | `vault.admin` | Reloads `config.yml` configuration |
| `/balance` or `/bal`, `/money` | `vault.economy.balance` | Checks your personal balance |
| `/balance <player>` | `vault.economy.balance.others` | Checks another player's balance |
| `/pay <player> <amount>` | `vault.economy.pay` | Transfers money to another player |
| `/eco <give\|take\|set\|reset> <player> [amount]` | `vault.economy.admin` | Administrator economy control (deposit, withdraw, set, or reset) |

---

## Configuration (`config.yml`)

```yaml
# ==============================================================================
#                  Vault (Native Paper & Folia Architecture)
# ==============================================================================

# Enable checking for updates (legacy check disabled by default)
update-check: false

# Built-in Lightweight Economy Engine
# Enabled by default so that plugins depending on Vault Economy (e.g. PremiumShop)
# work out-of-the-box without requiring EssentialsX or other heavy plugins.
# If a dedicated economy plugin is installed, its higher priority will supersede this.
builtin-economy:
  enabled: true
  currency-symbol: "$"
  currency-name-singular: "Dollar"
  currency-name-plural: "Dollars"
  starting-balance: 100.0
  fractional-digits: 2
  auto-create-accounts: true
  save-interval-seconds: 60

# Asynchronous Economy Conversion Settings (/vault-convert)
conversion:
  # Number of accounts to process per batch
  batch-size: 100
  # Delay (in ticks) between batches to maintain stable TPS/MSPT on large servers
  batch-delay-ticks: 1

# Log provider registrations and unregistrations to console
log-service-changes: true
```

---

## Building from Source

### Using Maven (Recommended)
```bash
mvn clean package
```
The compiled plugin JAR will be located at `target/Vault-1.7.3-modern.jar`.

### Using Gradle
```bash
./gradlew build
```
The compiled plugin JAR will be located at `build/libs/FBVault-1.7.3-modern.jar`.

---

## Project Structure

```
FBVault/
├── pom.xml                                      # Maven build config (Java 21, Paper 1.21+)
├── build.gradle.kts                             # Gradle Kotlin DSL build script
├── settings.gradle.kts                          # Gradle project settings
├── README.md                                    # Full documentation & build guide
├── src/main/resources/
│   ├── paper-plugin.yml                         # Native Paper plugin descriptor (provides: [Vault])
│   └── config.yml                               # Settings for async conversions & logging
├── src/main/java/net/milkbowl/vault/
│   ├── Vault.java                               # Main JavaPlugin entry point
│   ├── VaultBootstrap.java                      # Paper PluginBootstrap (LifecycleEvents.COMMANDS)
│   ├── command/
│   │   ├── VaultCommand.java                    # Paper BasicCommand for /vault, /vault-info
│   │   └── VaultConvertCommand.java             # Paper BasicCommand for /vault-convert
│   ├── economy/
│   │   ├── Economy.java                         # Standard Vault Economy interface
│   │   ├── EconomyResponse.java                 # Standard transaction outcome object
│   │   └── AbstractEconomy.java                 # Modernized fallback & recursion guard
│   ├── permission/
│   │   ├── Permission.java                      # Vault Permission abstract class
│   │   └── plugins/
│   │       └── Permission_SuperPerms.java       # Default SuperPerms bridge
│   ├── chat/
│   │   └── Chat.java                            # Vault Chat abstract class
│   └── util/
│       ├── FoliaScheduler.java                  # Folia/Paper universal async/global scheduler
│       └── VaultText.java                       # Adventure MiniMessage formatter
└── src/test/java/net/milkbowl/vault/economy/
    └── AbstractEconomyTest.java                 # Unit tests (MockBukkit / JUnit 5)
```

---

## API Binary Compatibility

The compiled JAR contains the complete `VaultAPI` specifications:

* `net.milkbowl.vault.Vault`
* `net.milkbowl.vault.economy.Economy`
* `net.milkbowl.vault.economy.AbstractEconomy`
* `net.milkbowl.vault.economy.EconomyResponse`
* `net.milkbowl.vault.permission.Permission`
* `net.milkbowl.vault.permission.plugins.Permission_SuperPerms`
* `net.milkbowl.vault.chat.Chat`

Any plugin depending on Vault via `plugin.yml` (`depend: [Vault]` or `softdepend: [Vault]`) will automatically hook into this plugin.
