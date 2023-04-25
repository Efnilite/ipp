package dev.efnilite.ipp.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for handling cooldowns.
 */
public class Cooldowns {

    private static final Map<String, Long> EXECUTION_TIMES = new HashMap<>();

    /**
     * Returns whether the provided action {@code key} can be performed with the provided cooldown {@code cooldown}.
     * <strong>This method is only recommended to be used with global actions</strong>, e.g. requiring no player.
     * Returns:
     * <ul>
     *     <li>{@code true} - When the action with the provided key can be performed.
     *     The last execution was more than {@code cooldown} milliseconds ago.</li>
     *     <li>{@code false} - When the action with the provided key cannot be performed.
     *     The last execution was less than {@code cooldown} milliseconds ago.</li>
     * </ul>
     *
     * @param key      The key by which the system can differentiate actions.
     * @param cooldown The cooldown in milliseconds.
     * @return true if the action {@code key} was more than {@code cooldown} ms ago, false if not.
     */
    public static boolean canPerform(@NotNull String key, long cooldown) {
        Objects.requireNonNull(key);

        Long lastTime = EXECUTION_TIMES.get(key);

        // if no value has been previously registered, player is executing for the first time, so return true
        if (lastTime == null) {
            EXECUTION_TIMES.put(key, System.currentTimeMillis());
            return true;
        }

        // get the difference between now and the last time the command was executed
        long dt = System.currentTimeMillis() - lastTime;

        // if the last time execution difference is higher than cooldown, allow execution
        if (dt > cooldown) {
            EXECUTION_TIMES.put(key, System.currentTimeMillis());
            return true;
        }

        return false;
    }

    /**
     * Returns whether the provided action {@code key}, belonging to player {@code player},  can be performed
     * with the provided cooldown {@code cooldown}. Returns:
     * <ul>
     *     <li>{@code true} - When the action with the provided player's key can be performed.
     *     The last execution was more than {@code cooldown} milliseconds ago.</li>
     *     <li>{@code false} - When the action with the provided player's key cannot be performed.
     *     The last execution was less than {@code cooldown} milliseconds ago.</li>
     * </ul>
     *
     * @param key      The key by which the system can differentiate actions.
     * @param cooldown The cooldown in milliseconds.
     * @return true if the action {@code key} was more than {@code cooldown} ms ago, false if not.
     */
    public static boolean canPerform(@NotNull Player player, @NotNull String key, long cooldown) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(key);

        return canPerform(player.getUniqueId() + "-" + key, cooldown);
    }

}