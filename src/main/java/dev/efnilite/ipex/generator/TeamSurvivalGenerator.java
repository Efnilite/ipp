package dev.efnilite.ipex.generator;

import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.player.ParkourPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for multiplayer
 */
public final class TeamSurvivalGenerator extends DefaultGenerator {

    private Location lastBlock;
    private ParkourPlayer owner;
    private final List<ParkourPlayer> players;

    public TeamSurvivalGenerator(ParkourPlayer player) {
        super(player);
        this.owner = player;
        players = new ArrayList<>();
    }

    public void setOwner(ParkourPlayer owner) {
        this.owner = owner;
    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }

    public TeamSurvivalGenerator addPlayers(ParkourPlayer... players) {
        this.players.addAll(Arrays.asList(players));
        return this;
    }
}
