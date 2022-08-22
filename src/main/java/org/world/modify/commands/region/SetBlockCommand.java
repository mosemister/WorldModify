package org.world.modify.commands.region;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectProxy;
import org.spongepowered.api.world.World;
import org.spongepowered.math.vector.Vector3i;
import org.world.modify.WorldModifyPlugin;
import org.world.modify.data.PlayerData;
import org.world.modify.shapes.Shape;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SetBlockCommand {

	public static final String KEY_BLOCK = "block";

	public static final String PERMISSION = "worldmodify.region.set.block";

	private SetBlockCommand() {
		throw new RuntimeException("Should not be creating a SetBlockCommand object");
	}

	public static class SetBlockExecutor implements CommandExecutor {

		private final Parameter.Value<BlockState> parameter;

		public SetBlockExecutor(Parameter.Value<BlockState> parameter) {
			this.parameter = parameter;
		}

		@Override
		public CommandResult execute(CommandContext context) throws CommandException {
			return SetBlockCommand.execute(context, context.one(this.parameter)
					.orElseThrow(() -> new CommandException(Component.text("Missing blockstate argument"), true)));
		}
	}

	private static <W extends World<W, ?>> CommandResult execute(@NotNull SubjectProxy context, BlockState type) throws
			CommandException {
		Subject subject = context.subject();
		if (!(subject instanceof Player)) {
			return CommandResult.error(
					WorldModifyPlugin.getPlugin().getMessageConfig().getOnlyRunBy().getMessage("command", "player"));
		}
		Player player = (Player) subject;
		W world = (W) player.world();
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
		shape.set(type, world, positions);
		return CommandResult.success();
	}

	private static Command.Builder applyCommand(Command.Builder builder) {
		return builder
				.permission(PERMISSION)
				.executionRequirements(cause -> cause.subject() instanceof Player);
	}

	public static Command.Parameterized createCommand() {
		Parameter.Value<BlockState> parameter = Parameter.blockState().key(KEY_BLOCK).build();
		return applyCommand(Command
				.builder()
				.executor(new SetBlockExecutor(parameter))
				.addParameter(parameter))
				.build();
	}
}
