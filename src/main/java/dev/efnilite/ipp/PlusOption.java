package dev.efnilite.ipp;

import dev.efnilite.ip.util.config.Option;
import org.bukkit.entity.Player;

/**
 * An enum for all Parkour Menu Options
 */
public enum PlusOption {

    MULTIPLAYER("multiplayer", "witp.multiplayer");

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
     * @param   player
     *          The player
     *
     * @return true if the player is allowed to perform this action, false if not
     */
    public boolean check(Player player) {
        return !Option.PERMISSIONS.get() || player.hasPermission(permission);
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }
}
