package org.world.modify.commands.region.position;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectProxy;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.math.vector.Vector3i;
import org.world.modify.WorldModifyPlugin;
import org.world.modify.data.PlayerData;
import org.world.modify.shapes.Shapes;

public final class SetPosAtChunkCommand {

	public static final String PERMISSION = "worldmodify.region.set.position";

	private SetPosAtChunkCommand() {
		throw new RuntimeException("Should not be creating a SetPosAtChunkCommand object");
	}

	private static CommandResult execute(@NotNull SubjectProxy context) {
		Subject subject = context.subject();
		if (!(subject instanceof Player)) {
			return CommandResult.error(
					WorldModifyPlugin.getPlugin().getMessageConfig().getOnlyRunBy().getMessage("command", "player"));
		}
		Player player = (Player) subject;
		PlayerData data = WorldModifyPlugin.getPlugin().getOrCreatePlayerData(player);
		Vector3i chunkPosition = player.location().chunkPosition();
		WorldChunk chunk = player.world().chunk(chunkPosition);
		data.setPosition(1, chunk.min());
		data.setPosition(2, chunk.max());
		data.setSelectedShape(Shapes.CUBE);
		Component component = WorldModifyPlugin.getPlugin().getMessageConfig().getSetPosition().getMessage(1);
		player.sendMessage(component);
		component = WorldModifyPlugin.getPlugin().getMessageConfig().getSetPosition().getMessage(2);
		player.sendMessage(component);
		return CommandResult.success();
	}

	private static Command.Builder applyCommand(Command.Builder builder) {
		return builder.permission(PERMISSION).executionRequirements(cause -> cause.subject() instanceof Player);
	}

	public static Command.Parameterized createCommand() {
		return applyCommand(Command
				.builder()
				.executor(SetPosAtChunkCommand::execute)
		).build();
	}
}
