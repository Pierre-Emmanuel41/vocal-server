package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.impl.PlayerVocalClient;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServerClientLeavePostEvent extends VocalServerEvent {
	private PlayerVocalClient client;

	/**
	 * Creates an event thrown when a player has left a mumble server.
	 * 
	 * @param server The server left by the client.
	 * @param client The client that has left a server.
	 */
	public VocalServerClientLeavePostEvent(IVocalServer server, PlayerVocalClient client) {
		super(server);
		this.client = client;
	}

	/**
	 * @return The client that has left a server.
	 */
	public PlayerVocalClient getClient() {
		return client;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("client=#" + getClient().hashCode());
		return String.format("%s_%s", getName(), joiner);
	}
}
