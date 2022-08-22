package org.world.modify.shapes;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.mirror.Mirror;
import org.spongepowered.api.util.mirror.Mirrors;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Cube implements Shape {
	@Override
	public int getMinimumPositionCount() {
		return 2;
	}

	@Override
	public int getMaximumPositionCount() {
		return 2;
	}

	@Override
	public @NotNull String getName() {
		return "Cube";
	}

	@Override
	public boolean isValid(@NotNull Map<Integer, Vector3i> positions) {
		if (positions.size() < this.getMinimumPositionCount()) {
			return false;
		}
		if (!positions.containsKey(1)) {
			return false;
		}
		return positions.containsKey(2);
	}

	public void flipAcrossX(@NotNull ServerWorld world, @NotNull Map<Integer, Vector3i> positions) {
		flipAcross(world, positions, Mirrors.FRONT_BACK.get(), true);
	}

	public void flipAcrossZ(@NotNull ServerWorld world, @NotNull Map<Integer, Vector3i> positions) {
		flipAcross(world, positions, Mirrors.LEFT_RIGHT.get(), false);
	}

	private Map<Vector3i, BlockSnapshot> rotate(@NotNull ServerWorld world, @NotNull Map<Integer, Vector3i> positions,
			@NotNull Mirror mirror, boolean flipOnX) {
		//TODO

		Vector3i[] posArray = this.getPositions(positions);
		int differenceX = posArray[1].x() - posArray[0].x();
		int differenceZ = posArray[1].z() - posArray[0].z();
		Map<Vector3i, BlockSnapshot> flipped = new LinkedHashMap<>();
		for (int x = posArray[0].x(); x < posArray[1].x(); x++) {
			for (int y = posArray[0].y(); y < posArray[1].y(); y++) {
				for (int z = posArray[0].z(); z < posArray[1].z(); z++) {
					BlockSnapshot snapshot = world.location(posArray[0].add(x, y, z)).createSnapshot();
					Vector3i rel = new Vector3i(flipOnX ? (differenceX - x) : x, y, flipOnX ? z : (differenceZ - z));
					if (x > ((flipOnX ? differenceX : differenceZ) / 2)) {
						BlockState state = snapshot.state();
						snapshot.withState(state.mirror(mirror));
					}
					flipped.put(rel, snapshot);
				}
			}
		}
		this.paste(world, flipped, positions);
		return flipped;
	}

	private Map<Vector3i, BlockSnapshot> flipAcross(@NotNull ServerWorld world,
			@NotNull Map<Integer, Vector3i> positions, @NotNull Mirror mirror, boolean flipOnX) {
		Vector3i[] posArray = this.getPositions(positions);
		int differenceX = posArray[1].x() - posArray[0].x();
		int differenceZ = posArray[1].z() - posArray[0].z();
		Map<Vector3i, BlockSnapshot> flipped = new LinkedHashMap<>();
		for (int x = posArray[0].x(); x < posArray[1].x(); x++) {
			for (int y = posArray[0].y(); y < posArray[1].y(); y++) {
				for (int z = posArray[0].z(); z < posArray[1].z(); z++) {
					BlockSnapshot snapshot = world.location(posArray[0].add(x, y, z)).createSnapshot();
					Vector3i rel = new Vector3i(flipOnX ? (differenceX - x) : x, y, flipOnX ? z : (differenceZ - z));
					if (x > ((flipOnX ? differenceX : differenceZ) / 2)) {
						BlockState state = snapshot.state();
						snapshot.withState(state.mirror(mirror));
					}
					flipped.put(rel, snapshot);
				}
			}
		}
		this.paste(world, flipped, positions);
		return flipped;
	}

	public @NotNull Vector3i[] getPositions(@NotNull Map<Integer, Vector3i> positions) {
		assert this.isValid(positions) : "Missing positions at 1 and/or 2";
		Vector3i pos1 = positions.get(1);
		Vector3i pos2 = positions.get(2);
		Vector3i min =
				new Vector3i(Math.min(pos1.x(), pos2.x()), Math.min(pos1.y(), pos2.y()), Math.min(pos1.z(), pos2.z()));
		Vector3i max =
				new Vector3i(Math.max(pos1.x(), pos2.x()), Math.max(pos1.y(), pos2.y()), Math.max(pos1.z(), pos2.z()));
		return new Vector3i[]{min, max};
	}

	@Override
	public void paste(@NotNull ServerWorld world, @NotNull Map<Vector3i, BlockSnapshot> clipboard,
			@NotNull Map<Integer, Vector3i> positions) {
		Vector3i[] posArray = getPositions(positions);
		ServerLocation position1 = world.location(posArray[0]);
		Vector3i pos2 = posArray[1];

		clipboard.forEach((rel, snapshot) -> {
			ServerLocation setPosition = position1.add(rel);
			if (pos2.x() < setPosition.x()) {
				return;
			}
			if (pos2.y() < setPosition.y()) {
				return;
			}
			if (pos2.z() < setPosition.z()) {
				return;
			}
			snapshot.copy().withLocation(setPosition).restore(true, BlockChangeFlags.NOTIFY_CLIENTS);
		});
	}

	@Override
	public <W extends World<W, ?>> void walk(@NotNull W world, @NotNull Consumer<Location<W, ?>> location,
			@NotNull Map<Integer, Vector3i> positions) {
		Vector3i[] posArray = this.getPositions(positions);
		for (int x = posArray[0].x(); x <= posArray[1].x(); x++) {
			for (int y = posArray[0].y(); y <= posArray[1].y(); y++) {
				for (int z = posArray[0].z(); z <= posArray[1].z(); z++) {
					location.accept(world.location(x, y, z));
				}
			}
		}
	}
}
