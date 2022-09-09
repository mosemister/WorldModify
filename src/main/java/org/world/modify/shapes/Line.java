package org.world.modify.shapes;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import java.util.Map;
import java.util.function.Consumer;

public class Line implements Shape {
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
		return "Line";
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

	@Override
	public void paste(@NotNull ServerWorld world, @NotNull Map<Vector3i, BlockSnapshot> clipboard,
			@NotNull Map<Integer, Vector3i> positions) {

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
	public <W extends World<W, ?>> void walk(@NotNull W world, @NotNull Consumer<Location<W, ?>> location,
			@NotNull Map<Integer, Vector3i> positions) {
		Vector3i[] posArray = this.getPositions(positions);
		Vector3i difference = new Vector3i(posArray[1].x() - posArray[0].x(), posArray[1].y() - posArray[0].y(),
				posArray[1].z() - posArray[0].z());
		int forEveryX = Math.min(difference.y(), difference.z()) / difference.x();
		int forEveryY = Math.min(difference.x(), difference.z()) / difference.y();
		int forEveryZ = Math.min(difference.x(), difference.y()) / difference.z();
		Vector3i plus = new Vector3i(0, 0, 0);
		if (difference.x() >= Math.max(difference.y(), difference.z())) {
			int plusX = difference.x() / Math.max(difference.y(), difference.z());
			plus = plus.add(plusX, 0, 0);
		}
		if (difference.y() >= Math.max(difference.x(), difference.z())) {
			int plusY = difference.y() / Math.max(difference.x(), difference.z());
			plus = plus.add(0, plusY, 0);
		}
		if (difference.z() >= Math.max(difference.y(), difference.z())) {
			int plusZ = difference.z() / Math.max(difference.y(), difference.x());
			plus = plus.add(0, 0, plusZ);
		}
		Vector3i previous = posArray[0];
		if (previous.equals(posArray[1])) {
			return;
		}
		int maxDistance = Math.max(difference.x(), Math.max(difference.y(), difference.z()));
		for (int i = 0; i < maxDistance; i++) {
			previous = previous.add(plus);
			if (previous.equals(posArray[1])) {
				break;
			}
			location.accept(world.location(previous));
		}
	}
}
