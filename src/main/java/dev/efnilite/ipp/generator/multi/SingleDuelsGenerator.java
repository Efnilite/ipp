package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.lib.vilib.util.Colls;
import dev.efnilite.ip.lib.vilib.util.Task;
import dev.efnilite.ip.menu.ParkourOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.mode.Mode;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.generator.single.PlusGenerator;
import dev.efnilite.ipp.mode.PlusMode;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleDuelsGenerator extends PlusGenerator {

    private static final List<Material> MATERIALS = List.of(
            Material.LIGHT_BLUE_CONCRETE, Material.RED_CONCRETE, Material.LIME_CONCRETE, Material.YELLOW_CONCRETE,
            Material.ORANGE_CONCRETE, Material.BLUE_CONCRETE, Material.MAGENTA_CONCRETE, Material.WHITE_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE, Material.BLACK_CONCRETE, Material.GREEN_CONCRETE, Material.BROWN_CONCRETE,
            Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.GRAY_CONCRETE, Material.PINK_CONCRETE);
    public DuelsGenerator owningGenerator;
    private BlockData blockData;

    public SingleDuelsGenerator(@NotNull Session session) {
        super(session, GeneratorOption.DISABLE_SCHEMATICS);

        player.updateGeneratorSettings(this);

        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATICS, ParkourOption.STYLES, ParkourOption.SPECIAL_BLOCKS);

        // avoids incomplete joining setup error
        task = Task.create(IPP.getPlugin())
                .delay(1)
                .execute(new BukkitRunnable() {
                    @Override
                    public void run() {

                    }
                })
                .run();
    }

    public void setPlayerIndex(int playerIndex) {
        blockData = switch (playerIndex) {
            // follow set pattern
            case 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 -> MATERIALS.get(playerIndex).createBlockData();

            // if there are more players, just get a random one
            default -> Colls.random(MATERIALS).createBlockData();
        };
    }

    @Override
    protected void registerScore(String time, String difficulty, int score) {
        if (score < owningGenerator.goal) {
            return;
        }

        this.score = owningGenerator.goal;

        owningGenerator.win(player);
    }

    @Override
    protected void score() {
        super.score();

        registerScore(getDetailedTime(), Double.toString(getDifficultyScore()), score);
    }

    @Override
    public BlockData selectBlockData() {
        return blockData;
    }

    @Override
    public Mode getMode() {
        return PlusMode.DUELS;
    }

    @Override
    public @NotNull List<ParkourPlayer> getPlayers() {
        return List.of(player);
    }
}
