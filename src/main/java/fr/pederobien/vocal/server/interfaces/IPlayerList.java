package fr.pederobien.vocal.server.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface IPlayerList extends Iterable<IVocalPlayer> {

	/**
	 * @return The name of this player list.
	 */
	String getName();

	/**
	 * Removes the player associated to the given name.
	 * 
	 * @param name The name of the player to remove.
	 * 
	 * @return True if the player was registered, false otherwise.
	 */
	boolean remove(String name);

	/**
	 * Removes each registered player from this list.
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
