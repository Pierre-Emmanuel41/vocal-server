package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.impl.PlayerVocalClient;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServerClientJoinPostEvent extends VocalServerEvent {
	private PlayerVocalClient client;

	/**
	 * Creates an event thrown when a player has joined a mumble server.
	 * 
	 * @param server The server joined by the client.
	 * @param client The client that has joined a server.
	 */
	public VocalServerClientJoinPostEvent(IVocalServer server, PlayerVocalClient client) {
		super(server);
		this.client = client;
	}

	/**
	 * @return The client that has joined a server.
	 */
	public PlayerVocalClient getClient() {
		return client;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("client=#" + getClient().hashCode());
		joiner.add("player=" + getClient().getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
