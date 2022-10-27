package fr.pederobien.vocal.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import fr.pederobien.communication.event.NewTcpClientEvent;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.server.event.VocalClientDisconnectPostEvent;
import fr.pederobien.vocal.server.event.VocalServerClientAddPostEvent;
import fr.pederobien.vocal.server.event.VocalServerClientRemovePostEvent;

public class ClientList implements IEventListener {
	private VocalServer server;
	private List<PlayerVocalClient> clients;
	private Lock lock;

	/**
	 * Creates a clients list associated to a vocal server. A client is an intermediate object used to gather information about the
	 * player from the game.
	 * 
	 * @param server The server associated to this clients list.
	 */
	public ClientList(VocalServer server) {
		this.server = server;
		clients = new ArrayList<PlayerVocalClient>();
		lock = new ReentrantLock(true);

		EventManager.registerListener(this);
	}

	/**
	 * @return A copy of the the underlying list that stores clients.
	 */
	public List<PlayerVocalClient> toList() {
		lock.lock();
		try {
			return new ArrayList<PlayerVocalClient>(clients);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @return a sequential {@code Stream} over the elements in this collection.
	 */
	public Stream<PlayerVocalClient> stream() {
		return toList().stream();
	}

	/**
	 * Get the client associated to the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return An optional that contains the client associated to the specified name if registered, an empty optional otherwise.
	 */
	public Optional<PlayerVocalClient> get(String name) {
		lock.lock();
		try {
			for (PlayerVocalClient client : clients)
				if (client.getPlayer() != null && client.getPlayer().getName().equals(name))
					return Optional.of(client);
			return Optional.empty();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation to remove all clients from this list.
	 */
	public void clear() {
		lock.lock();
		try {
			clients.clear();
		} finally {
			lock.unlock();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onNewClient(NewTcpClientEvent event) {
		if (!event.getServer().equals(server.getTcpServer()))
			return;

		createClient(event.getConnection());
	}

	@EventHandler
	private void onClientDisconnect(VocalClientDisconnectPostEvent event) {
		if (!event.getClient().getServer().equals(server))
			return;

		removeClient(event.getClient());
	}

	private PlayerVocalClient createClient(ITcpConnection connection) {
		lock.lock();
		try {
			PlayerVocalClient client = new PlayerVocalClient(server, connection);
			clients.add(client);
			client.setConnection(connection);
			EventManager.callEvent(new VocalServerClientAddPostEvent(server, client, connection.getAddress()));
			return client;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Thread safe operation to remove the given client from the registered clients list.
	 * 
	 * @param client The client to remove.
	 */
	private void removeClient(PlayerVocalClient client) {
		lock.lock();
		boolean removed = false;
		try {
			removed = clients.remove(client);
		} finally {
			lock.unlock();
		}

		if (removed)
			EventManager.callEvent(new VocalServerClientRemovePostEvent(server, client));
	}
}
