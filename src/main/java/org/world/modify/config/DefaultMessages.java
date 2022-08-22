package org.world.modify.config;

import org.world.modify.config.messages.*;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public final class DefaultMessages {

	public static final SetPositionMessage SET_POSITION = new SetPositionMessage(null);
	public static final OnlyRunByMessage ONLY_RUN_BY = new OnlyRunByMessage(null);
	public static final ServerOnlyCommand SERVER_ONLY_COMMAND = new ServerOnlyCommand(null);
	public static final MissingPositionsMessage MISSING_POSITIONS = new MissingPositionsMessage(null);
	public static final CannotFindBlockMessage CANNOT_FIND_BLOCK = new CannotFindBlockMessage(null);

	public static Collection<Message> getAllDefaultMessages() {
		return Arrays.stream(DefaultMessages.class.getDeclaredFields())
				.filter(field -> Modifier.isStatic(field.getModifiers()))
				.filter(field -> Modifier.isPublic(field.getModifiers()))
				.filter(field -> Modifier.isFinal(field.getModifiers()))
				.map(field -> {
					try {
						return (Message) field.get(null);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toSet());
	}
}
