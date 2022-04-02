package dev.efnilite.ipex.generator;

import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.player.ParkourPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for multiplayer
 */
public final class TeamSurvivalGenerator extends DefaultGenerator {

    private Location lastBlock;
    private final List<ParkourPlayer> players;

    public TeamSurvivalGenerator(ParkourPlayer player) {
        super(player);
        players = new ArrayList<>();
    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }
}
