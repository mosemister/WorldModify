package org.world.modify.commands.draw;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
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
import org.world.modify.shapes.Shapes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LineCommand {

	public static final String PERMISSION = "worldmodify.draw.line";

	public static class DrawExecutor implements CommandExecutor {

		@Override
		public CommandResult execute(CommandContext context) throws CommandException {
			assert Sponge.isServerAvailable() : WorldModifyPlugin.getPlugin()
					.getMessageConfig()
					.getServerOnlyCommand()
					.getOriginalMessage();
			Subject sender = context.subject();
			assert sender instanceof ServerPlayer : WorldModifyPlugin.getPlugin()
					.getMessageConfig()
					.getOnlyRunBy()
					.getMessage("command", "player");
			ServerPlayer player = (ServerPlayer) sender;
			PlayerData data = WorldModifyPlugin.getPlugin().getOrCreatePlayerData(player);
			Map<Integer, Vector3i> positions = data.getSelectedPosition();
			if (!data.getSelectedShape().isValid(positions)) {
				Collection<Integer> notFound = IntStream.range(0, 2)
						.filter(i -> !positions.containsKey(i))
						.boxed()
						.collect(Collectors.toCollection(LinkedList::new));
				throw new CommandException(
						WorldModifyPlugin.getPlugin().getMessageConfig().getMissingPositions().getMessage(notFound));
			}
			Shapes.LINE.set(BlockTypes.BEDROCK.get().defaultState(), player.world(), positions);
			return CommandResult.success();
		}
	}

	public static Command.Parameterized createCommand() {
		return Command
				.builder()
				.executor(new DrawExecutor())
				.permission(PERMISSION)
				.executionRequirements(cause -> Sponge.isServerAvailable() && cause.subject() instanceof ServerPlayer)
				.build();
	}

}
