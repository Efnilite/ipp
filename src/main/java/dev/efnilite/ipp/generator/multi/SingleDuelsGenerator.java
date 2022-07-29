package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.data.Score;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ip.util.config.Option;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.generator.single.PlusGenerator;
import dev.efnilite.vilib.util.Task;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleDuelsGenerator extends PlusGenerator {

    private DuelsGenerator owningGenerator;
    private int playerIndex;

    public SingleDuelsGenerator(@NotNull ParkourPlayer player, GeneratorOption... generatorOptions) {
        super(player.getSession(), generatorOptions);

        // setup menu
        menu = new SettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY, ParkourOption.STYLES);

        // set the task to an empty runnable
        // this avoids the incomplete joining setup error
        task = new BukkitRunnable() {
            @Override
            public void run() {

            }
        };

        // avoid not scheduled error by never running this runnable (lol)
        Task.create(IPP.getPlugin())
                .delay(Integer.MAX_VALUE)
                .execute(task)
                .run();
    }

    @Override
    public void updateScoreboard() {
        if (!Option.SCOREBOARD_ENABLED) {
            return;
        }

        if (!player.showScoreboard) {
            return;
        }

        // board can be null a few ticks after on player leave
        if (player.getBoard() == null) {
            return;
        }

        Leaderboard leaderboard = getGamemode().getLeaderboard();

        String title = Util.translate(player.getPlayer(), Option.SCOREBOARD_TITLE);
        List<String> lines = new ArrayList<>();

        Score top = null, rank = null;
        if (leaderboard != null) {

            // only get score at rank if lines contains variables
            if (Util.listContains(lines, "topscore", "topplayer")) {
                top = leaderboard.getScoreAtRank(1);
            }

            rank = leaderboard.get(player.getUUID());
        }

        // set generic score if score is not found
        top = top == null ? new Score("?", "?", "?", 0) : top;
        rank = rank == null ? new Score("?", "?", "?", 0) : rank;

        // get sorted leaderboard
        List<Map.Entry<ParkourPlayer, SingleDuelsGenerator>> sorted = owningGenerator.getLeaderboard();

        lines.add("");
        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<ParkourPlayer, SingleDuelsGenerator> entry = sorted.get(i);

            lines.add(Util.color(
                """
                <#0072B3>#%d <gray>%s <dark_gray>- <gray>%d
                """
            .formatted(i + 1, entry.getKey().getName(), entry.getValue().getScore())));
        }

        // update lines
        for (String line : Option.SCOREBOARD_LINES) {
            line = Util.translate(player.getPlayer(), line); // add support for PAPI placeholders in scoreboard

            lines.add(line
                    .replace("%score%", Integer.toString(score))
                    .replace("%time%", stopwatch.toString())
                    .replace("%highscore%", Integer.toString(rank.score()))
                    .replace("%topscore%", Integer.toString(top.score()))
                    .replace("%topplayer%", top.name()).replace("%session%", getSession().getSessionId()));
        }

        player.getBoard().updateTitle(title
                .replace("%score%", Integer.toString(score))
                .replace("%time%", stopwatch.toString())
                .replace("%highscore%", Integer.toString(rank.score()))
                .replace("%topscore%", Integer.toString(top.score()))
                .replace("%topplayer%", top.name()).replace("%session%", getSession().getSessionId()));
        player.getBoard().updateLines(lines);
    }

    @Override
    public void reset(boolean regenerate) {
        super.reset(regenerate);

        if (!regenerate) {
            owningGenerator.removePlayer(player);
        }
    }

    public void setOwningGenerator(DuelsGenerator owningGenerator) {
        this.owningGenerator = owningGenerator;
    }

    public DuelsGenerator getOwningGenerator() {
        return owningGenerator;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    @Override
    public BlockData selectBlockData() {
        return switch (playerIndex) {
            case 0 -> Material.BLUE_CONCRETE.createBlockData();
            case 1 -> Material.RED_CONCRETE.createBlockData();
            case 2 -> Material.GREEN_CONCRETE.createBlockData();
            case 3 -> Material.YELLOW_CONCRETE.createBlockData();
            default -> Material.STONE.createBlockData();
        };
    }

    public void stopGenerator() {
        this.stopped = true;
    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }
}
