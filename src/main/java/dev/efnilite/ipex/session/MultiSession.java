package dev.efnilite.ipex.session;

import com.google.common.annotations.Beta;
import dev.efnilite.witp.session.SingleSession;

/**
 * A Session for multiple players.
 *
 * @author Efnilite
 */
@Beta
public class MultiSession extends SingleSession {

    private int maxPlayers;

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
