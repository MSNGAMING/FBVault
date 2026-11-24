package net.milkbowl.vault.permission;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * The main Permission API for Vault.
 * Allows for group-based and player-based permission queries and manipulations.
 */
public abstract class Permission {

    protected static final Logger log = Logger.getLogger("Minecraft");
    protected Plugin plugin = null;

    /**
     * Gets the name of the permission method/provider.
     *
     * @return Name of Permission Method
     */
    public abstract String getName();

    /**
     * Checks if the permission provider is enabled.
     *
     * @return true if enabled
     */
    public abstract boolean isEnabled();

    /**
     * Returns true if the permission system is or attempts to be compatible with SuperPerms.
     *
     * @return true if SuperPerms compatible
     */
    public abstract boolean hasSuperPermsCompat();

    /**
     * Checks if a player has permission in the given world.
     *
     * @param world      World name or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if player has permission
     * @deprecated As of VaultAPI 1.4 use {@link #playerHas(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean has(@Nullable String world, String player, String permission) {
        return playerHas(world, player, permission);
    }

    /**
     * Checks if a player has permission in the given world.
     *
     * @param world      World object or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if player has permission
     * @deprecated As of VaultAPI 1.4 use {@link #playerHas(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean has(@Nullable World world, String player, String permission) {
        return playerHas(world == null ? null : world.getName(), player, permission);
    }

    /**
     * Checks if a CommandSender has a permission node using standard Bukkit SuperPerms.
     *
     * @param sender     CommandSender to check
     * @param permission Permission node
     * @return true if sender has permission
     */
    public boolean has(CommandSender sender, String permission) {
        return sender != null && sender.hasPermission(permission);
    }

    /**
     * Checks if player has a permission node using Bukkit SuperPerms.
     *
     * @param player     Player to check
     * @param permission Permission node
     * @return true if player has permission
     */
    public boolean has(Player player, String permission) {
        return player != null && player.hasPermission(permission);
    }

    /**
     * Checks if player has a permission node in the given world.
     *
     * @param world      World name or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if player has permission
     * @deprecated As of VaultAPI 1.4 use {@link #playerHas(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public abstract boolean playerHas(@Nullable String world, String player, String permission);

    /**
     * Checks if player has a permission node in the given world.
     *
     * @param world      World or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if player has permission
     * @deprecated As of VaultAPI 1.4 use {@link #playerHas(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean playerHas(@Nullable World world, String player, String permission) {
        return playerHas(world == null ? null : world.getName(), player, permission);
    }

    /**
     * Checks if an OfflinePlayer has a permission node in the specified world.
     *
     * @param world      World name or null for global
     * @param player     OfflinePlayer to check
     * @param permission Permission node
     * @return true if player has permission
     */
    public boolean playerHas(@Nullable String world, OfflinePlayer player, String permission) {
        if (player == null) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        return has(world, name, permission);
    }

    /**
     * Checks if player has a permission node on their current world.
     *
     * @param player     Player object
     * @param permission Permission node
     * @return true if player has permission
     */
    public boolean playerHas(Player player, String permission) {
        return has(player, permission);
    }

    /**
     * Adds permission to a player.
     *
     * @param world      World name or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerAdd(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public abstract boolean playerAdd(@Nullable String world, String player, String permission);

    /**
     * Adds permission to a player.
     *
     * @param world      World object or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerAdd(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean playerAdd(@Nullable World world, String player, String permission) {
        return playerAdd(world == null ? null : world.getName(), player, permission);
    }

    /**
     * Adds permission to a player.
     *
     * @param world      World name or null for global
     * @param player     OfflinePlayer to add to
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerAdd(@Nullable String world, OfflinePlayer player, String permission) {
        if (player == null) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        return playerAdd(world, name, permission);
    }

    /**
     * Adds permission to a player on their current world.
     *
     * @param player     Player object
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerAdd(Player player, String permission) {
        if (player == null) {
            return false;
        }
        return playerAdd(player.getWorld().getName(), (OfflinePlayer) player, permission);
    }

    /**
     * Adds transient permission to an offline player.
     *
     * @param player     OfflinePlayer
     * @param permission Permission node
     * @return true if successful
     * @throws UnsupportedOperationException if offline transient permissions are unsupported
     */
    public boolean playerAddTransient(OfflinePlayer player, String permission) throws UnsupportedOperationException {
        if (player != null && player.isOnline() && player.getPlayer() != null) {
            return playerAddTransient(player.getPlayer(), permission);
        }
        throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
    }

