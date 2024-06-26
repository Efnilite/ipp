package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.leaderboard.Score;
import dev.efnilite.ip.lib.vilib.util.Strings;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.mode.PlusMode;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Fastest to  100 score
 */
public final class TimeTrialGenerator extends PlusGenerator {

    // the colour gradients used
    private final static String[] COLOUR_GRADIENTS = new String[]{
            "#03a600", "#06a300", "#0a9f00", "#0d9c00", "#119800", "#149500", "#179200", "#1b8e00", "#1e8b00", "#218700",
            "#258400", "#288100", "#2c7d00", "#2f7a00", "#327600", "#367300", "#397000", "#3d6c00", "#406900", "#436500",
            "#476200", "#4a5f00", "#4e5b00", "#515800", "#555400", "#585100", "#5b4e00", "#5f4a00", "#624700", "#664300",
            "#694000", "#6c3d00", "#703900", "#733600", "#773200", "#7a2f00", "#7d2c00", "#812800", "#842500", "#882100",
            "#8b1e00", "#8e1b00", "#921700", "#951400", "#991000", "#9c0d00", "#9f0a00", "#a30600", "#a60300", "#aa0000"
    };

    private final int goal = IPP.getConfiguration().getFile("config").getInt("gamemodes.%s.goal".formatted(getMode().getName().toLowerCase()));
    private final int interval = goal / COLOUR_GRADIENTS.length;

    public TimeTrialGenerator(Session session) {
        // setup settings for generation
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL);

        // setup menu
        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.SPECIAL_BLOCKS);

        player.player.resetTitle();
    }

    @Override
    public void overrideProfile() {
        profile.set("blockLead", "10");
    }

    @Override
    public void tick() {
        super.tick();

        // Display score to player
        StringBuilder bar = new StringBuilder(); // build bar with score remaining
        for (int i = 0; i < goal; i++) {
            if (i % interval == 0) {
                if (i >= score) {
                    bar.append("<reset><dark_gray>");
                } else {
                    bar.append("<bold><").append(COLOUR_GRADIENTS[i / interval]).append(">");
                }
                bar.append("|");
            }
        }
        player.player.sendActionBar(TextComponent.fromLegacyText(Strings.colour("%s <red><bold>| <reset>%s".formatted(bar, getFormattedTime()))));
    }

    @Override
    protected void registerScore(String time, String difficulty, int score) {
        if (score < goal) {
            return;
        }

        score = goal;

        getMode().getLeaderboard().put(player.getUUID(), new Score(player.getName(), time, difficulty, score));

        player.teleport(player.getLocation().subtract(0, 15, 0));
    }

    @Override
    protected void score() {
        super.score();

        registerScore(getDetailedTime(), Double.toString(getDifficultyScore()), score);
    }

    @Override
    public Mode getMode() {
        return PlusMode.TIME_TRIAL;
    }
}