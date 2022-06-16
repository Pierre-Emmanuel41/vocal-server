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

import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.server.event.VocalPlayerNameChangePostEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerAddPostEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerAddPreEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerRemovePostEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerRemovePreEvent;
import fr.pederobien.vocal.server.exceptions.ServerPlayerListPlayerAlreadyRegisteredException;
import fr.pederobien.vocal.server.interfaces.IServerPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class ServerPlayerList implements IServerPlayerList, IEventListener {
	private IVocalServer server;
	private Map<String, IVocalPlayer> players;
	private Lock lock;

	/**
	 * Creates a player list associated to the given server.
	 * 
	 * @param server The server to which this list is attached.
	 */
	public ServerPlayerList(IVocalServer server) {
		this.server = server;

		players = new HashMap<String, IVocalPlayer>();
		lock = new ReentrantLock(true);

		EventManager.registerListener(this);
	}

	@Override
	public Iterator<IVocalPlayer> iterator() {
		return players.values().iterator();
	}

	@Override
	public IVocalServer getServer() {
		return server;
	}

	@Override
	public String getName() {
		return server.getName();
	}

	@Override
	public IVocalPlayer add(String name, InetSocketAddress address, boolean isMute, boolean isDeafen) {
		Optional<IVocalPlayer> optPlayer = get(name);
		if (optPlayer.isPresent())
			throw new ServerPlayerListPlayerAlreadyRegisteredException(server.getPlayers(), optPlayer.get());

		IVocalPlayer player = new VocalPlayer(getServer(), name, address, isMute, isDeafen);
		VocalServerPlayerAddPreEvent preEvent = new VocalServerPlayerAddPreEvent(this, player);
		EventManager.callEvent(preEvent, () -> addPlayer(player));
		return preEvent.isCancelled() ? null : player;
	}

	@Override
	public IVocalPlayer remove(String name) {
		Optional<IVocalPlayer> optPlayer = get(name);
		if (!optPlayer.isPresent())
			return null;

		VocalServerPlayerRemovePreEvent preEvent = new VocalServerPlayerRemovePreEvent(this, optPlayer.get());
		EventManager.callEvent(preEvent, () -> removePlayer(name));
		return preEvent.isCancelled() ? null : optPlayer.get();
	}

	@Override
	public boolean remove(IVocalPlayer player) {
		return remove(player.getName()) != null;
	}

	@Override
	public void clear() {
		lock.lock();
		try {
			Set<String> names = new HashSet<String>(players.keySet());
			for (String name : names)
				EventManager.callEvent(new VocalServerPlayerRemovePostEvent(this, players.remove(name)));
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

	@EventHandler
	private void onPlayerNameChange(VocalPlayerNameChangePostEvent event) {
		Optional<IVocalPlayer> optOldPlayer = get(event.getOldName());
		if (!optOldPlayer.isPresent())
			return;

		Optional<IVocalPlayer> optNewPlayer = get(event.getPlayer().getName());
		if (optNewPlayer.isPresent())
			throw new ServerPlayerListPlayerAlreadyRegisteredException(server.getPlayers(), optNewPlayer.get());

		lock.lock();
		try {
			IVocalPlayer player = players.remove(event.getOldName());
			players.put(player.getName(), player);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation that consists in adding the given player to this list.
	 * 
	 * @param player The player to add.
	 */
	private void addPlayer(IVocalPlayer player) {
		lock.lock();
		try {
			players.put(player.getName(), player);
		} finally {
			lock.unlock();
		}

		EventManager.callEvent(new VocalServerPlayerAddPostEvent(this, player));
	}

	/**
	 * Thread safe operation that consists in removing the player associated to the given name.
	 * 
	 * @param name The name of the player to remove.
	 * 
	 * @return The remove player if registered, null otherwise.
	 */
	private IVocalPlayer removePlayer(String name) {
		lock.lock();
		IVocalPlayer player = null;
		try {
			player = players.remove(name);
		} finally {
			lock.unlock();
		}

		if (player != null)
			EventManager.callEvent(new VocalServerPlayerRemovePostEvent(this, player));

		return player;
	}
}
