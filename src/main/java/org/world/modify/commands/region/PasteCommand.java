package org.world.modify.commands.region;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.math.vector.Vector3i;
import org.world.modify.WorldModifyPlugin;
import org.world.modify.data.PlayerData;
import org.world.modify.shapes.Shape;
import org.world.modify.shapes.Shapes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PasteCommand {

	public static final String PERMISSION = "worldmodify.region.paste";

	private PasteCommand() {
		throw new RuntimeException("Should not be creating a PasteCommand object");
	}

	public static class PasteExecutor implements CommandExecutor {

		@Override
		public CommandResult execute(CommandContext context) throws CommandException {
			if (!Sponge.isServerAvailable()) {
				throw new CommandException(
						WorldModifyPlugin.getPlugin().getMessageConfig().getServerOnlyCommand().getOriginalMessage());
			}
			Subject subject = context.subject();
			if (!(subject instanceof ServerPlayer)) {
				return CommandResult.error(WorldModifyPlugin.getPlugin()
						.getMessageConfig()
						.getOnlyRunBy()
						.getMessage("command", "player"));
			}
			ServerPlayer player = (ServerPlayer) subject;
			PlayerData data = WorldModifyPlugin.getPlugin().getOrCreatePlayerData(player);
			Map<Integer, Vector3i> positions = data.getSelectedPosition();
			Shape shape = data.getSelectedShape();
			if (!data.getSelectedShape().isValid(positions)) {
				Collection<Integer> notFound = IntStream.range(0, shape.getMinimumPositionCount())
						.filter(i -> !positions.containsKey(i))
						.boxed()
						.collect(Collectors.toCollection(LinkedList::new));
				throw new CommandException(
						WorldModifyPlugin.getPlugin().getMessageConfig().getMissingPositions().getMessage(notFound));
			}
			shape.paste(player.world(), data.getClipboard(), data.getSelectedPosition());
			return CommandResult.success();
		}
	}

	public static @NotNull Command.Parameterized createCommand() {
		return Command
				.builder()
				.permission(PERMISSION)
				.executor(new PasteExecutor())
				.executionRequirements(cause -> Sponge.isServerAvailable() && cause.subject() instanceof ServerPlayer)
				.build();
	}
}
