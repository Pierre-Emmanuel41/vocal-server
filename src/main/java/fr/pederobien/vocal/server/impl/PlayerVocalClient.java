package fr.pederobien.vocal.server.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.event.VocalServerClientJoinPostEvent;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class PlayerVocalClient extends AbstractVocalConnection implements IEventListener {
	private IVocalPlayer player;
	private AtomicBoolean isRegistered;
	private AtomicBoolean isJoined;

	/**
	 * Creates a client associated to a specific player.
	 * 
	 * @param server     The server associated to this connection.
	 * @param connection The TCP connection with the remote.
	 */
	public PlayerVocalClient(IVocalServer server, ITcpConnection connection) {
		super(server, connection);

		isRegistered = new AtomicBoolean(false);
		isJoined = new AtomicBoolean(false);

		EventManager.registerListener(this);
	}

	/**
	 * @return The player associated to this client.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}

	/**
	 * Creates a player associated to the given name.
	 * 
	 * @param name     The player's name.
	 * @param isMute   The player's mute status.
	 * @param isDeafen The player's deafen status.
	 * 
	 * @return True if the player has been created, false otherwise.
	 */
	public boolean join(String name, boolean isMute, boolean isDeafen) {
		if (!isJoined.compareAndSet(false, true))
			return false;

		player = new VocalPlayer(getServer(), name, isMute, isDeafen);
		EventManager.callEvent(new VocalServerClientJoinPostEvent(getServer(), this));
		return true;
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
		if (request.getHeader().getIdentifier() == VocalIdentifier.SET_SERVER_JOIN)
			return true;

		if (!isJoined.get())
			return false;

		return false;
	}
}
