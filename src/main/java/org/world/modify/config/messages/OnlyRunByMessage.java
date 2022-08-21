package org.world.modify.config.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.world.modify.config.messages.inserts.MessageInserts;

public class OnlyRunByMessage extends AbstractMessage {

	public OnlyRunByMessage(
			@Nullable Component originalMessage) {
		super(Component.text(
						"Can only run " + MessageInserts.TYPE.find() + " by a " + MessageInserts.COMMAND_SENDER.find()),
				originalMessage, MessageInserts.COMMAND_SENDER, MessageInserts.TYPE);
	}

	public @NotNull Component getMessage(@NotNull String type, @NotNull String commandSender) {
		Component message = this.getOriginalMessage();
		message = MessageInserts.TYPE.apply(message, type);
		message = MessageInserts.COMMAND_SENDER.apply(message, commandSender);
		return message;
	}
}
