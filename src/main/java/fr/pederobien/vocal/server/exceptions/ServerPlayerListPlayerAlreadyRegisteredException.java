package fr.pederobien.vocal.server.exceptions;

import fr.pederobien.vocal.server.interfaces.IServerPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class ServerPlayerListPlayerAlreadyRegisteredException extends ServerPlayerListException {
	private static final long serialVersionUID = 1L;
	private IVocalPlayer player;

	public ServerPlayerListPlayerAlreadyRegisteredException(IServerPlayerList list, IVocalPlayer player) {
		super(String.format("The player %s is already registered on %s", player.getName(), list.getName()), list);
		this.player = player;
	}

	/**
	 * @return The already registered player.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}
}
