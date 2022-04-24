package dev.efnilite.ipplus.mode;

import com.google.gson.annotations.Expose;
import dev.efnilite.ip.schematic.selection.Selection;

/**
 * Class for Lobby selections.
 * Used as wrapper for Gson serialization support.
 */
public class LobbySelection {

    @Expose
    private Selection selection;

    public LobbySelection(Selection selection) {
        this.selection = selection;
    }

    public Selection getSelection() {
        return selection;
    }
}
