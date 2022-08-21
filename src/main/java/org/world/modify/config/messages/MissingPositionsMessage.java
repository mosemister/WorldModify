package org.world.modify.config.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.world.modify.config.messages.inserts.MessageInserts;

import java.util.Arrays;
import java.util.Collection;

public class MissingPositionsMessage extends AbstractMessage {

	public MissingPositionsMessage(
			@Nullable Component originalMessage) {
		super(Component.text("You are missing " + MessageInserts.POSITIONS.find()), originalMessage,
				MessageInserts.POSITIONS);
	}

	@Deprecated
	public @NotNull Component getMessage() {
		throw new RuntimeException("Message must have at least 1 position");
	}

	public @NotNull Component getMessage(@NotNull Integer... positions) {
		return this.getMessage(Arrays.asList(positions));
	}

	public @NotNull Component getMessage(@NotNull Collection<Integer> positions) {
		Component message = this.getOriginalMessage();
		return MessageInserts.POSITIONS.apply(message, positions);
	}

}
