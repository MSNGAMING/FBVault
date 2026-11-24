package net.milkbowl.vault.permission.plugins;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * SuperPerms fallback implementation for Vault.
 * Provides basic Bukkit permissible checks when no dedicated permission manager is active.
 */
public class Permission_SuperPerms extends Permission {

    private static final String NAME = "SuperPerms";
    private static final String[] EMPTY_ARRAY = new String[0];

    public Permission_SuperPerms(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean playerHas(@Nullable String world, String player, String permission) {
        if (player == null || permission == null) {
            return false;
        }
        Player p = Bukkit.getPlayerExact(player);
        if (p != null) {
            return p.hasPermission(permission);
        }
        OfflinePlayer op = Bukkit.getOfflinePlayer(player);
        if (op.isOnline()) {
            Player onlinePlayer = op.getPlayer();
            return onlinePlayer != null && onlinePlayer.hasPermission(permission);
        }
        return false;
    }

    @Override
    public boolean playerHas(@Nullable String world, OfflinePlayer player, String permission) {
        if (player == null || permission == null) {
            return false;
        }
        if (player.isOnline()) {
            Player p = player.getPlayer();
            return p != null && p.hasPermission(permission);
        }
        return false;
    }

    @Override
    public boolean playerAdd(@Nullable String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean playerRemove(@Nullable String world, String player, String permission) {
        return false;
    }

    @Override
    public boolean groupHas(@Nullable String world, String group, String permission) {
        throw new UnsupportedOperationException(getName() + " does not support group permissions.");
    }

    @Override
    public boolean groupAdd(@Nullable String world, String group, String permission) {
        throw new UnsupportedOperationException(getName() + " does not support group permissions.");
    }

    @Override
    public boolean groupRemove(@Nullable String world, String group, String permission) {
        throw new UnsupportedOperationException(getName() + " does not support group permissions.");
    }

    @Override
    public boolean playerInGroup(@Nullable String world, String player, String group) {
        return playerHas(world, player, "groups." + group);
    }

    @Override
    public boolean playerAddGroup(@Nullable String world, String player, String group) {
        throw new UnsupportedOperationException(getName() + " does not support group permissions.");
    }

    @Override
    public boolean playerRemoveGroup(@Nullable String world, String player, String group) {
        throw new UnsupportedOperationException(getName() + " does not support group permissions.");
    }

    @Override
    public String[] getPlayerGroups(@Nullable String world, String player) {
        throw new UnsupportedOperationException(getName() + " does not support group permissions.");
    }

    @Override
    public String getPrimaryGroup(@Nullable String world, String player) {
        throw new UnsupportedOperationException(getName() + " does not support group permissions.");
    }

    @Override
    public String[] getGroups() {
        return EMPTY_ARRAY;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }
}
