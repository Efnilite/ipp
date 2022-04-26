package dev.efnilite.ipp.session;

import com.google.common.annotations.Beta;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.session.SingleSession;
import org.jetbrains.annotations.NotNull;

/**
 * A Session for multiple players.
 *
 * @author Efnilite
 */
@Beta
public class MultiSession extends SingleSession {

    private int maxPlayers;

    public static Session create(@NotNull ParkourPlayer player) {
        // create session
        Session session = new MultiSession();
        session.addPlayers(player);
        session.register();

        // set session id for player
        player.setSessionId(session.getSessionId());

        return session;
    }

    @Override
    public boolean isAcceptingPlayers() {
        return true;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
