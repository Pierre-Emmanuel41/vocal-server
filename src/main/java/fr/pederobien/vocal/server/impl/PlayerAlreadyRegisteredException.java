package fr.pederobien.vocal.server.impl;

import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class PlayerAlreadyRegisteredException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PlayerAlreadyRegisteredException(IVocalPlayer player, String serverName) {
		super(String.format("The player %s is already registered on server %s", player.getName(), serverName));
	}
}
