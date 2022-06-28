package fr.pederobien.vocal.server.event;

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
}
