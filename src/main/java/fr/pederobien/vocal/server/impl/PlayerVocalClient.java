package fr.pederobien.vocal.server.impl;

import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class PlayerVocalClient extends AbstractVocalConnection implements IEventListener {

	/**
	 * Creates a client associated to a specific player.
	 * 
	 * @param server     The server associated to this connection.
	 * @param connection The TCP connection with the remote.
	 */
	public PlayerVocalClient(IVocalServer server, ITcpConnection connection) {
		super(server, connection);

		EventManager.registerListener(this);
	}
}
