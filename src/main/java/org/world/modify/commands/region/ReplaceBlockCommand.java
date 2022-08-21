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
import org.world.modify.shapes.Shapes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class ReplaceBlockCommand {

	public static final String KEY_BLOCK = "block";
	public static final String KEY_REPLACEMENT = "replacement";

	public static final String PERMISSION = "worldmodify.region.set.block";

	public static class SetBlockExecutor implements CommandExecutor {

		private final @NotNull Parameter.Value<BlockState> original;
		private final @NotNull Parameter.Value<BlockState> replacement;

		public SetBlockExecutor(@NotNull Parameter.Value<BlockState> original,
				@NotNull Parameter.Value<BlockState> replacement) {
			this.original = original;
			this.replacement = replacement;
		}

		@Override
		public CommandResult execute(CommandContext context) throws CommandException {
			return ReplaceBlockCommand.execute(
					context,
					context
							.one(this.original)
							.orElseThrow(
									() -> new CommandException(Component.text("Missing blockstate argument"), true)),
					context
							.one(this.replacement)
							.orElseThrow(
									() -> new CommandException(Component.text("Missing blockstate argument"), true)));
		}
	}

	private static <W extends World<W, ?>> CommandResult execute(@NotNull SubjectProxy context,
			@NotNull BlockState type, @NotNull BlockState replacement) {
		Subject subject = context.subject();
		if (!(subject instanceof Player)) {
			return CommandResult.error(
					WorldModifyPlugin.getPlugin().getMessageConfig().getOnlyRunBy().getMessage("command", "player"));
		}
		Player player = (Player) subject;
		W world = (W) player.world();
		PlayerData data = WorldModifyPlugin.getPlugin().getOrCreatePlayerData(player);
		Map<Integer, Vector3i> positions = data.getSelectedPosition();
		if (!Shapes.CUBE.isValid(positions)) {
			Collection<Integer> notFound = new LinkedList<>();
			if (!positions.containsKey(1)) {
				notFound.add(1);
			}
			if (!positions.containsKey(2)) {
				notFound.add(2);
			}
			return CommandResult.error(
					WorldModifyPlugin
							.getPlugin()
							.getMessageConfig()
							.getMissingPositions()
							.getMessage(notFound));
		}
		Shapes.CUBE.replace(type, replacement, world, positions);
		return CommandResult.success();
	}

	private static Command.Builder applyCommand(Command.Builder builder) {
		return builder
				.permission(PERMISSION)
				.executionRequirements(cause -> cause.subject() instanceof Player);
	}

	public static Command.Parameterized createCommand() {
		Parameter.Value<BlockState> original = Parameter.blockState().key(KEY_BLOCK).build();
		Parameter.Value<BlockState> replacement = Parameter.blockState().key(KEY_REPLACEMENT).build();

		return applyCommand(Command
				.builder()
				.executor(new SetBlockExecutor(original, replacement))
				.addParameter(original)
				.addParameter(replacement))
				.build();
	}
}
