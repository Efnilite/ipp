package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.config.Locales;
import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.leaderboard.Leaderboard;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.data.Score;
import dev.efnilite.ip.util.Util;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.generator.single.PlusGenerator;
import dev.efnilite.ipp.session.MultiSession;
import dev.efnilite.vilib.util.Numbers;
import dev.efnilite.vilib.util.Strings;
import dev.efnilite.vilib.util.Task;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleDuelsGenerator extends PlusGenerator {

    private static final List<Material> MATERIALS = List.of(
            Material.LIGHT_BLUE_CONCRETE, Material.RED_CONCRETE, Material.LIME_CONCRETE, Material.YELLOW_CONCRETE,
            Material.ORANGE_CONCRETE, Material.BLUE_CONCRETE, Material.MAGENTA_CONCRETE, Material.WHITE_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE, Material.BLACK_CONCRETE, Material.GREEN_CONCRETE, Material.BROWN_CONCRETE,
            Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.GRAY_CONCRETE, Material.PINK_CONCRETE);

    private BlockData blockData;
    public DuelsGenerator owningGenerator;

    public SingleDuelsGenerator(@NotNull MultiSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATIC, ParkourOption.SCORE_DIFFICULTY,
                ParkourOption.STYLES, ParkourOption.SPECIAL_BLOCKS);

        // set the task to an empty runnable
        // this avoids the incomplete joining setup error
        task = Task.create(IPP.getPlugin())
                .delay(1)
                .execute(new BukkitRunnable() {
                    @Override
                    public void run() {

                    }
                })
                .run();
    }

    @Override
    public void updateScoreboard() {
        if (player == null || player.board == null || player.board.isDeleted()) {
            return;
        }

        if (!(boolean) Option.OPTIONS_DEFAULTS.get(ParkourOption.SCOREBOARD)) {
            return;
        }

        if (!player.showScoreboard) {
            return;
        }

        Leaderboard leaderboard = getGamemode().getLeaderboard();

        String title = Util.translate(player.player, Locales.getString(player.getLocale(), "scoreboard.title", true));
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

            lines.add(Strings.colour(
                """
                <#FF6464>#%d <gray>%s <dark_gray>- <gray>%d
                """
            .formatted(i + 1, entry.getKey().getName(), entry.getValue().getScore())));
        }

        // update lines
        for (String line : Locales.getStringList(player.getLocale(), "scoreboard.lines", true)) {
            line = Util.translate(player.player, line); // add support for PAPI placeholders in scoreboard

            lines.add(line
                    .replace("%score%", Integer.toString(score))
                    .replace("%time%", stopwatch.toString())
                    .replace("%highscore%", Integer.toString(rank.score()))
                    .replace("%topscore%", Integer.toString(top.score()))
                    .replace("%topplayer%", top.name()).replace("%session%", getSession().getSessionId()));
        }

        player.board.updateTitle(title
                .replace("%score%", Integer.toString(score))
                .replace("%time%", stopwatch.toString())
                .replace("%highscore%", Integer.toString(rank.score()))
                .replace("%topscore%", Integer.toString(top.score()))
                .replace("%topplayer%", top.name()).replace("%session%", getSession().getSessionId()));
        player.board.updateLines(lines);
    }

    public void setPlayerIndex(int playerIndex) {
        blockData = switch (playerIndex) {
            // follow set pattern
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 -> MATERIALS.get(playerIndex).createBlockData();

            // if there are more players, just get a random one
            default -> MATERIALS.get(Numbers.random(0, MATERIALS.size())).createBlockData();
        };
    }

    @Override
    public BlockData selectBlockData() {
        return blockData;
    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.DUELS;
    }

    @Override
    protected void registerScore() {

    }
}
