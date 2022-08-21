package org.world.modify.config.messages.inserts;

import org.jetbrains.annotations.NotNull;
import org.world.modify.WorldModifyPlugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public class ArrayMessageInsert<T> extends AbstractMessageInsert<Collection<T>> {
	public ArrayMessageInsert(@NotNull String find, boolean useOr) {
		this(find, useOr, Object::toString);
	}

	public ArrayMessageInsert(@NotNull String find, boolean useOr, @NotNull Function<T, String> function) {
		super(find, (collection) -> {
			StringBuilder builder = new StringBuilder();
			Iterator<T> iter = collection.iterator();
			for (int i = 0; i < collection.size() - 1; i++) {
				if (i == 0) {
					builder.append(function.apply(iter.next()));
					continue;
				}
				builder.append(", ").append(function.apply(iter.next()));
			}
			builder.append(" ");
			if (useOr) {
				builder.append(WorldModifyPlugin.getPlugin().getMessageConfig().getOr());
			} else {
				builder.append(WorldModifyPlugin.getPlugin().getMessageConfig().getAnd());
			}
			builder.append(" ");
			builder.append(function.apply(iter.next()));
			return builder.toString();
		});
	}
}