    /**
     * Adds transient permission to a player via Bukkit's PermissionAttachment.
     *
     * @param player     Player object
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerAddTransient(Player player, String permission) {
        if (player == null || permission == null) {
            return false;
        }
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
            PermissionAttachment attach = paInfo.getAttachment();
            if (attach != null && this.plugin != null && this.plugin.equals(attach.getPlugin())) {
                attach.setPermission(permission, true);
                return true;
            }
        }
        if (this.plugin == null) {
            return false;
        }
        PermissionAttachment attach = player.addAttachment(this.plugin);
        attach.setPermission(permission, true);
        return true;
    }

    /**
     * Adds world-specific transient permission to an offline player.
     *
     * @param worldName  World name
     * @param player     OfflinePlayer
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerAddTransient(@Nullable String worldName, OfflinePlayer player, String permission) {
        return playerAddTransient(player, permission);
    }

    /**
     * Adds world-specific transient permission to a player.
     *
     * @param worldName  World name
     * @param player     Player object
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerAddTransient(@Nullable String worldName, Player player, String permission) {
        return playerAddTransient(player, permission);
    }

    /**
     * Removes world-specific transient permission from an offline player.
     *
     * @param worldName  World name
     * @param player     OfflinePlayer
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerRemoveTransient(@Nullable String worldName, OfflinePlayer player, String permission) {
        return playerRemoveTransient(player, permission);
    }

    /**
     * Removes world-specific transient permission from a player.
     *
     * @param worldName  World name
     * @param player     Player object
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerRemoveTransient(@Nullable String worldName, Player player, String permission) {
        return playerRemoveTransient((OfflinePlayer) player, permission);
    }

    /**
     * Removes permission from a player.
     *
     * @param world      World name or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerRemove(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public abstract boolean playerRemove(@Nullable String world, String player, String permission);

    /**
     * Removes permission from a player.
     *
     * @param world      World name or null for global
     * @param player     OfflinePlayer
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerRemove(@Nullable String world, OfflinePlayer player, String permission) {
        if (player == null) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        return playerRemove(world, name, permission);
    }

    /**
     * Removes permission from a player.
     *
     * @param world      World object or null for global
     * @param player     Player name
     * @param permission Permission node
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerRemove(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean playerRemove(@Nullable World world, String player, String permission) {
        return playerRemove(world == null ? null : world.getName(), player, permission);
    }

    /**
     * Removes permission from a player on their current world.
     *
     * @param player     Player object
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerRemove(Player player, String permission) {
        if (player == null) {
            return false;
        }
        return playerRemove(player.getWorld().getName(), (OfflinePlayer) player, permission);
    }

    /**
     * Removes transient permission from an offline player.
     *
     * @param player     OfflinePlayer
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerRemoveTransient(OfflinePlayer player, String permission) {
        if (player != null && player.isOnline() && player.getPlayer() != null) {
            return playerRemoveTransient(player.getPlayer(), permission);
        }
        return false;
    }

    /**
     * Removes transient permission from a player.
     *
     * @param player     Player object
     * @param permission Permission node
     * @return true if successful
     */
    public boolean playerRemoveTransient(Player player, String permission) {
        if (player == null || permission == null) {
            return false;
        }
        for (PermissionAttachmentInfo paInfo : player.getEffectivePermissions()) {
            PermissionAttachment attach = paInfo.getAttachment();
            if (attach != null && this.plugin != null && this.plugin.equals(attach.getPlugin())) {
                attach.unsetPermission(permission);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a group has permission in the specified world.
     *
     * @param world      World name or null for global
     * @param group      Group name
     * @param permission Permission node
     * @return true if group has permission
     */
    public abstract boolean groupHas(@Nullable String world, String group, String permission);

    /**
     * Checks if a group has permission in the specified world.
     *
     * @param world      World object or null for global
     * @param group      Group name
     * @param permission Permission node
     * @return true if group has permission
     */
    public boolean groupHas(@Nullable World world, String group, String permission) {
        return groupHas(world == null ? null : world.getName(), group, permission);
    }

    /**
     * Adds permission to a group.
     *
     * @param world      World name or null for global
     * @param group      Group name
     * @param permission Permission node
     * @return true if successful
     */
    public abstract boolean groupAdd(@Nullable String world, String group, String permission);

    /**
     * Adds permission to a group.
     *
     * @param world      World object or null for global
     * @param group      Group name
     * @param permission Permission node
     * @return true if successful
     */
    public boolean groupAdd(@Nullable World world, String group, String permission) {
        return groupAdd(world == null ? null : world.getName(), group, permission);
    }

    /**
     * Removes permission from a group.
     *
     * @param world      World name or null for global
     * @param group      Group name
     * @param permission Permission node
     * @return true if successful
     */
    public abstract boolean groupRemove(@Nullable String world, String group, String permission);

    /**
     * Removes permission from a group.
     *
     * @param world      World object or null for global
     * @param group      Group name
     * @param permission Permission node
     * @return true if successful
     */
    public boolean groupRemove(@Nullable World world, String group, String permission) {
        return groupRemove(world == null ? null : world.getName(), group, permission);
    }

    /**
     * Checks if a player is in a group.
     *
     * @param world  World name or null for global
     * @param player Player name
     * @param group  Group name
     * @return true if player is in group
     * @deprecated As of VaultAPI 1.4 use {@link #playerInGroup(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public abstract boolean playerInGroup(@Nullable String world, String player, String group);

    /**
     * Checks if a player is in a group.
     *
     * @param world  World object or null for global
     * @param player Player name
     * @param group  Group name
     * @return true if player is in group
     * @deprecated As of VaultAPI 1.4 use {@link #playerInGroup(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean playerInGroup(@Nullable World world, String player, String group) {
        return playerInGroup(world == null ? null : world.getName(), player, group);
    }

    /**
     * Checks if an offline player is in a group.
     *
     * @param world  World name or null for global
     * @param player OfflinePlayer
     * @param group  Group name
     * @return true if player is in group
     */
    public boolean playerInGroup(@Nullable String world, OfflinePlayer player, String group) {
        if (player == null) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        return playerInGroup(world, name, group);
    }

    /**
     * Checks if a player is in a group on their current world.
     *
     * @param player Player object
     * @param group  Group name
     * @return true if player is in group
     */
    public boolean playerInGroup(Player player, String group) {
        if (player == null) {
            return false;
        }
        return playerInGroup(player.getWorld().getName(), (OfflinePlayer) player, group);
    }

    /**
     * Adds a player to a group.
     *
     * @param world  World name or null for global
     * @param player Player name
     * @param group  Group name
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerAddGroup(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public abstract boolean playerAddGroup(@Nullable String world, String player, String group);

    /**
     * Adds a player to a group.
     *
     * @param world  World object or null for global
     * @param player Player name
     * @param group  Group name
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerAddGroup(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean playerAddGroup(@Nullable World world, String player, String group) {
        return playerAddGroup(world == null ? null : world.getName(), player, group);
    }

    /**
     * Adds an offline player to a group.
     *
     * @param world  World name or null for global
     * @param player OfflinePlayer
     * @param group  Group name
     * @return true if successful
     */
    public boolean playerAddGroup(@Nullable String world, OfflinePlayer player, String group) {
        if (player == null) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        return playerAddGroup(world, name, group);
    }

    /**
     * Adds a player to a group on their current world.
     *
     * @param player Player object
     * @param group  Group name
     * @return true if successful
     */
    public boolean playerAddGroup(Player player, String group) {
        if (player == null) {
            return false;
        }
        return playerAddGroup(player.getWorld().getName(), (OfflinePlayer) player, group);
    }

    /**
     * Removes a player from a group.
     *
     * @param world  World name or null for global
     * @param player Player name
     * @param group  Group name
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerRemoveGroup(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public abstract boolean playerRemoveGroup(@Nullable String world, String player, String group);

    /**
     * Removes a player from a group.
     *
     * @param world  World object or null for global
     * @param player Player name
     * @param group  Group name
     * @return true if successful
     * @deprecated As of VaultAPI 1.4 use {@link #playerRemoveGroup(String, OfflinePlayer, String)} instead.
     */
    @Deprecated
    public boolean playerRemoveGroup(@Nullable World world, String player, String group) {
        return playerRemoveGroup(world == null ? null : world.getName(), player, group);
    }

    /**
     * Removes an offline player from a group.
     *
     * @param world  World name or null for global
     * @param player OfflinePlayer
     * @param group  Group name
     * @return true if successful
     */
    public boolean playerRemoveGroup(@Nullable String world, OfflinePlayer player, String group) {
        if (player == null) {
            return false;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return false;
        }
        return playerRemoveGroup(world, name, group);
    }

    /**
     * Removes a player from a group on their current world.
     *
     * @param player Player object
     * @param group  Group name
     * @return true if successful
     */
    public boolean playerRemoveGroup(Player player, String group) {
        if (player == null) {
            return false;
        }
        return playerRemoveGroup(player.getWorld().getName(), (OfflinePlayer) player, group);
    }

    /**
     * Gets list of groups the player is a member of.
     *
     * @param world  World name or null for global
     * @param player Player name
     * @return array of group names
     * @deprecated As of VaultAPI 1.4 use {@link #getPlayerGroups(String, OfflinePlayer)} instead.
     */
    @Deprecated
    public abstract String[] getPlayerGroups(@Nullable String world, String player);

    /**
     * Gets list of groups the player is a member of.
     *
     * @param world  World object or null for global
     * @param player Player name
     * @return array of group names
     * @deprecated As of VaultAPI 1.4 use {@link #getPlayerGroups(String, OfflinePlayer)} instead.
     */
    @Deprecated
    public String[] getPlayerGroups(@Nullable World world, String player) {
        return getPlayerGroups(world == null ? null : world.getName(), player);
    }

    /**
     * Gets list of groups the offline player is a member of.
     *
     * @param world  World name or null for global
     * @param player OfflinePlayer
     * @return array of group names
     */
    public String[] getPlayerGroups(@Nullable String world, OfflinePlayer player) {
        if (player == null) {
            return new String[0];
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return new String[0];
        }
        return getPlayerGroups(world, name);
    }

    /**
     * Gets list of groups the player is a member of on their current world.
     *
     * @param player Player object
     * @return array of group names
     */
    public String[] getPlayerGroups(Player player) {
        if (player == null) {
            return new String[0];
        }
        return getPlayerGroups(player.getWorld().getName(), (OfflinePlayer) player);
    }

    /**
     * Gets the primary group of a player.
     *
     * @param world  World name or null for global
     * @param player Player name
     * @return primary group name
     * @deprecated As of VaultAPI 1.4 use {@link #getPrimaryGroup(String, OfflinePlayer)} instead.
     */
    @Deprecated
    public abstract String getPrimaryGroup(@Nullable String world, String player);

    /**
     * Gets the primary group of a player.
     *
     * @param world  World object or null for global
     * @param player Player name
     * @return primary group name
     * @deprecated As of VaultAPI 1.4 use {@link #getPrimaryGroup(String, OfflinePlayer)} instead.
     */
    @Deprecated
    public String getPrimaryGroup(@Nullable World world, String player) {
        return getPrimaryGroup(world == null ? null : world.getName(), player);
    }

    /**
     * Gets the primary group of an offline player.
     *
     * @param world  World name or null for global
     * @param player OfflinePlayer
     * @return primary group name
     */
    public String getPrimaryGroup(@Nullable String world, OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        String name = player.getName();
        if (name == null || name.isBlank()) {
            return null;
        }
        return getPrimaryGroup(world, name);
    }

    /**
     * Gets the primary group of a player on their current world.
     *
     * @param player Player object
     * @return primary group name
     */
    public String getPrimaryGroup(Player player) {
        if (player == null) {
            return null;
        }
        return getPrimaryGroup(player.getWorld().getName(), (OfflinePlayer) player);
    }

    /**
     * Returns an array of all known group names.
     *
     * @return array of group names
     */
    public abstract String[] getGroups();

    /**
     * Returns true if the given implementation supports groups.
     *
     * @return true if groups are supported
     */
    public abstract boolean hasGroupSupport();
}
