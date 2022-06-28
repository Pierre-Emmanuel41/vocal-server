package fr.pederobien.vocal.server.event;

import fr.pederobien.vocal.server.impl.PlayerVocalClient;

public class PlayerVocalClientEvent extends ProjectVocalServerEvent {
	private PlayerVocalClient client;

	/**
	 * Creates a client event.
	 * 
	 * @param client The client source involved in this event.
	 */
	public PlayerVocalClientEvent(PlayerVocalClient client) {
		this.client = client;
	}

	/**
	 * @return The client involved in this event.
	 */
	public PlayerVocalClient getClient() {
		return client;
	}
}
