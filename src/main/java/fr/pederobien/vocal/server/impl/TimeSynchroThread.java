package fr.pederobien.vocal.server.impl;

import java.time.LocalTime;

public class TimeSynchroThread extends Thread {
	private ClientList clients;

	/**
	 * Creates a thread in order to time-synchronize all the clients.
	 * 
	 * @param clients
	 */
	public TimeSynchroThread(ClientList clients) {
		super("TimeSynchronizer");
		this.clients = clients;

		setDaemon(true);
		setPriority(MAX_PRIORITY);
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				Thread.sleep(5000);
				clients.stream().parallel().forEach(client -> client.synchronize(LocalTime.now()));
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
