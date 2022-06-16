package fr.pederobien.vocal.server.impl;

import java.util.Optional;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.impl.AddressMessage;
import fr.pederobien.communication.impl.UdpServer;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.VocalMessageExtractor;
import fr.pederobien.vocal.common.impl.VocalMessageFactory;
import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.common.impl.messages.v10.PlayerSpeakInfoMessageV10;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.event.PlayerSpeakEvent;
import fr.pederobien.vocal.server.interfaces.IServerPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalServer implements IVocalServer, IEventListener {
	private static final double EPSILON = Math.pow(10, -4);
	private String name;
	private int port;
	private IServerPlayerList players;
	private UdpServer udpServer;
	private VocalMessageFactory factory;

	/**
	 * Creates a server for vocal communication between several players.
	 * 
	 * @param name The server name.
	 * @param port The server port number for the UDP and TCP communication.
	 */
	public VocalServer(String name, int port) {
		this.name = name;
		this.port = port;

		players = new ServerPlayerList(this);
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
	public IServerPlayerList getPlayers() {
		return players;
	}

	@EventHandler
	private void onDataReceived(DataReceivedEvent event) {
		if (!event.getConnection().equals(udpServer.getConnection()))
			return;

		// Verification of the structure of the bytes array.
		IVocalMessage message = factory.parse(event.getBuffer());
		if (message.getHeader().getIdentifier() != VocalIdentifier.PLAYER_SPEAK_INFO)
			return;

		// Checking if the player is know by the server.
		PlayerSpeakInfoMessageV10 playerSpeakMessage = (PlayerSpeakInfoMessageV10) message;
		Optional<IVocalPlayer> optPlayer = getPlayers().get(playerSpeakMessage.getPlayerName());

		if (!optPlayer.isPresent())
			return;

		IVocalPlayer player = optPlayer.get();

		// Dispatching the player speak event in order to specify to which person the transmitter speak
		PlayerSpeakEvent playerSpeakEvent = new PlayerSpeakEvent(this, player, playerSpeakMessage.getData());
		EventManager.callEvent(playerSpeakEvent);

		// Sending the audio sample to the concerned players.
		IVocalPlayer transmitter = playerSpeakEvent.getTransmitter();
		byte[] data = playerSpeakEvent.getData();
		playerSpeakEvent.getVolumes().keySet().parallelStream().forEach(receiver -> {

			// Checking if the receiver can accept audio sample from the transmitter
			if (!receiver.isOnline() || receiver.isDeafen() || transmitter.isMuteBy(receiver))
				return;

			// Checking volume before sending.
			VolumeResult volume = playerSpeakEvent.getVolumes().get(receiver);
			if (volume == null || volume.getGlobal() < EPSILON)
				return;

			IVocalMessage response = factory.create(VocalIdentifier.PLAYER_SPEAK_SET, transmitter.getName(), data, volume);
			udpServer.getConnection().send(new AddressMessage(response.generate(), response.getHeader().getSequence(), receiver.getAddress()));
		});
	}
}
