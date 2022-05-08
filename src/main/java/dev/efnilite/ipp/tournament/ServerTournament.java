package dev.efnilite.ipp.tournament;

import dev.efnilite.ip.player.data.Score;
import dev.efnilite.ip.session.Tournament;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of a tournament
 * todo
 */
public class ServerTournament implements Tournament {

    private final String mode;
    private final Map<UUID, Score> scores = new HashMap<>();

    public ServerTournament(String mode) {
        this.mode = mode;
    }

    @Override
    public void addScore(UUID uuid, Score score) {
        scores.put(uuid, score);
    }
}
