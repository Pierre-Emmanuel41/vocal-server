package fr.pederobien.vocal.server.event;

import fr.pederobien.vocal.server.interfaces.IServerPlayerList;

public class VocalServerPlayerListEvent extends ProjectVocalServerEvent {
	private IServerPlayerList list;

	/**
	 * Creates a server player list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public VocalServerPlayerListEvent(IServerPlayerList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public IServerPlayerList getList() {
		return list;
	}
}
