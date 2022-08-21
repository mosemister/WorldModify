package org.world.modify.config.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.world.modify.config.messages.inserts.MessageInsert;

import java.util.Collection;

public interface Message {

	@NotNull Component getOriginalMessage();
	void setOriginalMessage(@NotNull Component component);
	@NotNull Component getDefaultMessage();

	@NotNull Collection<MessageInsert<?>> getInserts();
}
