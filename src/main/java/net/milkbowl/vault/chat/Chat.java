package net.milkbowl.vault.chat;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * The main Chat API for Vault.
 * Manages player and group prefixes, suffixes, and custom metadata nodes.
 */
public abstract class Chat {

    protected Permission perms;

    public Chat(Permission perms) {
        this.perms = perms;
    }

    /**
     * Gets the name of the chat provider.
     *
     * @return name
     */
    public abstract String getName();

    /**
     * Checks if the chat provider is enabled.
     *
     * @return true if enabled
     */
    public abstract boolean isEnabled();

    // =========================================================================
    // Player Prefix
    // =========================================================================

    @Deprecated
    public abstract String getPlayerPrefix(@Nullable String world, String player);

    public String getPlayerPrefix(@Nullable String world, OfflinePlayer player) {
        if (player == null) {
            return "";
        }
        String name = player.getName();
        return name == null ? "" : getPlayerPrefix(world, name);
    }

    @Deprecated
    public String getPlayerPrefix(@Nullable World world, String player) {
        return getPlayerPrefix(world == null ? null : world.getName(), player);
    }

    public String getPlayerPrefix(Player player) {
        if (player == null) {
            return "";
        }
        return getPlayerPrefix(player.getWorld().getName(), (OfflinePlayer) player);
    }

    @Deprecated
    public abstract void setPlayerPrefix(@Nullable String world, String player, String prefix);

    public void setPlayerPrefix(@Nullable String world, OfflinePlayer player, String prefix) {
        if (player == null) {
            return;
        }
        String name = player.getName();
        if (name != null) {
            setPlayerPrefix(world, name, prefix);
        }
    }

    @Deprecated
    public void setPlayerPrefix(@Nullable World world, String player, String prefix) {
        setPlayerPrefix(world == null ? null : world.getName(), player, prefix);
    }

    public void setPlayerPrefix(Player player, String prefix) {
        if (player == null) {
            return;
        }
        setPlayerPrefix(player.getWorld().getName(), (OfflinePlayer) player, prefix);
    }

    // =========================================================================
    // Player Suffix
    // =========================================================================

    @Deprecated
    public abstract String getPlayerSuffix(@Nullable String world, String player);

    public String getPlayerSuffix(@Nullable String world, OfflinePlayer player) {
        if (player == null) {
            return "";
        }
        String name = player.getName();
        return name == null ? "" : getPlayerSuffix(world, name);
    }

    @Deprecated
    public String getPlayerSuffix(@Nullable World world, String player) {
        return getPlayerSuffix(world == null ? null : world.getName(), player);
    }

    public String getPlayerSuffix(Player player) {
        if (player == null) {
            return "";
        }
        return getPlayerSuffix(player.getWorld().getName(), (OfflinePlayer) player);
    }

    @Deprecated
    public abstract void setPlayerSuffix(@Nullable String world, String player, String suffix);

    public void setPlayerSuffix(@Nullable String world, OfflinePlayer player, String suffix) {
        if (player == null) {
            return;
        }
        String name = player.getName();
        if (name != null) {
            setPlayerSuffix(world, name, suffix);
        }
    }

    @Deprecated
    public void setPlayerSuffix(@Nullable World world, String player, String suffix) {
        setPlayerSuffix(world == null ? null : world.getName(), player, suffix);
    }

    public void setPlayerSuffix(Player player, String suffix) {
        if (player == null) {
            return;
        }
        setPlayerSuffix(player.getWorld().getName(), (OfflinePlayer) player, suffix);
    }

    // =========================================================================
    // Group Prefix / Suffix
    // =========================================================================

    public abstract String getGroupPrefix(@Nullable String world, String group);

    public String getGroupPrefix(@Nullable World world, String group) {
        return getGroupPrefix(world == null ? null : world.getName(), group);
    }

    public abstract void setGroupPrefix(@Nullable String world, String group, String prefix);

    public void setGroupPrefix(@Nullable World world, String group, String prefix) {
        setGroupPrefix(world == null ? null : world.getName(), group, prefix);
    }

    public abstract String getGroupSuffix(@Nullable String world, String group);

    public String getGroupSuffix(@Nullable World world, String group) {
        return getGroupSuffix(world == null ? null : world.getName(), group);
    }

    public abstract void setGroupSuffix(@Nullable String world, String group, String suffix);

    public void setGroupSuffix(@Nullable World world, String group, String suffix) {
        setGroupSuffix(world == null ? null : world.getName(), group, suffix);
    }

    // =========================================================================
    // Metadata Node: Integer
    // =========================================================================

    public int getPlayerInfoInteger(@Nullable String world, OfflinePlayer player, String node, int defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        String name = player.getName();
        return name == null ? defaultValue : getPlayerInfoInteger(world, name, node, defaultValue);
    }

    @Deprecated
    public abstract int getPlayerInfoInteger(@Nullable String world, String player, String node, int defaultValue);

    @Deprecated
    public int getPlayerInfoInteger(@Nullable World world, String player, String node, int defaultValue) {
        return getPlayerInfoInteger(world == null ? null : world.getName(), player, node, defaultValue);
    }

