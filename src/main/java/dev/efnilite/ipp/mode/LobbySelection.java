package dev.efnilite.ipp.mode;

import com.google.gson.annotations.Expose;
import dev.efnilite.ip.schematic.selection.Selection;
import dev.efnilite.ipp.IPP;
import dev.efnilite.vilib.serialization.ObjectSerializer;
import dev.efnilite.vilib.vector.Vector3D;
import org.bukkit.World;

/**
 * Class for Lobby selections.
 * Used as wrapper for Gson serialization support.
 */
public class LobbySelection {

    @Expose
    public String pos1;
    @Expose
    public String pos2;

    private final Selection selection;

    public LobbySelection(Selection selection) {
        this.selection = selection;

        this.pos1 = ObjectSerializer.serialize64(Vector3D.fromBukkit(selection.getPos1().toVector()));
        this.pos2 = ObjectSerializer.serialize64(Vector3D.fromBukkit(selection.getPos2().toVector()));
    }

    public static LobbySelection from(World world, String pos1, String pos2) {
        Vector3D vec1 = ObjectSerializer.deserialize64(pos1);
        Vector3D vec2 = ObjectSerializer.deserialize64(pos2);

        if (vec1 == null || vec2 == null) {
            IPP.logging().stack("Location of lobby mode area is null",
                    "delete the lobbies folder and restart the server", new IllegalArgumentException());
            return null;
        }

        return new LobbySelection(new Selection(vec1.toLocation(world), vec2.toLocation(world)));
    }

    public Selection getSelection() {
        return selection;
    }
}