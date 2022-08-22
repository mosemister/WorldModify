package org.world.modify.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.world.modify.WorldModifyPlugin;
import org.world.modify.config.messages.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageConfig {

	private final HoconConfigurationLoader loader;
	private ConfigurationNode rootNode;
	private final Path file = Paths.get(WorldModifyPlugin.getPlugin().getConfigDirectory().toString(), "Messages"
			+ ".conf");


	private static final Object[] SET_POSITION_NODE = {"position", "set"};
	private static final Object[] ONLY_RUN_BY_NODE = {"error", "onlyRunBy"};
	private static final Object[] MISSING_POSITIONS_NODE = {"error", "missingPositions"};
	private static final Object[] CANNOT_FIND_BLOCK_NODE = {"error", "cannotFindBlock"};

	private static final Object[] SERVER_ONLY_COMMAND_NODE = {"error", "serverOnly"};
	private static final Object[] AND_NODE = {"single", "and"};
	private static final Object[] OR_NODE = {"single", "or"};
	private static final Object[] CUBE_NODE = {"single", "cube"};

	public MessageConfig() throws ConfigurateException {
		this.loader = HoconConfigurationLoader.builder().path(file).build();
		try {
			this.rootNode = this.loader.load();
		} catch (ConfigurateException e) {
			this.rootNode = this.loader.createNode();
		}
		update();
	}

	private void update() throws ConfigurateException {
		updateMessage(DefaultMessages.SET_POSITION, SET_POSITION_NODE);

		updateMessage(DefaultMessages.ONLY_RUN_BY, ONLY_RUN_BY_NODE);
		updateMessage(DefaultMessages.MISSING_POSITIONS, MISSING_POSITIONS_NODE);
		updateMessage(DefaultMessages.SERVER_ONLY_COMMAND, SERVER_ONLY_COMMAND_NODE);
		updateMessage(DefaultMessages.CANNOT_FIND_BLOCK, CANNOT_FIND_BLOCK_NODE);

		if (this.rootNode.node(AND_NODE).isNull()) {
			this.rootNode.node(AND_NODE).set("and");
		}
		if (this.rootNode.node(OR_NODE).isNull()) {
			this.rootNode.node(OR_NODE).set("or");
		}
		if (this.rootNode.node(CUBE_NODE).isNull()) {
			this.rootNode.node(CUBE_NODE).set("Cube");
		}
		this.loader.save(this.rootNode);
	}

	private void updateMessage(@NotNull Message message, Object... node) throws SerializationException {
		if (!this.rootNode.node(node).isNull()) {
			return;
		}
		this.rootNode.node(node).set(getMessage(message));
	}

	private String getMessage(@NotNull Message message) {
		return LegacyComponentSerializer.legacyAmpersand().serialize(message.getDefaultMessage());
	}

	public CannotFindBlockMessage getCannotFindBlock() {
		String messageLegacy = this.rootNode.node(CANNOT_FIND_BLOCK_NODE).getString();
		Component message = null;
		if (messageLegacy != null) {
			message = LegacyComponentSerializer.legacyAmpersand().deserialize(messageLegacy);
		}
		return new CannotFindBlockMessage(message);
	}

	public ServerOnlyCommand getServerOnlyCommand() {
		String messageLegacy = this.rootNode.node(SERVER_ONLY_COMMAND_NODE).getString();
		Component message = null;
		if (messageLegacy != null) {
			message = LegacyComponentSerializer.legacyAmpersand().deserialize(messageLegacy);
		}
		return new ServerOnlyCommand(message);
	}

	public MissingPositionsMessage getMissingPositions() {
		String messageLegacy = this.rootNode.node(MISSING_POSITIONS_NODE).getString();
		Component message = null;
		if (messageLegacy != null) {
			message = LegacyComponentSerializer.legacyAmpersand().deserialize(messageLegacy);
		}
		return new MissingPositionsMessage(message);
	}

	public OnlyRunByMessage getOnlyRunBy() {
		String messageLegacy = this.rootNode.node(ONLY_RUN_BY_NODE).getString();
		Component message = null;
		if (messageLegacy != null) {
			message = LegacyComponentSerializer.legacyAmpersand().deserialize(messageLegacy);
		}
		return new OnlyRunByMessage(message);
	}


	public SetPositionMessage getSetPosition() {
		String messageLegacy = this.rootNode.node(SET_POSITION_NODE).getString();
		Component message = null;
		if (messageLegacy != null) {
			message = LegacyComponentSerializer.legacyAmpersand().deserialize(messageLegacy);
		}
		return new SetPositionMessage(message);
	}

	public @NotNull String getOr() {
		return this.rootNode.node(OR_NODE).getString("or");
	}

	public @NotNull String getAnd() {
		return this.rootNode.node(AND_NODE).getString("and");
	}

	public @NotNull String getCube() {
		return this.rootNode.node(CUBE_NODE).getString("Cube");
	}


}
