package fr.pederobien.vocal.server.impl;

import java.util.Optional;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.impl.AddressMessage;
import fr.pederobien.communication.impl.UdpServer;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.common.impl.Idc;
import fr.pederobien.vocal.common.impl.Oid;
import fr.pederobien.vocal.common.impl.VocalMessageExtractor;
import fr.pederobien.vocal.common.impl.VocalMessageFactory;
import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.common.impl.messages.v10.PlayerSpeakInfoMessageV10;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.event.PlayerSpeakEvent;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServer implements IVocalServer, IEventListener {
	private static final double EPSILON = Math.pow(10, -4);
	private String name;
	private int port;
	private PlayerList players;
	private UdpServer udpServer;
	private VocalMessageFactory factory;

	/**
	 * Creates a server for vocal communication between several players.
	 * 
	 * @param name The server name.
	 * @param port The server port number for the UDP communication.
	 */
	public VocalServer(String name, int port) {
		this.name = name;
		this.port = port;

		players = new PlayerList(name);
		udpServer = new UdpServer(getName(), getPort(), () -> new VocalMessageExtractor());
		factory = VocalMessageFactory.getInstance(10000);

		EventManager.registerListener(this);
	}

	@Override
	public void open() {
		udpServer.connect();
	}

	@Override
	public void close() {
		udpServer.disconnect();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public PlayerList getPlayers() {
		return players;
	}

	@EventHandler
	private void onDataReceived(DataReceivedEvent event) {
		if (!event.getConnection().equals(udpServer.getConnection()))
			return;

		// Verification of the structure of the bytes array.
		IVocalMessage message = factory.parse(event.getBuffer());
		if (message.getHeader().getIdc() != Idc.PLAYER_SPEAK || message.getHeader().getOid() != Oid.INFO)
			return;

		// Checking if the player is know by the server.
		PlayerSpeakInfoMessageV10 playerSpeakMessage = (PlayerSpeakInfoMessageV10) message;
		Optional<IVocalPlayer> optPlayer = getPlayers().get(playerSpeakMessage.getPlayerName());
		IVocalPlayer player = optPlayer.isPresent() ? optPlayer.get() : getPlayers().add(playerSpeakMessage.getPlayerName(), event.getAddress());

		// Dispatching the player speak event in order to specify to which person the transmitter speak
		PlayerSpeakEvent playerSpeakEvent = new PlayerSpeakEvent(this, player, playerSpeakMessage.getData());
		EventManager.callEvent(playerSpeakEvent);

		// Sending the audio sample to the concerned players.
		String transmitter = playerSpeakEvent.getTransmitter().getName();
		byte[] data = playerSpeakEvent.getData();
		playerSpeakEvent.getVolumes().keySet().parallelStream().forEach(p -> {
			// Checking volume before sending.
			VolumeResult volume = playerSpeakEvent.getVolumes().get(p);
			if (volume.getGlobal() < EPSILON)
				return;

			IVocalMessage response = factory.create(Idc.PLAYER_SPEAK, Oid.SET, transmitter, data, volume);
			udpServer.getConnection().send(new AddressMessage(response.generate(), response.getHeader().getIdentifier(), p.getAddress()));
		});
	}
}
