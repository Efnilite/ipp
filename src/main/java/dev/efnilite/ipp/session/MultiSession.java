package dev.efnilite.ipp.session;

import com.google.common.annotations.Beta;
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
@Beta
public class MultiSession extends SingleSession {

    private int maxPlayers;

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
        System.out.println("join call");
        if (isAcceptingPlayers()) {
            System.out.println("is accepting players");
            MultiGamemode gamemode = (MultiGamemode) getGamemode();
            gamemode.join(player, this);
        } else if (isAcceptingSpectators()) {
            Gamemodes.SPECTATOR.create(player, this);
        }
    }

    @Override
    public boolean isAcceptingPlayers() {
        return getPlayers().size() < maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
