package dev.efnilite.ipp.session;

import dev.efnilite.ip.api.Gamemodes;
import dev.efnilite.ip.api.MultiGamemode;
import dev.efnilite.ip.player.ParkourPlayer;
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
