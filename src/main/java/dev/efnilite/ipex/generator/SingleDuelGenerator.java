package dev.efnilite.ipex.generator;

import dev.efnilite.witp.generator.DefaultGenerator;
import dev.efnilite.witp.generator.base.GeneratorOption;
import dev.efnilite.witp.player.ParkourPlayer;
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
        switch (playerIndex) {
            case 1:
                return Material.BLUE_CONCRETE.createBlockData();
            case 2:
                return Material.RED_CONCRETE.createBlockData();
            case 3:
                return Material.GREEN_CONCRETE.createBlockData();
            case 4:
                return Material.YELLOW_CONCRETE.createBlockData();
        }
        return Material.STONE.createBlockData();
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
