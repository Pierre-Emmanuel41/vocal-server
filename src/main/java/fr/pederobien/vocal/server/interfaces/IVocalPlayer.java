package fr.pederobien.vocal.server.interfaces;

import java.net.InetSocketAddress;

public interface IVocalPlayer {

	/**
	 * @return The server on which this player is registered.
	 */
	IVocalServer getServer();

	/**
	 * @return The name of this player
	 */
	String getName();

	/**
	 * @return True if this player is connected to this server.
	 */
	boolean isOnline();

	/**
	 * Set the online status of a player.
	 * 
	 * @param isOnline The new player's online status.
	 */
	void setOnline(boolean isOnline);

	/**
	 * @return True if this player is mute, false otherwise.
	 */
	boolean isMute();

	/**
	 * Indicates if this player is mute for the given player.
	 * 
	 * @param player The player to check.
	 * 
	 * @return True if this player is mute for the given player, false otherwise.
	 */
	boolean isMuteBy(IVocalPlayer player);

	/**
	 * Set if this player is mute for another player.
	 * 
	 * @param player The other player for which this player is mute.
	 * @param isMute True if this player is mute for the other player, false otherwise.
	 */
	void setMuteBy(IVocalPlayer player, boolean isMute);

	/**
	 * @return True if this player is deafen, false otherwise.
	 */
	boolean isDeafen();

	/**
	 * @return The address used for the TCP communication.
	 */
	InetSocketAddress getTcpAddress();

	/**
	 * @return The address used for the UDP communication.
	 */
	InetSocketAddress getUdpAddress();
}
