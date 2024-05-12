package dev.efnilite.ipp;

import dev.efnilite.ip.config.Config;
import org.bukkit.permissions.Permissible;

/**
 * An enum for all Parkour Menu Options
 */
public enum PlusOption {

    MULTIPLAYER("ip.multiplayer"),
    ACTIVE("ip.active"),
    PRACTICE_SETTINGS("ip.settings.practice_settings"),
    INVITE("ip.invite");

    /**
     * The permission required to change this option
     */
    private final String permission;

    PlusOption(String permission) {
        this.permission = permission;
    }

    /**
     * Checks if a player has the current permission if permissions are enabled.
     * If perms are disabled, always returns true.
     *
     * @param permissible The permissible
     * @return true if the player is allowed to perform this action, false if not
     */
    public boolean mayPerform(Permissible permissible) {
        if (Config.CONFIG.getBoolean("permissions.enabled")) {
            return permissible.hasPermission(permission);
        }
        return true;
    }
}
