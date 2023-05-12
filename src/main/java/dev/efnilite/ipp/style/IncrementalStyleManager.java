package dev.efnilite.ipp.style;

import dev.efnilite.ip.config.Option;
import dev.efnilite.ip.session.Session;
import dev.efnilite.ip.world.WorldDivider;
import dev.efnilite.ipp.IPP;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class IncrementalStyleManager {

    private static final Map<Session, Integer> increments = new HashMap<>();

    public static void register() {
        Option.initStyles("styles.incremental.list", "incremental", IPP.getConfiguration().getFile("config"),
                (blocks, session) -> {
                    int increment = increments.getOrDefault(session, 0);

                    if (blocks.size() > increment + 1) {
                        increments.put(session, increment + 1);
                    } else {
                        increments.put(session, 0);
                    }

                    return blocks.get(increment);
                });
    }

    // clear unused sessions to avoid memory leak
    public static void clear() {
        Collection<Session> activeSessions = WorldDivider.sessions.values();

        increments.keySet().stream()
                .filter(session -> !activeSessions.contains(session))
                .forEach(increments::remove);
    }
}
