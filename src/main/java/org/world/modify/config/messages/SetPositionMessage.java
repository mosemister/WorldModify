package org.world.modify.config.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.world.modify.config.messages.inserts.MessageInserts;

public class SetPositionMessage extends AbstractMessage {

	public SetPositionMessage(
			@Nullable Component originalMessage) {
		super(Component.text("position " + MessageInserts.POSITION.find() + " set").color(NamedTextColor.AQUA),
				originalMessage, MessageInserts.POSITION);
	}

	public @NotNull Component getMessage(int position) {
		return MessageInserts.POSITION.apply(this.getOriginalMessage(), position);
	}
}
