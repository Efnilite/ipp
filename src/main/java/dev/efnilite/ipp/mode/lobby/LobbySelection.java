package dev.efnilite.ipp.mode.lobby;

import com.google.gson.annotations.Expose;
import dev.efnilite.ipp.IPP;
import dev.efnilite.ipp.util.ObjectSerializer;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.io.IOException;

/**
 * Class for Lobby selections.
 * Used as wrapper for Gson serialization support.
 */
public class LobbySelection {

    private final BoundingBox bb;
    @Expose
    public String pos1;
    @Expose
    public String pos2;

    public LobbySelection(BoundingBox bb) throws IOException {
        this.bb = bb;

        this.pos1 = ObjectSerializer.serialize64(bb.getMin());
        this.pos2 = ObjectSerializer.serialize64(bb.getMax());
    }

    public static LobbySelection from(World world, String pos1, String pos2) throws IOException, ClassNotFoundException {
        Vector vec1 = ObjectSerializer.deserialize64(pos1);
        Vector vec2 = ObjectSerializer.deserialize64(pos2);

        if (vec1 == null || vec2 == null) {
            IPP.logging().stack("Location of lobby mode area is null",
                    "delete the lobbies folder and restart the server", new IllegalArgumentException());
            return null;
        }

        return new LobbySelection(BoundingBox.of(vec1.toLocation(world), vec2.toLocation(world)));
    }

    public BoundingBox getBb() {
        return bb;
    }
}