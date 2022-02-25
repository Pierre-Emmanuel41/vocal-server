package fr.pederobien.vocal.server.event;

import fr.pederobien.vocal.server.interfaces.IPlayerList;

public class VocalPlayerListEvent extends ProjectVocalServerEvent {
	private IPlayerList list;

	/**
	 * Creates a player list event.
	 * 
	 * @param list The list source involved in this event.
	 */
	public VocalPlayerListEvent(IPlayerList list) {
		this.list = list;
	}

	/**
	 * @return The list involved in this event.
	 */
	public IPlayerList getList() {
		return list;
	}
}
