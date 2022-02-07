package dev.efnilite.ipex.generator;

import dev.efnilite.fycore.util.colour.Colours;
import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.generator.base.GeneratorOption;
import dev.efnilite.witp.player.ParkourPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Fastest to  100 score
 */
public final class TimeTrialGenerator extends DefaultGenerator {

    private final int goal = 100;

    public TimeTrialGenerator(ParkourPlayer player) {
        super(player, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.DISABLE_SPECIAL, GeneratorOption.DISABLE_ADAPTIVE);

        player.getPlayer().resetTitle();
    }

    @Override
    public void tick() {
        super.tick();

        // Display score to player
        StringBuilder bar = new StringBuilder(); // build bar with score remaining
        bar.append("&2&l");
        for (int i = 0; i < goal; i++) {
            if (i == score) {
                bar.append("&8&l");
            }
            if (i % 2 == 0) { // !! made for 100 score
                bar.append("|");
            }
        }
        player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Colours.colour(bar + " &4&l| &8" + time)));
    }

    @Override
    public void score() {
        score++;
        totalScore++;

        if (score >= goal) {
            score = goal;
            reset(true);
        }
    }

    @Override
    public void menu() {
        super.handler.menu("structure", "difficulty", "special");
    }
}