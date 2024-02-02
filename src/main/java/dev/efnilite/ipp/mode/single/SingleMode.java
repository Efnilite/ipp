package dev.efnilite.ipp.mode.single;

import dev.efnilite.ip.generator.ParkourGenerator;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.vilib.inventory.item.Item;
import dev.efnilite.ipp.config.PlusLocales;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class SingleMode implements Mode {

    @Override
    public @NotNull Item getItem(String locale) {
        return PlusLocales.getItem(locale, "play.single.%s".formatted(getName()));
    }

    /**
     * Avoids repetition in creating single modes.
     *
     * @param player    The player.
     * @param generator The generator function.
     */
    protected void create(Player player, Function<Session, ParkourGenerator> generator) {
        ParkourPlayer pp = ParkourPlayer.getPlayer(player);
        if (pp != null && pp.session.generator.getMode().getName().equals(getName())) {
            return;
        }
        player.closeInventory();

        Session.create(generator, null, null, player);
    }
}