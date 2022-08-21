package org.world.modify.config.messages.inserts;

import java.util.Collection;

public final class MessageInserts {

	public static final MessageInsert<Integer> POSITION = new AbstractMessageInsert<>("%position%");
	public static final MessageInsert<String> COMMAND_SENDER = new AbstractMessageInsert<>("%command_sender%");
	public static final MessageInsert<String> TYPE = new AbstractMessageInsert<>("%type%");
	public static final MessageInsert<Collection<Integer>> POSITIONS = new ArrayMessageInsert<>("%positions%", false);

}
