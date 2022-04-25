package dev.efnilite.ipplus.mode;

import com.google.gson.annotations.Expose;
import dev.efnilite.ip.schematic.selection.Selection;

/**
 * Class for Lobby selections.
 * Used as wrapper for Gson serialization support.
 */
public record LobbySelection(@Expose Selection selection) {

}