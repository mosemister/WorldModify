package org.world.modify.config.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class CannotFindBlockMessage extends AbstractMessage {

	public CannotFindBlockMessage(@Nullable Component originalMessage) {
		super(Component.text("Cannot find block"), originalMessage);
	}
}
