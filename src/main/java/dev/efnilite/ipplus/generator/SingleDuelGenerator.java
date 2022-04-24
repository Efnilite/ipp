package dev.efnilite.ipplus.generator;

import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class SingleDuelGenerator extends DefaultGenerator {

    private DuelGenerator owningGenerator;
    private int playerIndex;

    public SingleDuelGenerator(@NotNull ParkourPlayer player, GeneratorOption... generatorOptions) {
        super(player, generatorOptions);
    }

    public void setOwningGenerator(DuelGenerator owningGenerator) {
        this.owningGenerator = owningGenerator;
    }

    public DuelGenerator getOwningGenerator() {
        return owningGenerator;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    @Override
    public BlockData selectBlockData() {
        return switch (playerIndex) {
            case 1 -> Material.BLUE_CONCRETE.createBlockData();
            case 2 -> Material.RED_CONCRETE.createBlockData();
            case 3 -> Material.GREEN_CONCRETE.createBlockData();
            case 4 -> Material.YELLOW_CONCRETE.createBlockData();
            default -> Material.STONE.createBlockData();
        };
    }

    public void stopGenerator() {
        this.stopped = true;
    }

    public void setPlayerSpawn(Location spawn) {
        this.playerSpawn = spawn;
        player.teleport(spawn);
    }

    public void setBlockSpawn(Location spawn) {
        this.blockSpawn = spawn;
    }

    @Override
    public void menu() {

    }

    @Override
    public void score() {
        this.score++;
        this.totalScore++;
    }
}
