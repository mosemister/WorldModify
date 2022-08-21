package org.world.modify.config.messages.inserts;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.NotNull;

public interface MessageInsert <I> {

	@NotNull String find();

	@NotNull String convert(@NotNull I value);

	default @NotNull Component apply(@NotNull Component message, @NotNull I value){
		return message.replaceText(TextReplacementConfig.builder().replacement(convert(value)).matchLiteral(find()).build());
	}
}
