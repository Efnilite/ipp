package dev.efnilite.ipp.session;

import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.mode.Modes;
import dev.efnilite.ip.mode.MultiMode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.config.PlusLocales;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A Session for multiple players.
 *
 * @author Efnilite
 */
public class MultiSession extends Session {

    public int maxPlayers;

    public static MultiSession create(@NotNull ParkourPlayer player, @NotNull Mode gamemode) {
        // create session
        MultiSession session = new MultiSession();
        session.addPlayers(player);
        session.setGamemode(gamemode); // todo
        session.isAcceptingPlayers = s -> s.getPlayers().size() < ((MultiSession) s).maxPlayers;

        return session;
    }

    @Override
    public void join(Player player) {
        if (isAcceptingPlayers()) {
            MultiMode mode = (MultiMode) getMode();
            mode.join(player, this);
        } else if (isAcceptingSpectators) {
            Modes.SPECTATOR.create(player, this);
        }
    }

    @Override
    public void addPlayers(ParkourPlayer... players) {
        for (ParkourPlayer player : players) {
            for (ParkourPlayer to : getPlayers()) {
                to.send(PlusLocales.getString(player.locale, "play.multi.other_join", false).formatted(player.getName()));
            }
        }

        super.addPlayers(players);
    }

    @Override
    public void removePlayers(ParkourPlayer... players) {
        super.removePlayers(players);

        for (ParkourPlayer player : players) {
            for (ParkourPlayer to : getPlayers()) {
                to.send(PlusLocales.getString(player.locale, "play.multi.other_leave", false).formatted(player.getName()));
            }
        }
    }
}
