package org.world.modify.config.messages;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class ServerOnlyCommand extends AbstractMessage {

	public ServerOnlyCommand(
			@Nullable Component originalMessage) {
		super(Component.text("Command can only be ran on a server"), originalMessage);
	}
}
