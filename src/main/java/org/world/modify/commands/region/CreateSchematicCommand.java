package org.world.modify.commands.region;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Identifiable;
import org.spongepowered.api.world.schematic.Schematic;
import org.spongepowered.api.world.volume.block.BlockVolume;
import org.spongepowered.math.vector.Vector3i;
import org.world.modify.WorldModifyPlugin;
import org.world.modify.data.PlayerData;
import org.world.modify.shapes.Shapes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CreateSchematicCommand {

	public static final String PERMISSION = "worldmodify.region.schematic.create";

	public static class CreateExecutor implements CommandExecutor {

		@Override
		public CommandResult execute(CommandContext context) throws CommandException {
			return createSchematic((Identifiable) context.subject(), "temp");
		}
	}

	private static CommandResult createSchematic(@NotNull Identifiable player, @NotNull String name) throws
			CommandException {
		PlayerData data = WorldModifyPlugin.getPlugin().getOrCreatePlayerData(player.uniqueId());
		Map<Integer, Vector3i> positions = data.getSelectedPosition();
		if (!Shapes.CUBE.isValid(positions)) {
			Collection<Integer> notFound = IntStream.range(0, Shapes.CUBE.getMinimumPositionCount())
					.filter(i -> !positions.containsKey(i))
					.boxed()
					.collect(Collectors.toCollection(LinkedList::new));
			throw new CommandException(
					WorldModifyPlugin.getPlugin().getMessageConfig().getMissingPositions().getMessage(notFound));
		}
		BlockVolume.Mutable mutable = BlockVolume.Mutable.empty(positions.get(1), positions.get(2));

		System.out.println("BlockVolume: " + mutable);

		Schematic.Builder schematicBuilder = Schematic.builder();
		schematicBuilder.blocks(mutable);
		schematicBuilder.metaValue(Schematic.METADATA_AUTHOR, player.uniqueId());
		schematicBuilder.metaValue(Schematic.METADATA_NAME, name);
		schematicBuilder.metaValue(Schematic.METADATA_DATE, LocalDate.now());
		schematicBuilder.metaValue(Schematic.METADATA_REQUIRED_MODS, Collections.emptyList());

		Schematic schematic = schematicBuilder.build();
		File file = new File(WorldModifyPlugin.getPlugin().getConfigDirectory().toFile(),
				"schematic/" + player.uniqueId() + "/" + name + ".schematic");

		DataContainer schematicData = Sponge.dataManager()
				.translator(Schematic.class)
				.orElseThrow(() -> new RuntimeException("No schematic translator found"))
				.translate(schematic);

		try {
			FileOutputStream fis = new FileOutputStream(file);
			DataFormats.NBT.get().writeTo(fis, schematicData);
			return CommandResult.success();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Command.Builder applyCommand(@NotNull Command.Builder builder) {
		return builder.executor(new CreateExecutor())
				.permission(PERMISSION)
				.executionRequirements(cause -> Sponge.dataManager().translator(Schematic.class).isPresent()
						&& cause.subject() instanceof Player);
	}

	public static Command.Parameterized createCommand() {
		return applyCommand(Command
				.builder()
		).build();
	}


}
