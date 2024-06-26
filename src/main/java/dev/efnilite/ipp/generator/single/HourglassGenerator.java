package dev.efnilite.ipp.generator.single;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.lib.vilib.util.Strings;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.config.PlusConfigOption;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.Location;
import org.bukkit.Material;

import java.time.Duration;
import java.time.Instant;

/**
 * Class for only having 1 second to stay on the block
 */
public final class HourglassGenerator extends PlusGenerator {

    // https://colordesigner.io/gradient-generator
    private final static String[] GRADIENT_COLOURS = new String[]{
            "#00aa00", "#31a400", "#469f00", "#559900", "#619300", "#6c8c00", "#758600", "#7d7f00", "#857800", "#8c7100",
            "#926a00", "#976200", "#9c5a00", "#a05100", "#a34800", "#a63f00", "#a83400", "#a92800", "#aa1a00", "#aa0000"
    };

    /**
     * The current countdown
     */
    private Instant countdown;

    /**
     * The block that will be set to air at the end of the countdown.
     */
    private Location countdownLocation;

    public HourglassGenerator(Session session) {
        super(session, GeneratorOption.DISABLE_SCHEMATICS);

        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATICS);
    }

    @Override
    public void tick() {
        super.tick();

        if (score == 0 || countdown == null) {
            return;
        }

        Duration delta = Duration.between(countdown, Instant.now()).abs();
        StringBuilder bar = new StringBuilder(); // build bar with time remaining

        int increments = (PlusConfigOption.HOURGLASS_TIME / 20);
        int time = Math.min((int) delta.toMillis() / increments, 20);

        bar.append("<").append(GRADIENT_COLOURS[time > 0 ? time - 1 : time]).append(">").append("<bold>");

        for (int i = 20; i > 0; i--) { // countDOWN
            if (i == time) {
                break;
            }
            bar.append("|");
        }

        player.player.sendTitle(Strings.colour("<reset>"), Strings.colour(bar.toString()), 0, 10, 0);

        if (delta.toMillis() < PlusConfigOption.HOURGLASS_TIME) {
            return;
        }

        countdownLocation.getBlock().setType(Material.AIR);
        countdown = null;
    }

    @Override
    public void score() {
        super.score();

        // on score cooldown should start
        countdown = Instant.now();
        countdownLocation = player.getLocation().clone().subtract(0, 1, 0).clone();
        if (countdownLocation.getBlock().getType() == Material.AIR) {
            countdownLocation.subtract(0, 0.5, 0);
        }
    }

    @Override
    public Mode getMode() {
        return PlusMode.HOURGLASS;
    }
}
