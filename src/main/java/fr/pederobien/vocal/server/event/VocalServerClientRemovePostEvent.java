package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.impl.PlayerVocalClient;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServerClientRemovePostEvent extends VocalServerEvent {
	private PlayerVocalClient client;

	/**
	 * Creates an event thrown when the client is removed from the client list associated to the given server. This event is thrown
	 * when the player is no more connected in game and the connection with the mumble client has been lost. If one of the two
	 * connections is still alive then this event is not thrown.
	 * 
	 * @param server The server from which the client has been removed..
	 * @param client The removed client.
	 */
	public VocalServerClientRemovePostEvent(IVocalServer server, PlayerVocalClient client) {
		super(server);
		this.client = client;
	}

	/**
	 * @return The remove client.
	 */
	public PlayerVocalClient getClient() {
		return client;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("client=#" + getClient().hashCode());
		return String.format("%s_%s", getName(), joiner);
	}
}
