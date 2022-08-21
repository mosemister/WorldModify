package org.world.modify.config.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.world.modify.config.messages.inserts.MessageInsert;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

public class AbstractMessage implements Message {

	private @Nullable Component originalMessage;
	private final @NotNull Component defaultMessage;
	private final @NotNull Collection<MessageInsert<?>> inserts = new LinkedHashSet<>();

	public AbstractMessage(@NotNull Component defaultMessage, @Nullable Component originalMessage,
			MessageInsert<?>... inserts) {
		this.defaultMessage = defaultMessage;
		this.originalMessage = originalMessage;
		this.inserts.addAll(Arrays.asList(inserts));
	}

	@Override
	public @NotNull Component getOriginalMessage() {
		if (this.originalMessage == null) {
			return this.defaultMessage;
		}
		return this.originalMessage;
	}

	@Override
	public void setOriginalMessage(@NotNull Component component) {
		this.originalMessage = component;
	}

	@Override
	public @NotNull Component getDefaultMessage() {
		return this.defaultMessage;
	}

	@Override
	public @NotNull Collection<MessageInsert<?>> getInserts() {
		return this.inserts;
	}
}