    public int getPlayerInfoInteger(Player player, String node, int defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        return getPlayerInfoInteger(player.getWorld().getName(), (OfflinePlayer) player, node, defaultValue);
    }

    public void setPlayerInfoInteger(@Nullable String world, OfflinePlayer player, String node, int value) {
        if (player == null) {
            return;
        }
        String name = player.getName();
        if (name != null) {
            setPlayerInfoInteger(world, name, node, value);
        }
    }

    @Deprecated
    public abstract void setPlayerInfoInteger(@Nullable String world, String player, String node, int value);

    @Deprecated
    public void setPlayerInfoInteger(@Nullable World world, String player, String node, int value) {
        setPlayerInfoInteger(world == null ? null : world.getName(), player, node, value);
    }

    public void setPlayerInfoInteger(Player player, String node, int value) {
        if (player == null) {
            return;
        }
        setPlayerInfoInteger(player.getWorld().getName(), (OfflinePlayer) player, node, value);
    }

    public abstract int getGroupInfoInteger(@Nullable String world, String group, String node, int defaultValue);

    public int getGroupInfoInteger(@Nullable World world, String group, String node, int defaultValue) {
        return getGroupInfoInteger(world == null ? null : world.getName(), group, node, defaultValue);
    }

    public abstract void setGroupInfoInteger(@Nullable String world, String group, String node, int value);

    public void setGroupInfoInteger(@Nullable World world, String group, String node, int value) {
        setGroupInfoInteger(world == null ? null : world.getName(), group, node, value);
    }

    // =========================================================================
    // Metadata Node: Double
    // =========================================================================

    public double getPlayerInfoDouble(@Nullable String world, OfflinePlayer player, String node, double defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        String name = player.getName();
        return name == null ? defaultValue : getPlayerInfoDouble(world, name, node, defaultValue);
    }

    @Deprecated
    public abstract double getPlayerInfoDouble(@Nullable String world, String player, String node, double defaultValue);

    @Deprecated
    public double getPlayerInfoDouble(@Nullable World world, String player, String node, double defaultValue) {
        return getPlayerInfoDouble(world == null ? null : world.getName(), player, node, defaultValue);
    }

    public double getPlayerInfoDouble(Player player, String node, double defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        return getPlayerInfoDouble(player.getWorld().getName(), (OfflinePlayer) player, node, defaultValue);
    }

    public void setPlayerInfoDouble(@Nullable String world, OfflinePlayer player, String node, double value) {
        if (player == null) {
            return;
        }
        String name = player.getName();
        if (name != null) {
            setPlayerInfoDouble(world, name, node, value);
        }
    }

    @Deprecated
    public abstract void setPlayerInfoDouble(@Nullable String world, String player, String node, double value);

    @Deprecated
    public void setPlayerInfoDouble(@Nullable World world, String player, String node, double value) {
        setPlayerInfoDouble(world == null ? null : world.getName(), player, node, value);
    }

    public void setPlayerInfoDouble(Player player, String node, double value) {
        if (player == null) {
            return;
        }
        setPlayerInfoDouble(player.getWorld().getName(), (OfflinePlayer) player, node, value);
    }

    public abstract double getGroupInfoDouble(@Nullable String world, String group, String node, double defaultValue);

    public double getGroupInfoDouble(@Nullable World world, String group, String node, double defaultValue) {
        return getGroupInfoDouble(world == null ? null : world.getName(), group, node, defaultValue);
    }

    public abstract void setGroupInfoDouble(@Nullable String world, String group, String node, double value);

    public void setGroupInfoDouble(@Nullable World world, String group, String node, double value) {
        setGroupInfoDouble(world == null ? null : world.getName(), group, node, value);
    }

    // =========================================================================
    // Metadata Node: Boolean
    // =========================================================================

    public boolean getPlayerInfoBoolean(@Nullable String world, OfflinePlayer player, String node, boolean defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        String name = player.getName();
        return name == null ? defaultValue : getPlayerInfoBoolean(world, name, node, defaultValue);
    }

    @Deprecated
    public abstract boolean getPlayerInfoBoolean(@Nullable String world, String player, String node, boolean defaultValue);

    @Deprecated
    public boolean getPlayerInfoBoolean(@Nullable World world, String player, String node, boolean defaultValue) {
        return getPlayerInfoBoolean(world == null ? null : world.getName(), player, node, defaultValue);
    }

    public boolean getPlayerInfoBoolean(Player player, String node, boolean defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        return getPlayerInfoBoolean(player.getWorld().getName(), (OfflinePlayer) player, node, defaultValue);
    }

    public void setPlayerInfoBoolean(@Nullable String world, OfflinePlayer player, String node, boolean value) {
        if (player == null) {
            return;
        }
        String name = player.getName();
        if (name != null) {
            setPlayerInfoBoolean(world, name, node, value);
        }
    }

    @Deprecated
    public abstract void setPlayerInfoBoolean(@Nullable String world, String player, String node, boolean value);

    @Deprecated
    public void setPlayerInfoBoolean(@Nullable World world, String player, String node, boolean value) {
        setPlayerInfoBoolean(world == null ? null : world.getName(), player, node, value);
    }

