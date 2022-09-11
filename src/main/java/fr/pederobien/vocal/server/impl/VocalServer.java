package fr.pederobien.vocal.server.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import fr.pederobien.communication.impl.TcpServer;
import fr.pederobien.communication.impl.UdpServer;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.common.impl.VocalMessageExtractor;
import fr.pederobien.vocal.server.impl.request.ServerRequestManager;
import fr.pederobien.vocal.server.interfaces.IServerPlayerList;
import fr.pederobien.vocal.server.interfaces.IServerRequestManager;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServer implements IVocalServer, IEventListener {
	private TcpServer tcpServer;
	private UdpServer udpServer;
	private VocalAudioConnection audioConnection;
	private String name;
	private AtomicBoolean isOpened;
	private AtomicInteger port;
	private IServerPlayerList players;
	private IServerRequestManager serverRequestManager;
	private ClientList clients;
	private SpeakBehavior speakBehavior;

	/**
	 * Creates a server for vocal communication between several players.
	 * 
	 * @param name          The server name.
	 * @param port          The server port number for the UDP and TCP communication.
	 * @param speakBehavior the default server behavior when a player is speaking.
	 */
	public VocalServer(String name, int port, SpeakBehavior speakBehavior) {
		this.name = name;
		this.speakBehavior = speakBehavior;

		this.port = new AtomicInteger(port);
		tcpServer = new TcpServer(getName(), getPort(), () -> new VocalMessageExtractor(), true);
		udpServer = new UdpServer(getName(), getPort(), () -> new VocalMessageExtractor());
		isOpened = new AtomicBoolean(false);
		players = new ServerPlayerList(this);
		serverRequestManager = new ServerRequestManager(this);
		clients = new ClientList(this);

		EventManager.registerListener(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SpeakBehavior getSpeakBehavior() {
		return speakBehavior;
	}

	@Override
	public void open() {
		if (!isOpened.compareAndSet(false, true))
			return;

		tcpServer.connect();
		udpServer.connect();
		audioConnection = new VocalAudioConnection(this, udpServer.getConnection());
		clients.clear();
	}

	@Override
	public void close() {
		if (!isOpened.compareAndSet(true, false))
			return;

		tcpServer.disconnect();
		udpServer.disconnect();
		EventManager.unregisterListener(clients);
	}

	@Override
	public boolean isOpened() {
		return isOpened.get();
	}

	@Override
	public int getPort() {
		return port.get();
	}

	@Override
	public IServerPlayerList getPlayers() {
		return players;
	}

	@Override
	public IServerRequestManager getRequestManager() {
		return serverRequestManager;
	}

	/**
	 * @return The TCP server on which configuration request are sent.
	 */
	protected TcpServer getTcpServer() {
		return tcpServer;
	}

	/**
	 * @return The audio server the receive/send audio samples.
	 */
	protected VocalAudioConnection getAudioConnection() {
		return audioConnection;
	}
}
