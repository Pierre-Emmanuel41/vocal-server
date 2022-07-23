package fr.pederobien.vocal.server.impl;

import java.net.InetSocketAddress;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.communication.interfaces.IUdpConnection;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.common.impl.VocalAddressMessage;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.event.VocalPlayerSpeakEvent;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalAudioConnection extends AbstractVocalConnection implements IEventListener {
	private static final double EPSILON = Math.pow(10, -5);

	/**
	 * Creates a vocal connection in order to send or receive requests from the remote.
	 * 
	 * @param server     The server associated to this connection.
	 * @param connection The UDP connection with the remote.
	 */
	public VocalAudioConnection(IVocalServer server, IUdpConnection connection) {
		super(server, connection);

		EventManager.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerSpeak(VocalPlayerSpeakEvent event) {
		IVocalPlayer transmitter = event.getTransmitter();
		byte[] data = event.getData();
		boolean isMono = event.isMono();
		boolean isEncoded = event.isEncoded();
		event.getVolumes().keySet().parallelStream().filter(receiver -> receiver.getUdpAddress() != null).forEach(receiver -> {

			// Checking if the receiver can accept audio sample from the transmitter
			if (receiver.isDeafen() || transmitter.isMuteBy(receiver))
				return;

			// Checking volume before sending.
			VolumeResult volume = event.getVolumes().get(receiver);
			if (volume == null || volume.getGlobal() < EPSILON)
				return;

			send(getServer().getRequestManager().onPlayerSpeak(getVersion(), transmitter, data, isMono, isEncoded, volume), receiver.getUdpAddress());
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onUnexpectedDataReceived(DataReceivedEvent event) {
		IVocalMessage request = checkReceivedRequest(event);

		if (request == null || request.getHeader().getIdentifier() != VocalIdentifier.PLAYER_SPEAK_INFO)
			return;

		getServer().getRequestManager().answer(new RequestReceivedHolder(this, event, request));
	}

	/**
	 * Send a request to the remote.
	 * 
	 * @param message The request to send to the remote.
	 * @param address The address to which the message should be sent.
	 */
	protected void send(IVocalMessage message, InetSocketAddress address) {
		if (message == null || getConnection() == null || getConnection().isDisposed())
			return;

		((IUdpConnection) getConnection()).send(new VocalAddressMessage(message, address));
	}
}
