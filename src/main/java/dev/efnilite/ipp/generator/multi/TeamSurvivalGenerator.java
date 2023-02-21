package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.ParkourOption;
import dev.efnilite.ip.api.Gamemode;
import dev.efnilite.ip.generator.settings.GeneratorOption;
import dev.efnilite.ip.menu.settings.ParkourSettingsMenu;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ip.player.data.Score;
import dev.efnilite.ipp.gamemode.PlusGamemodes;
import dev.efnilite.ipp.session.MultiSession;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Generator for the Team Survival gamemode.
 */
public final class TeamSurvivalGenerator extends MultiplayerGenerator {

    // where every player is
    private final Map<ParkourPlayer, Block> lastPlayerBlockMap = new HashMap<>();

    public TeamSurvivalGenerator(MultiSession session) {
        super(session, GeneratorOption.DISABLE_ADAPTIVE, GeneratorOption.DISABLE_SCHEMATICS, GeneratorOption.IGNORE_CHECK_FOR_PROGRESS);

        menu = new ParkourSettingsMenu(ParkourOption.SCHEMATIC);
    }

    @Override
    public void updatePreferences() {
        profile.setSetting("useSchematic", "false");
    }

    @Override
    public void updateScoreboard() {
        super.updateScoreboard();

        for (ParkourPlayer pp : getPlayers()) {
            if (pp == player || player.board == null || pp.board == null) {
                continue;
            }

            pp.board.updateTitle(player.board.getTitle());
            pp.board.updateLines(player.board.getLines());
        }
    }

    @Override
    protected void registerScore() {
        for (ParkourPlayer pp : getPlayers()) {
            getGamemode().getLeaderboard().put(pp.getUUID(),
                    new Score(pp.getName(), stopwatch.toString(), player.calculateDifficultyScore(), score));
        }
    }

    @Override
    public void tick() {
        if (stopped || session == null || getPlayers().size() == 0) {
            return;
        }

        // the index of the last person
        int lastIndex = Integer.MAX_VALUE;
        ParkourPlayer lastPlayer = null;

        for (ParkourPlayer pp : getPlayers()) {
            Location location = pp.getLocation();
            // get the block below player
            Block blockBelow = location.clone().subtract(0, 1, 0).getBlock();

            // teleport player if worlds don't match
            if (location.getWorld() != playerSpawn.getWorld()) {
                pp.teleport(playerSpawn);
                continue;
            }

            // get the last solid block that the player was standing on
            Block last = lastPlayerBlockMap.get(pp);

            // if the difference in height is more than 10, reset
            if (last != null && last.getY() - location.getY() > 10 && playerSpawn.distance(location) > 5) {
                fall();
                return;
            }
//            System.out.println("===");

            int currentIndex;
            if (positionIndexMap.containsKey(blockBelow)) {
                // player is at a known position
//                System.out.println(pp.getName() + " at known pos");

                // register as the last player block
                lastPlayerBlockMap.put(pp, blockBelow);
                // set the current index
                currentIndex = positionIndexMap.get(blockBelow);
            } else {
//                System.out.println(pp.getName() + " at unknown pos");

                // player is not at a known position
                if (last != null) {
//                    System.out.println(pp.getName() + " has last known pos");
                    currentIndex = positionIndexMap.get(last);
                } else {
//                    System.out.println(pp.getName() + " has no last known pos");
                    currentIndex = -1;
                }
            }

            if (lastIndex >= currentIndex) {
//                System.out.println(pp.getName() + " is the new last player");
                lastIndex = currentIndex;
                lastPlayer = pp;
            }
//            System.out.println("last index: " + lastIndex + " | current index: " + currentIndex);
//            System.out.println("===");
        }

        if (lastPlayer != null) {
            player = lastPlayer;
        }

//        Map<Integer, String> ppm = new HashMap<>();
//        for (ParkourPlayer parkourPlayer : getPlayers()) {
//            Block block = lastPlayerBlockMap.get(parkourPlayer);
//
//            int index = -1;
//
//            // if last registered block is null, it means the player isn't on the parkour yet, so just skip this check
//            if (block != null && positionIndexMap.containsKey(block)) {
//                index = positionIndexMap.get(block);
//            }
//
//            ppm.put(index, parkourPlayer.getName());
//        }
//
//        List<Map.Entry<Integer, String>> entries = new ArrayList<>(ppm.entrySet());
//        entries.sort(Map.Entry.comparingByKey(Comparator.naturalOrder()));
//
//        Map<Integer, String> sorted = new LinkedHashMap<>();
//        for (Map.Entry<Integer, String> entry : entries) {
//            sorted.put(entry.getKey(), entry.getValue());
//        }
//
//        for (Integer integer : sorted.keySet()) {
//            System.out.println(integer + " - " + sorted.get(integer));
//        }

        super.tick();

        if (lastPlayer != null) {
//            System.out.println("last player: " + lastPlayer.getName());
            player = lastPlayer;
        }

//        System.out.println("players: " + getPlayers().stream().map(ParkourUser::getName).collect(Collectors.joining()));
//        System.out.println("specs: " + session.getSpectators());
    }

    @Override
    public void reset(boolean regenerate) {
        if (!regenerate) {
            if (session.getPlayers().size() == 1) {
                player = session.getPlayers().get(0);
                regenerate = true;
            }
        }

        super.reset(regenerate);

        if (regenerate) {
            lastPlayerBlockMap.clear();

            getPlayers().forEach(player -> player.teleport(playerSpawn));
        }
    }

    @Override
    public Gamemode getGamemode() {
        return PlusGamemodes.TEAM_SURVIVAL;
    }
}
