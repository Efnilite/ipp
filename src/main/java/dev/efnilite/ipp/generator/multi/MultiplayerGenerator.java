package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.generator.GeneratorOption;
import dev.efnilite.ip.player.ParkourPlayer;
import dev.efnilite.ipp.generator.single.PlusGenerator;
import dev.efnilite.ipp.session.MultiSession;

import java.util.List;

public abstract class MultiplayerGenerator extends PlusGenerator {

    /**
     * The session that belongs to this Generator
     */
    protected MultiSession session;

    public MultiplayerGenerator(MultiSession session, GeneratorOption... options) {
        super(session, options);
        this.session = session;
    }

    public List<ParkourPlayer> getPlayers() {
        return session.getPlayers();
    }
}