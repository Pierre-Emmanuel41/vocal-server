package fr.pederobien.vocal.server.event;

import java.net.InetSocketAddress;
import java.util.StringJoiner;

import fr.pederobien.vocal.server.impl.PlayerVocalClient;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServerClientAddPostEvent extends VocalServerEvent {
	private PlayerVocalClient client;
	private InetSocketAddress address;

	/**
	 * Creates an event thrown when a new client has been created.
	 * 
	 * @param server The server on which the client has been created.
	 * @param client The created client.
	 */
	public VocalServerClientAddPostEvent(IVocalServer server, PlayerVocalClient client, InetSocketAddress address) {
		super(server);
		this.client = client;
		this.address = address;
	}

	/**
	 * @return The created client.
	 */
	public PlayerVocalClient getClient() {
		return client;
	}

	/**
	 * @return The address used to create a new client.
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("server=" + getServer().getName());
		joiner.add("client=#" + getClient().hashCode());
		joiner.add("address=" + getAddress());
		return String.format("%s_%s", getName(), joiner);
	}
}
