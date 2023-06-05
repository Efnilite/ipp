package dev.efnilite.ipp.menu;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class AdventureInviteSender {

    public AdventureInviteSender(Player player, Player toJoin, String message) {
        player.sendMessage(MiniMessage.miniMessage()
            .deserialize(message)
            .clickEvent(ClickEvent.runCommand("/parkour join %s".formatted(toJoin.getName()))));
    }

}
