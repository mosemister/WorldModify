package org.world.modify.config.messages.inserts;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AbstractMessageInsert<I> implements MessageInsert<I> {

	private final @NotNull String find;
	private final @NotNull Function<I, String> toString;

	public AbstractMessageInsert(@NotNull String find) {
		this(find, Object::toString);
	}

	public AbstractMessageInsert(@NotNull String find, @NotNull Function<I, String> function) {
		this.find = find;
		this.toString = function;
	}

	@Override
	public @NotNull String find() {
		return this.find;
	}

	@Override
	public @NotNull String convert(@NotNull I value) {
		return this.toString.apply(value);
	}
}
