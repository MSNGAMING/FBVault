package net.milkbowl.vault.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;

/**
 * Modern formatting helper for MiniMessage and Adventure Components with legacy fallback.
 */
public final class VaultText {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    private VaultText() {
    }

    /**
     * Parses a MiniMessage string into an Adventure Component.
     *
     * @param message MiniMessage format string
     * @return Adventure Component
     */
    public static Component parse(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(message);
    }

    /**
     * Sends a MiniMessage formatted message to the given CommandSender.
     *
     * @param sender  recipient
     * @param message MiniMessage string
     */
    public static void send(CommandSender sender, String message) {
        if (sender == null || message == null || message.isEmpty()) {
            return;
        }
        sender.sendMessage(parse(message));
    }

    /**
     * Converts a MiniMessage string to legacy color-coded string for backward compatibility.
     *
     * @param message MiniMessage string
     * @return legacy section-coded string
     */
    public static String toLegacy(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }
        return LEGACY_SERIALIZER.serialize(parse(message));
    }
}
