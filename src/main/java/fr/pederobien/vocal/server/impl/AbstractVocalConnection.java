package fr.pederobien.vocal.server.impl;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.interfaces.IConnection;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class AbstractVocalConnection {
	private IVocalServer server;
	private IConnection connection;
	private static float version;

	/**
	 * Creates a vocal connection in order to send or receive requests from the remote.
	 * 
	 * @param server     The server associated to this connection.
	 * @param connection The connection with the remote.
	 */
	public AbstractVocalConnection(IVocalServer server, IConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	/**
	 * @return The server associated to this vocal connection.
	 */
	public IVocalServer getServer() {
		return server;
	}

	/**
	 * @return The version of the communication protocol to use.
	 */
	protected float getVersion() {
		return version;
	}

	/**
	 * Set the communication protocol version.
	 * 
	 * @param version The new version of the communication protocol.
	 */
	protected void setVersion(float version) {
		AbstractVocalConnection.version = version;
	}

	/**
	 * @return The connection with the remote.
	 */
	protected IConnection getConnection() {
		return connection;
	}

	/**
	 * Set the connection with the remote.
	 * 
	 * @param connection The connection in order to send or receive requests from the remote.
	 */
	protected void setConnection(IConnection connection) {
		this.connection = connection;
	}

	protected IVocalMessage checkReceivedRequest(DataReceivedEvent event) {
		if (!event.getConnection().equals(getConnection()))
			return null;

		try {
			return VocalServerMessageFactory.parse(event.getBuffer());
		} catch (Exception e) {
			return null;
		}
	}
}
