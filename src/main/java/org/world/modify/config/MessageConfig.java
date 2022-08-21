package org.world.modify.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.world.modify.WorldModifyPlugin;
import org.world.modify.config.messages.Message;
import org.world.modify.config.messages.MissingPositionsMessage;
import org.world.modify.config.messages.OnlyRunByMessage;
import org.world.modify.config.messages.SetPositionMessage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageConfig {

	private HoconConfigurationLoader loader;
	private ConfigurationNode rootNode;
	private final Path file = Paths.get(WorldModifyPlugin.getPlugin().getConfigDirectory().toString(), "Messages"
			+ ".conf");


	private static final Object[] SET_POSITION_NODE = {"position", "set"};
	private static final Object[] ONLY_RUN_BY_NODE = {"error", "onlyRunBy"};
	private static final Object[] MISSING_POSITIONS_NODE = {"error", "missingPositions"};
	private static final Object[] AND_NODE = {"single", "and"};
	private static final Object[] OR_NODE = {"single", "or"};

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
		this.rootNode.node(SET_POSITION_NODE).set(getMessage(DefaultMessages.SET_POSITION));

		this.rootNode.node(ONLY_RUN_BY_NODE).set(getMessage(DefaultMessages.ONLY_RUN_BY));
		this.rootNode.node(MISSING_POSITIONS_NODE).set(getMessage(DefaultMessages.MISSING_POSITIONS));

		this.rootNode.node(AND_NODE).set("and");
		this.rootNode.node(OR_NODE).set("or");

		this.loader.save(this.rootNode);
	}

	private String getMessage(Message message) {
		return LegacyComponentSerializer.legacyAmpersand().serialize(message.getDefaultMessage());
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

	public String getOr() {
		return this.rootNode.node(OR_NODE).getString();
	}

	public String getAnd() {
		return this.rootNode.node(AND_NODE).getString();
	}


}
