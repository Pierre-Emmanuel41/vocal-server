package fr.pederobien.vocal.server.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.communication.event.NewTcpClientEvent;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.server.event.VocalServerClientAddPostEvent;

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

		EventManager.callEvent(new LogEvent("Creating clients list"));
		EventManager.registerListener(this);
	}

	/**
	 * Get the client associated to the given name.
	 * 
	 * @param name The player name.
	 * 
	 * @return An optional that contains the client associated to the specified name if registered, an empty optional otherwise.
	 */
	public Optional<PlayerVocalClient> get(String name) {
		for (PlayerVocalClient client : clients)
			if (client.getPlayer() != null && client.getPlayer().getName().equals(name))
				return Optional.of(client);
		return Optional.empty();
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
		EventManager.callEvent(new LogEvent("Registering a new client"));
		if (!event.getServer().equals(server.getTcpServer()))
			return;

		createClient(event.getConnection());
	}

	private PlayerVocalClient createClient(ITcpConnection connection) {
		lock.lock();
		try {
			PlayerVocalClient client = new PlayerVocalClient(server, connection);
			clients.add(client);
			client.setTcpConnection(connection);
			EventManager.callEvent(new VocalServerClientAddPostEvent(server, client, connection.getAddress()));
			return client;
		} finally {
			lock.unlock();
		}
	}
}
