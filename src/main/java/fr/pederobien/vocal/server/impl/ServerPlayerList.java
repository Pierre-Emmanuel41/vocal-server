package fr.pederobien.vocal.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.server.event.VocalClientDisconnectPostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerNameChangePostEvent;
import fr.pederobien.vocal.server.event.VocalServerClientJoinPostEvent;
import fr.pederobien.vocal.server.event.VocalServerClientLeavePostEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerAddPostEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerRemovePostEvent;
import fr.pederobien.vocal.server.exceptions.ServerPlayerListPlayerAlreadyRegisteredException;
import fr.pederobien.vocal.server.interfaces.IServerPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class ServerPlayerList implements IServerPlayerList, IEventListener {
	private VocalServer server;
	private Map<String, IVocalPlayer> players;
	private Lock lock;

	/**
	 * Creates a player list associated to the given server.
	 * 
	 * @param server The server to which this list is attached.
	 */
	public ServerPlayerList(VocalServer server) {
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
	public Optional<IVocalPlayer> get(String name) {
		return Optional.ofNullable(players.get(name));
	}

	@Override
	public Stream<IVocalPlayer> stream() {
		return toList().stream();
	}

	@Override
	public List<IVocalPlayer> toList() {
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

	@EventHandler
	private void onClientJoin(VocalServerClientJoinPostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		addPlayer(event.getClient().getPlayer());
	}

	@EventHandler
	private void onClientLeave(VocalServerClientLeavePostEvent event) {
		if (!event.getServer().equals(getServer()))
			return;

		removePlayer(event.getClient().getPlayer());
	}

	@EventHandler
	private void onClientDisconnect(VocalClientDisconnectPostEvent event) {
		if (!event.getClient().getServer().equals(getServer()))
			return;

		removePlayer(event.getClient().getPlayer());
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
	 * @param player The player to remove.
	 */
	private void removePlayer(IVocalPlayer player) {
		lock.lock();
		try {
			if (players.remove(player.getName()) != null)
				EventManager.callEvent(new VocalServerPlayerRemovePostEvent(this, player));
		} finally {
			lock.unlock();
		}
	}
}
