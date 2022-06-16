package fr.pederobien.vocal.server.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import fr.pederobien.utils.event.EventManager;
import fr.pederobien.vocal.server.event.VocalPlayerListPlayerAddPostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerListPlayerRemovePostEvent;
import fr.pederobien.vocal.server.interfaces.IPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class PlayerList implements IPlayerList {
	private IVocalServer server;
	private Map<String, IVocalPlayer> players;
	private Lock lock;

	/**
	 * Creates a list in order to store players.
	 * 
	 * @param name The list name.
	 */
	protected PlayerList(IVocalServer server) {
		this.server = server;

		players = new HashMap<String, IVocalPlayer>();
		lock = new ReentrantLock(true);
	}

	@Override
	public Iterator<IVocalPlayer> iterator() {
		return players.values().iterator();
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public boolean remove(String name) {
		IVocalPlayer player = removePlayer(name);
		if (player != null)
			EventManager.callEvent(new VocalPlayerListPlayerRemovePostEvent(this, player));
		return player != null;
	}

	@Override
	public void clear() {
		lock.lock();
		try {
			Set<String> names = new HashSet<String>(players.keySet());
			for (String name : names)
				EventManager.callEvent(new VocalPlayerListPlayerRemovePostEvent(this, players.remove(name)));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Optional<IVocalPlayer> get(String name) {
		return Optional.ofNullable(players.get(name));
	}

	@Override
	public Stream<IVocalPlayer> stream() {
		return list().stream();
	}

	@Override
	public List<IVocalPlayer> list() {
		return new ArrayList<IVocalPlayer>(players.values());
	}

	/**
	 * Creates a player based on the given name and address and register it in the underlying list of players.
	 * 
	 * @param name    The player name.
	 * @param address The player address.
	 * 
	 * @return The created player.
	 */
	protected IVocalPlayer add(String name, InetSocketAddress address) {
		IVocalPlayer player = addPlayer(name, address);
		EventManager.callEvent(new VocalPlayerListPlayerAddPostEvent(this, player));
		return player;
	}

	/**
	 * Creates a player based on the given name and address and register it thread safely in the underlying list of players.
	 * 
	 * @param name    The player name.
	 * @param address The player address.
	 * 
	 * @return The created player.
	 */
	private IVocalPlayer addPlayer(String name, InetSocketAddress address) {
		lock.lock();
		try {
			IVocalPlayer player = players.get(name);
			if (player != null)
				throw new PlayerAlreadyRegisteredException(player, getName());

			player = new VocalPlayer(server, name, address, false, false);
			players.put(player.getName(), player);
			return player;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Removes thread safely a player registered under the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return The player if registered, null otherwise.
	 */
	private IVocalPlayer removePlayer(String name) {
		lock.lock();
		try {
			return players.remove(name);
		} finally {
			lock.unlock();
		}
	}
}
