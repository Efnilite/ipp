package dev.efnilite.ipp.generator;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.menu.SettingsMenu;
import dev.efnilite.ip.session.Session;
import dev.efnilite.vilib.chat.ChatColour;
import dev.efnilite.vilib.chat.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.time.Duration;
import java.time.Instant;

/**
 * Class for only having 1 second to stay on the block
 */
public final class HourglassGenerator extends DefaultGenerator {

    // https://colordesigner.io/gradient-generator
    private final static String[] GRADIENT_COLOURS = new String[] {
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
        super(session, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.INCREASED_TICK_ACCURACY); // to increase smoothness of countdown
    }

    @Override
    public void tick() {
        super.tick();

        if (score == 0 || countdown == null) {
            return;
        }

        Duration delta = Duration.between(Instant.now(), countdown).abs();
        StringBuilder bar = new StringBuilder(); // build bar with time remaining

        int time = (int) delta.toMillis() / 50;
        if (time > 20) {
            time = 20;
        }

        bar.append(ChatColor.of(GRADIENT_COLOURS[time > 0 ? time - 1 : time])).append("<bold>");

        for (int i = 20; i > 0; i--) { // countDOWN
            if (i == time) {
                break;
            }
            bar.append("|");
        }

        player.getPlayer().sendTitle(Message.parseFormatting("&r"), Message.parseFormatting(bar.toString()), 0, 10, 0);

//        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
//                new TextComponent(Message.parseFormatting(bar.toString())));

        if (delta.getSeconds() < 1) {
            return;
        }

        countdownLocation.getBlock().setType(Material.AIR);
    }

    @Override
    public void score() {
        super.score();

        // on score cooldown should start
        countdown = Instant.now();
        countdownLocation = lastStandingPlayerLocation.clone().subtract(0, 1, 0).clone();
    }

    @Override
    public void menu() {
        SettingsMenu.open(player, ParkourOption.SCHEMATICS, ParkourOption.SCORE_DIFFICULTY);
    }
}