    public void setPlayerInfoBoolean(Player player, String node, boolean value) {
        if (player == null) {
            return;
        }
        setPlayerInfoBoolean(player.getWorld().getName(), (OfflinePlayer) player, node, value);
    }

    public abstract boolean getGroupInfoBoolean(@Nullable String world, String group, String node, boolean defaultValue);

    public boolean getGroupInfoBoolean(@Nullable World world, String group, String node, boolean defaultValue) {
        return getGroupInfoBoolean(world == null ? null : world.getName(), group, node, defaultValue);
    }

    public abstract void setGroupInfoBoolean(@Nullable String world, String group, String node, boolean value);

    public void setGroupInfoBoolean(@Nullable World world, String group, String node, boolean value) {
        setGroupInfoBoolean(world == null ? null : world.getName(), group, node, value);
    }

    // =========================================================================
    // Metadata Node: String
    // =========================================================================

    public String getPlayerInfoString(@Nullable String world, OfflinePlayer player, String node, String defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        String name = player.getName();
        return name == null ? defaultValue : getPlayerInfoString(world, name, node, defaultValue);
    }

    @Deprecated
    public abstract String getPlayerInfoString(@Nullable String world, String player, String node, String defaultValue);

    @Deprecated
    public String getPlayerInfoString(@Nullable World world, String player, String node, String defaultValue) {
        return getPlayerInfoString(world == null ? null : world.getName(), player, node, defaultValue);
    }

    public String getPlayerInfoString(Player player, String node, String defaultValue) {
        if (player == null) {
            return defaultValue;
        }
        return getPlayerInfoString(player.getWorld().getName(), (OfflinePlayer) player, node, defaultValue);
    }

    public void setPlayerInfoString(@Nullable String world, OfflinePlayer player, String node, String value) {
        if (player == null) {
            return;
        }
        String name = player.getName();
        if (name != null) {
            setPlayerInfoString(world, name, node, value);
        }
    }

    @Deprecated
    public abstract void setPlayerInfoString(@Nullable String world, String player, String node, String value);

    @Deprecated
    public void setPlayerInfoString(@Nullable World world, String player, String node, String value) {
        setPlayerInfoString(world == null ? null : world.getName(), player, node, value);
    }

    public void setPlayerInfoString(Player player, String node, String value) {
        if (player == null) {
            return;
        }
        setPlayerInfoString(player.getWorld().getName(), (OfflinePlayer) player, node, value);
    }

    public abstract String getGroupInfoString(@Nullable String world, String group, String node, String defaultValue);

    public String getGroupInfoString(@Nullable World world, String group, String node, String defaultValue) {
        return getGroupInfoString(world == null ? null : world.getName(), group, node, defaultValue);
    }

    public abstract void setGroupInfoString(@Nullable String world, String group, String node, String value);

    public void setGroupInfoString(@Nullable World world, String group, String node, String value) {
        setGroupInfoString(world == null ? null : world.getName(), group, node, value);
    }

    // =========================================================================
    // Delegated Group Operations (from Permission)
    // =========================================================================

    public boolean playerInGroup(@Nullable String world, OfflinePlayer player, String group) {
        return this.perms != null && this.perms.playerInGroup(world, player, group);
    }

    @Deprecated
    public boolean playerInGroup(@Nullable String world, String player, String group) {
        return this.perms != null && this.perms.playerInGroup(world, player, group);
    }

    @Deprecated
    public boolean playerInGroup(@Nullable World world, String player, String group) {
        return playerInGroup(world == null ? null : world.getName(), player, group);
    }

    public boolean playerInGroup(Player player, String group) {
        return playerInGroup(player.getWorld().getName(), (OfflinePlayer) player, group);
    }

    public String[] getPlayerGroups(@Nullable String world, OfflinePlayer player) {
        return this.perms == null ? new String[0] : this.perms.getPlayerGroups(world, player);
    }

    @Deprecated
    public String[] getPlayerGroups(@Nullable String world, String player) {
        return this.perms == null ? new String[0] : this.perms.getPlayerGroups(world, player);
    }

    @Deprecated
    public String[] getPlayerGroups(@Nullable World world, String player) {
        return getPlayerGroups(world == null ? null : world.getName(), player);
    }

    public String[] getPlayerGroups(Player player) {
        return getPlayerGroups(player.getWorld().getName(), (OfflinePlayer) player);
    }

    public String getPrimaryGroup(@Nullable String world, OfflinePlayer player) {
        return this.perms == null ? null : this.perms.getPrimaryGroup(world, player);
    }

    @Deprecated
    public String getPrimaryGroup(@Nullable String world, String player) {
        return this.perms == null ? null : this.perms.getPrimaryGroup(world, player);
    }

    @Deprecated
    public String getPrimaryGroup(@Nullable World world, String player) {
        return getPrimaryGroup(world == null ? null : world.getName(), player);
    }

    public String getPrimaryGroup(Player player) {
        return getPrimaryGroup(player.getWorld().getName(), (OfflinePlayer) player);
    }

    public String[] getGroups() {
        return this.perms == null ? new String[0] : this.perms.getGroups();
    }
}
