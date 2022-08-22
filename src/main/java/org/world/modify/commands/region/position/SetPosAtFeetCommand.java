package org.world.modify.commands.region.position;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectProxy;
import org.world.modify.WorldModifyPlugin;
import org.world.modify.data.PlayerData;
import org.world.modify.shapes.Shape;
import org.world.modify.shapes.Shapes;

public final class SetPosAtFeetCommand {

	public static final String KEY_POSITION = "position";

	public static final String PERMISSION = "worldmodify.region.set.position";

	private SetPosAtFeetCommand() {
		throw new RuntimeException("Should not be creating a SetPosAtFeetCommand object");
	}

	public static class SetPositionExecutor implements CommandExecutor {

		private final @NotNull Parameter.Value<Integer> position;

		public SetPositionExecutor(@NotNull Parameter.Value<Integer> parameter) {
			this.position = parameter;
		}

		@Override
		public CommandResult execute(CommandContext context) throws CommandException {
			int position = context.one(this.position)
					.orElseThrow(() -> new CommandException(Component.text("A position is required"), true));
			return SetPosAtFeetCommand.execute(context, position);
		}
	}

	private static CommandResult execute(@NotNull SubjectProxy context, int position) {
		Subject subject = context.subject();
		if (!(subject instanceof Player)) {
			return CommandResult.error(
					WorldModifyPlugin.getPlugin().getMessageConfig().getOnlyRunBy().getMessage("command", "player"));
		}
		Player player = (Player) subject;
		PlayerData data = WorldModifyPlugin.getPlugin().getOrCreatePlayerData(player);
		data.setPosition(position, player.blockPosition());
		Component component = WorldModifyPlugin.getPlugin().getMessageConfig().getSetPosition().getMessage(position);
		player.sendMessage(component);
		return CommandResult.success();
	}

	private static Command.Builder applyCommand(Command.Builder builder) {
		return builder.permission(PERMISSION).executionRequirements(cause -> cause.subject() instanceof Player);
	}

	public static Command.Parameterized createWorldEditCommand(int pos) {
		return applyCommand(Command.builder().executor(context -> execute(context, pos))).build();

	}

	public static Command.Parameterized createCommand() {
		int maxPos = Shapes
				.getShapes()
				.parallelStream()
				.mapToInt(Shape::getMaximumPositionCount)
				.max()
				.orElseThrow(() -> new RuntimeException("No shapes found"));

		Parameter.Value<Integer> positionParameter = Parameter.rangedInteger(1, maxPos)
				.key(KEY_POSITION)
				.build();

		return applyCommand(Command
				.builder()
				.executor(new SetPositionExecutor(positionParameter))
				.addParameter(positionParameter))
				.build();
	}
}
