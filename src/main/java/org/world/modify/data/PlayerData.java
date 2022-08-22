package org.world.modify.data;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.math.vector.Vector3i;
import org.world.modify.shapes.Shape;
import org.world.modify.shapes.Shapes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

	private final Map<Integer, Vector3i> positions = new ConcurrentHashMap<>();
	private final Map<Vector3i, BlockSnapshot> clipboard = new LinkedHashMap<>();
	private final @NotNull UUID attachedPlayer;
	private @NotNull Shape selectedShape = Shapes.CUBE;

	public PlayerData(@NotNull UUID uuid) {
		this.attachedPlayer = uuid;
	}

	public @NotNull UUID getAttachedPlayer() {
		return this.attachedPlayer;
	}

	public Map<Vector3i, BlockSnapshot> getClipboard() {
		return this.clipboard;
	}

	public @NotNull Shape getSelectedShape() {
		return this.selectedShape;
	}

	public void setSelectedShape(@NotNull Shape shape) {
		this.selectedShape = shape;
	}

	public void setClipboard(@NotNull Map<Vector3i, BlockSnapshot> map) {
		this.clipboard.clear();
		this.clipboard.putAll(map);
	}

	public Optional<Vector3i> getSelectedPosition(int index) {
		if (this.positions.size() <= index) {
			return Optional.empty();
		}
		return Optional.of(this.positions.get(index));
	}

	public Map<Integer, Vector3i> getSelectedPosition() {
		return Collections.unmodifiableMap(this.positions);
	}

	public void setPosition(int index, Vector3i position) {
		this.positions.put(index, position);
	}

}
