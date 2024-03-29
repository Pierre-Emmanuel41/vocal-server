package fr.pederobien.vocal.server.interfaces;

import java.net.InetSocketAddress;
import java.util.stream.Stream;

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
	 * Set the name of this player.
	 * 
	 * @param name The new player name.
	 */
	public void setName(String name);

	/**
	 * @return True if this player is mute, false otherwise.
	 */
	boolean isMute();

	/**
	 * Set the new mute status of this player.
	 * 
	 * @param isMute True if the player is mute, false otherwise.
	 */
	public void setMute(boolean isMute);

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
	 * @return A stream that contains players for which this player is mute.
	 */
	Stream<IVocalPlayer> getMuteByPlayers();

	/**
	 * @return True if this player is deafen, false otherwise.
	 */
	boolean isDeafen();

	/**
	 * Set the new deafen status of the player.
	 * 
	 * @param isDeafen True if the player is deafen, false otherwise.
	 */
	public void setDeafen(boolean isDeafen);

	/**
	 * @return The address used for the TCP communication.
	 */
	InetSocketAddress getTcpAddress();

	/**
	 * @return The address used for the UDP communication.
	 */
	InetSocketAddress getUdpAddress();
}
