package fr.pederobien.vocal.server.event;

import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServerEvent extends ProjectVocalServerEvent {
	private IVocalServer server;

	/**
	 * Creates a vocal server event.
	 * 
	 * @param server The server source involved in this event.
	 */
	public VocalServerEvent(IVocalServer server) {
		this.server = server;
	}

	/**
	 * @return The server involved in this event.
	 */
	public IVocalServer getServer() {
		return server;
	}
}
