package fr.pederobien.vocal.server.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class PlayerVocalClient extends AbstractVocalConnection implements IEventListener {
	private IVocalPlayer player;
	private AtomicBoolean isRegistered;

	/**
	 * Creates a client associated to a specific player.
	 * 
	 * @param server     The server associated to this connection.
	 * @param connection The TCP connection with the remote.
	 */
	public PlayerVocalClient(IVocalServer server, ITcpConnection connection) {
		super(server, connection);

		player = new VocalPlayer(server);
		isRegistered = new AtomicBoolean(false);

		EventManager.registerListener(this);
	}

	/**
	 * @return The player associated to this client.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}

	@Override
	protected void setTcpConnection(ITcpConnection connection) {
		super.setTcpConnection(connection);

		if (establishCommunicationProtocolVersion() && isRegistered.compareAndSet(false, true))
			EventManager.registerListener(this);
	}

	@EventHandler
	private void onUnexpectedDataReceived(UnexpectedDataReceivedEvent event) {
		IVocalMessage request = checkReceivedRequest(event);
		if (request == null)
			return;

		if (checkPermission(request))
			send(getServer().getRequestManager().answer(new RequestReceivedHolder(request, this)));
		else
			send(VocalServerMessageFactory.answer(request, VocalErrorCode.PERMISSION_REFUSED));
	}

	// @EventHandler
	// private void onConnectionLost(ConnectionLostEvent event) {
	// Always allow this request whatever the client state.
	// if (request.getHeader().getIdentifier() == Identifier.SET_SERVER_LEAVE) {
	// EventManager.callEvent(new ServerClientLeavePostEvent(getServer(), this));
	// send(MumbleServerMessageFactory.answer(request));
	// return;
	// }
	// }

	private boolean checkPermission(IVocalMessage request) {
		return true;
	}
}
