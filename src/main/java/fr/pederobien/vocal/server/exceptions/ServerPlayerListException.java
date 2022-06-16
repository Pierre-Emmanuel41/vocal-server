package fr.pederobien.vocal.server.exceptions;

import fr.pederobien.vocal.server.interfaces.IServerPlayerList;

public class ServerPlayerListException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private IServerPlayerList list;

	public ServerPlayerListException(String message, IServerPlayerList list) {
		super(message);
		this.list = list;
	}

	/**
	 * @return The list involved in this exception.
	 */
	public IServerPlayerList getList() {
		return list;
	}
}
