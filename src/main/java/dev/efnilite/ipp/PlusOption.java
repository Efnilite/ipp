package dev.efnilite.ipp;

import dev.efnilite.ip.config.Option;
import org.bukkit.permissions.Permissible;

/**
 * An enum for all Parkour Menu Options
 */
public enum PlusOption {

    // play
    MULTIPLAYER("multiplayer", "ip.multiplayer"),

    // community
    ACTIVE("active", "ip.active"),

    // settings
    PRACTICE_SETTINGS("practice_settings", "ip.settings.practice_settings"),

    // lobby
    INVITE("invite", "ip.invite");

    // other


    /**
     * The name of the option
     */
    private final String name;

    /**
     * The permission required to change this option
     */
    private final String permission;

    PlusOption(String name, String permission) {
        this.name = name;
        this.permission = permission;
    }

    /**
     * Checks if a player has the current permission if permissions are enabled.
     * If perms are disabled, always returns true.
     *
     * @param   permissible
     *          The permissible
     *
     * @return true if the player is allowed to perform this action, false if not
     */
    public boolean check(Permissible permissible) {
        if (Option.PERMISSIONS) {
            return permissible.hasPermission(permission);
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }
}
