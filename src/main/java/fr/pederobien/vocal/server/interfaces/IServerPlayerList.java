package fr.pederobien.vocal.server.interfaces;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface IServerPlayerList extends Iterable<IVocalPlayer> {

	/**
	 * @return The server to which this list is associated.
	 */
	IVocalServer getServer();

	/**
	 * @return The name of this player list.
	 */
	String getName();

	/**
	 * Creates a player and register it.
	 * 
	 * @param name     The player's name.
	 * @param address  The player's address.
	 * @param isMute   The player's mute status.
	 * @param isDeafen The player's deafen status.
	 * 
	 * @return The created player.
	 */
	IVocalPlayer add(String name, InetSocketAddress address, boolean isMute, boolean isDeafen);

	/**
	 * Removes the player associated to the given name.
	 * 
	 * @param name The player name to remove.
	 * 
	 * @return The removed player if registered, null otherwise.
	 */
	IVocalPlayer remove(String name);

	/**
	 * Removes the given player from this list.
	 * 
	 * @param player The player to remove.
	 * 
	 * @return True if the player was registered, false otherwise.
	 */
	boolean remove(IVocalPlayer player);

	/**
	 * Removes all registered players.
	 */
	void clear();

	/**
	 * Get the player associated to the given name.
	 * 
	 * @param name The name of the player to retrieve?
	 * 
	 * @return An optional that contains the player if registered, an empty optional otherwise.
	 */
	Optional<IVocalPlayer> get(String name);

	/**
	 * @return A sequential {@code Stream} with this list as its source.
	 */
	Stream<IVocalPlayer> stream();

	/**
	 * @return A copy of the underlying player list.
	 */
	List<IVocalPlayer> list();
}
