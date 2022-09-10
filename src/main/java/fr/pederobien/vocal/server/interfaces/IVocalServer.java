package fr.pederobien.vocal.server.interfaces;

import fr.pederobien.vocal.server.impl.SpeakBehavior;

public interface IVocalServer {

	/**
	 * @return The name of this server.
	 */
	String getName();

	/**
	 * @return The default server behavior when a player is speaking.
	 */
	SpeakBehavior getSpeakBehavior();

	/**
	 * Open this server in order to accept players.
	 */
	void open();

	/**
	 * Close this server. Each players will be kicked.
	 */
	void close();

	/**
	 * @return If the server has been opened or the method {@link #close()} has not been called.
	 */
	boolean isOpened();

	/**
	 * @return The port number for the TCP and UDP communication.
	 */
	int getPort();

	/**
	 * @return A list that contains players registered on this server.
	 */
	IServerPlayerList getPlayers();

	/**
	 * @return The manager responsible to create messages to send to the remote.
	 */
	IServerRequestManager getRequestManager();
}
