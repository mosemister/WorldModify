package org.world.modify;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;
import org.world.modify.commands.draw.LineCommand;
import org.world.modify.commands.region.*;
import org.world.modify.commands.region.position.SetPosAtChunkCommand;
import org.world.modify.commands.region.position.SetPosAtFeetCommand;
import org.world.modify.commands.region.position.SetPosAtRayCommand;
import org.world.modify.config.MessageConfig;
import org.world.modify.data.PlayerData;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@Plugin("world_modify")
public class WorldModifyPlugin {

	private final @NotNull Collection<PlayerData> playersData = new HashSet<>();
	private final @NotNull PluginContainer container;
	private final @NotNull Logger logger;
	private final MessageConfig messageConfig;
	private static WorldModifyPlugin plugin;

	@Inject
	public WorldModifyPlugin(@NotNull PluginContainer container, @NotNull Logger logger) {
		plugin = this;
		this.container = container;
		this.logger = logger;
		try {
			this.messageConfig = new MessageConfig();
		} catch (ConfigurateException e) {
			throw new RuntimeException(e);
		}
	}

	@Listener
	public void onRegisterCommands(RegisterCommandEvent<Command.Parameterized> event) {
		Command.Parameterized setRegionPositonAtFeetCommand = SetPosAtFeetCommand.createCommand();
		//Command.Parameterized setRegionPositionAtRayCommand = SetPosAtRayCommand.createCommand();
		Command.Parameterized setRegionPositionAtChunkCommand = SetPosAtChunkCommand.createCommand();

		Command.Parameterized setCommand = Command.builder()
				.addChild(setRegionPositonAtFeetCommand, "here")
				//.addChild(setRegionPositionAtRayCommand, "looking")
				.build();

		Command.Parameterized lineCommand = LineCommand.createCommand();

		Command.Parameterized saveSchematicCommand = CreateSchematicCommand.createCommand();

		Command.Parameterized schematicCommand =
				Command.builder().addChild(saveSchematicCommand, "save", "create").build();

		Command.Parameterized setRegionBlockCommand = SetBlockCommand.createCommand();
		Command.Parameterized replaceRegionBlockCommand = ReplaceBlockCommand.createCommand();
		Command.Parameterized copyRegionCommand = CopyCommand.createCommand();
		Command.Parameterized pasteRegionCommand = PasteCommand.createCommand();

		Command.Parameterized regionCommand = Command.builder()
				.addChild(setRegionBlockCommand, "as", "block")
				.addChild(replaceRegionBlockCommand, "replace")
				.addChild(setCommand, "set", "position")
				.addChild(copyRegionCommand, "copy")
				.addChild(pasteRegionCommand, "paste")
				//.addChild(schematicCommand, "schematic")
				.build();

		Command.Parameterized mainCommand = Command
				.builder()
				.addChild(regionCommand, "region", "r")
				.build();

		event.register(this.container, mainCommand, "worldmodify", "wm");

		//worldedit like commands
		event.register(this.container, SetPosAtFeetCommand.createWorldEditCommand(1), "/pos1");
		event.register(this.container, SetPosAtFeetCommand.createWorldEditCommand(2), "/pos2");
		event.register(this.container, SetPosAtRayCommand.createWorldEditCommand(1), "/hpos1");
		event.register(this.container, SetPosAtRayCommand.createWorldEditCommand(2), "/hpos2");
		event.register(this.container, setRegionPositionAtChunkCommand, "/chunk");
		event.register(this.container, setRegionBlockCommand, "/set");
		event.register(this.container, replaceRegionBlockCommand, "/replace");
		event.register(this.container, copyRegionCommand, "/copy");
		event.register(this.container, pasteRegionCommand, "/paste");
		//event.register(this.container, schematicCommand, "/schematic");
		//event.register(this.container, lineCommand, "/line");


	}

	public @NotNull PluginContainer getContainer() {
		return this.container;
	}

	public @NotNull Collection<PlayerData> getPlayerData() {
		return Collections.unmodifiableCollection(this.playersData);
	}

	public @NotNull PlayerData getOrCreatePlayerData(@NotNull Identifiable player) {
		return this.getOrCreatePlayerData(player.uniqueId());
	}

	public @NotNull PlayerData getOrCreatePlayerData(@NotNull UUID uuid) {
		return this.getPlayerData()
				.parallelStream()
				.filter(data -> data.getAttachedPlayer().equals(uuid))
				.findAny()
				.orElseGet(() -> {
					PlayerData data = new PlayerData(uuid);
					this.playersData.add(data);
					return data;
				});
	}

	public @NotNull Path getConfigDirectory() {
		return Sponge.configManager().pluginConfig(this.container).directory();
	}

	public @NotNull MessageConfig getMessageConfig() {
		return this.messageConfig;
	}

	public static WorldModifyPlugin getPlugin() {
		return plugin;
	}

}
