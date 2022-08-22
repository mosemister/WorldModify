package org.world.modify.shapes;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface Shape {

	int getMinimumPositionCount();

	int getMaximumPositionCount();

	@NotNull String getName();

	boolean isValid(@NotNull Map<Integer, Vector3i> positions);

	void paste(@NotNull ServerWorld world, @NotNull Map<Vector3i, BlockSnapshot> clipboard,
			@NotNull Map<Integer, Vector3i> positions);

	<W extends World<W, ?>> void walk(@NotNull W world, @NotNull Consumer<Location<W, ?>> location,
			@NotNull Map<Integer, Vector3i> positions);

	default <W extends World<W, ?>> void set(@NotNull BlockState state, @NotNull W world,
			@NotNull Map<Integer, Vector3i> positions) {
		this.walk(world, loc -> loc.setBlock(state, BlockChangeFlags.NOTIFY_CLIENTS), positions);
	}

	default <W extends World<W, ?>> void replace(@NotNull BlockState original, @NotNull BlockState replace,
			@NotNull W world, @NotNull Map<Integer, Vector3i> positions) {
		this.walk(world, loc -> {
			if (!loc.block().equals(original)) {
				return;
			}
			loc.setBlock(replace, BlockChangeFlags.NOTIFY_CLIENTS);
		}, positions);
	}

	default Map<Vector3i, BlockSnapshot> copy(@NotNull ServerWorld world, Map<Integer, Vector3i> positions) {
		Map<Vector3i, BlockSnapshot> snapshots = new LinkedHashMap<>();
		assert this.isValid(positions) : "Requires positions set at 1 and 2";
		Vector3i pos1 = positions.get(1);
		this.walk(world, loc -> {
					Vector3i rel = loc.blockPosition().sub(pos1);
					snapshots.put(rel,
							loc
									.onServer()
									.orElseThrow(() -> new RuntimeException("On Server check failed"))
									.createSnapshot());
				},
				positions);
		return snapshots;
	}

}
