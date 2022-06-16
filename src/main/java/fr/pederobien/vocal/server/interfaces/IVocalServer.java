package fr.pederobien.vocal.server.interfaces;

public interface IVocalServer {

	/**
	 * Open this server in order to accept players.
	 */
	void open();

	/**
	 * Close this server. Each players will be kicked.
	 */
	void close();

	/**
	 * @return The name of this server.
	 */
	String getName();

	/**
	 * @return The number for the UDP communication.
	 */
	int getPort();

	/**
	 * @return A list that contains players registered on this server.
	 */
	IServerPlayerList getPlayers();
}
