package dev.efnilite.ipp.generator.multi;

import dev.efnilite.ip.generator.DefaultGenerator;
import dev.efnilite.ip.generator.base.GeneratorOption;
import dev.efnilite.ipp.session.MultiSession;

public abstract class MultiplayerGenerator extends DefaultGenerator {

    /**
     * The session that belongs to this Generator
     */
    protected MultiSession session;

    public MultiplayerGenerator(MultiSession session, GeneratorOption... options) {
        super(session, options);
        this.session = session;
    }

    @Override
    public MultiSession getSession() {
        return session;
    }
}