package dev.efnilite.ipp.session;

import dev.efnilite.ip.api.Gamemodes;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.ParkourSpectator;
import dev.efnilite.ip.session.SingleSession;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A Session for multiple players.
 *
 * @author Efnilite
 */
public class MultiSession extends SingleSession {

    private int maxPlayers;

    private Boolean isAcceptingPlayers;

    public static MultiSession create(@NotNull ParkourPlayer player, @NotNull MultiGamemode gamemode) {
        // create session
        MultiSession session = new MultiSession();
        session.addPlayers(player);
        session.register();

        session.setGamemode(gamemode);

        return session;
    }

    @Override
    public void join(Player player) {
        if (isAcceptingPlayers()) {
            MultiGamemode gamemode = (MultiGamemode) getGamemode();
            gamemode.join(player, this);
        } else if (isAcceptingSpectators()) {
            Gamemodes.SPECTATOR.create(player, this);
        }
    }

    @Override
    public void addPlayers(ParkourPlayer... players) {
        super.addPlayers(players);

        for (ParkourPlayer joined : players) {
            for (ParkourPlayer player : getPlayers()) {
                player.sendTranslated("play.multi.other_join", joined.getName());
            }
        }
    }

    @Override
    public void removePlayers(ParkourPlayer... players) {
        super.removePlayers(players);

        for (ParkourPlayer joined : players) {
            for (ParkourPlayer player : getPlayers()) {
                player.sendTranslated("play.multi.other_leave", joined.getName());
            }
        }
    }

    public void setAcceptingPlayers(boolean acceptingPlayers) {
        isAcceptingPlayers = acceptingPlayers;
    }

    @Override
    public boolean isAcceptingPlayers() {
        if (!isAcceptingPlayers) {
            return false;
        }

        return getPlayers().size() < maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
