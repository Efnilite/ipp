package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.player.data.Score;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.vilib.util.Strings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Fastest to  100 score
 */
public final class TimeTrialGenerator extends PlusGenerator {

    // the colour gradients used
    private final static String[] COLOUR_GRADIENTS = new String[] {
            "#03a600", "#06a300", "#0a9f00", "#0d9c00", "#119800", "#149500", "#179200", "#1b8e00", "#1e8b00", "#218700",
            "#258400", "#288100", "#2c7d00", "#2f7a00", "#327600", "#367300", "#397000", "#3d6c00", "#406900", "#436500",
            "#476200", "#4a5f00", "#4e5b00", "#515800", "#555400", "#585100", "#5b4e00", "#5f4a00", "#624700", "#664300",
            "#694000", "#6c3d00", "#703900", "#733600", "#773200", "#7a2f00", "#7d2c00", "#812800", "#842500", "#882100",
            "#8b1e00", "#8e1b00", "#921700", "#951400", "#991000", "#9c0d00", "#9f0a00", "#a30600", "#a60300", "#aa0000"
    };

    private final int goal = IPP.getConfiguration().getFile("config").getInt("gamemodes." + getGamemode().getName().toLowerCase() + ".goal");

    public TimeTrialGenerator(Session session) {
        // setup settings for generation
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.DISABLE_ADAPTIVE);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATIC, ParkourOption.SCORE_DIFFICULTY, ParkourOption.SPECIAL_BLOCKS);

        player.player.resetTitle();
    }

    @Override
    public void tick() {
        super.tick();

        // Display score to player
        StringBuilder bar = new StringBuilder(); // build bar with score remaining
        for (int i = 0; i < goal; i++) {
            if (i % 2 == 0) { // !! made for 100 score
                if (i >= score) {
                    bar.append("<dark_gray><bold>");
                } else {
                    bar.append("<").append(COLOUR_GRADIENTS[i / 2]).append(">").append("<bold>");
                }
                bar.append("|");
            }
        }
        player.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(Strings.colour(bar + " <red><bold>| <dark_gray>" + stopwatch.toString())));
    }

    @Override
    public void score() {
        score++;
        totalScore++;

        if (score >= goal) {
            score = goal;

            getGamemode().getLeaderboard().put(player.getUUID(),
                    new Score(player.getName(), stopwatch.toString(), player.calculateDifficultyScore(), score));

            player.teleport(player.getLocation().subtract(0, 15, 0));
        }
    }

    @Override
    protected void registerScore() {

    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.TIME_TRIAL;
    }
}