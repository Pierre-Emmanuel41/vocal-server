package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.impl.PlayerVocalClient;

public class VocalClientDisconnectPostEvent extends PlayerVocalClientEvent {

	/**
	 * Creates an event thrown when a mumble client has been disconnected from the server.
	 * 
	 * @param client The disconnected client.
	 */
	public VocalClientDisconnectPostEvent(PlayerVocalClient client) {
		super(client);
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getClient().getServer().getName());
		joiner.add("client=#" + getClient().hashCode());
		return String.format("%s_%s", getName(), joiner);
	}
}
