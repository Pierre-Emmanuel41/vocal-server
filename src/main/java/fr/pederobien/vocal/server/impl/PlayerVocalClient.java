package fr.pederobien.vocal.server.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import fr.pederobien.communication.event.ConnectionLostEvent;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.interfaces.IConnection;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventHandler;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.EventPriority;
import fr.pederobien.utils.event.IEventListener;
import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.event.VocalClientDisconnectPostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerDeafenChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerMuteByChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerMuteChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerNameChangePostEvent;
import fr.pederobien.vocal.server.event.VocalServerClientJoinPostEvent;
import fr.pederobien.vocal.server.event.VocalServerClientLeavePostEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerAddPostEvent;
import fr.pederobien.vocal.server.event.VocalServerPlayerRemovePostEvent;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class PlayerVocalClient extends AbstractTcpVocalConnection implements IEventListener {
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
	protected void setConnection(IConnection connection) {
		super.setConnection((ITcpConnection) connection);

		if (establishCommunicationProtocolVersion() && isRegistered.compareAndSet(false, true))
			EventManager.registerListener(this);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerAdd(VocalServerPlayerAddPostEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		doIfPlayerJoined(() -> send(getServer().getRequestManager().onServerPlayerAdd(getVersion(), event.getPlayer())));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onServerPlayerRemove(VocalServerPlayerRemovePostEvent event) {
		if (!event.getList().getServer().equals(getServer()))
			return;

		doIfPlayerJoined(() -> send(getServer().getRequestManager().onServerPlayerRemove(getVersion(), event.getPlayer())));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerNameChange(VocalPlayerNameChangePostEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		doIfPlayerJoined(() -> send(getServer().getRequestManager().onPlayerNameChange(getVersion(), event.getOldName(), event.getPlayer().getName())));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteChange(VocalPlayerMuteChangePostEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		doIfPlayerJoined(() -> send(getServer().getRequestManager().onPlayerMuteChange(getVersion(), event.getPlayer())));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerMuteByChange(VocalPlayerMuteByChangePostEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()) || !event.getSource().equals(player))
			return;

		doIfPlayerJoined(() -> send(getServer().getRequestManager().onPlayerMuteByChange(getVersion(), event.getPlayer(), event.getSource())));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDeafenChange(VocalPlayerDeafenChangePostEvent event) {
		if (!event.getPlayer().getServer().equals(getServer()))
			return;

		doIfPlayerJoined(() -> send(getServer().getRequestManager().onPlayerDeafenChange(getVersion(), event.getPlayer())));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onUnexpectedDataReceived(UnexpectedDataReceivedEvent event) {
		IVocalMessage request = checkReceivedRequest(event);
		if (request == null)
			return;

		// Always allow this request whatever the client state.
		if (request.getHeader().getIdentifier() == VocalIdentifier.SET_SERVER_LEAVE) {
			isJoined.set(false);
			EventManager.callEvent(new VocalServerClientLeavePostEvent(getServer(), this));
			send(VocalServerMessageFactory.answer(request));
			player = null;
			return;
		}

		if (checkPermission(request))
			send(getServer().getRequestManager().answer(new RequestReceivedHolder(this, event, request)));
		else
			send(VocalServerMessageFactory.answer(request, VocalErrorCode.PERMISSION_REFUSED));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void OnConnectionLostEvent(ConnectionLostEvent event) {
		if (!event.getConnection().equals(getConnection()))
			return;

		isJoined.set(false);
		getConnection().dispose();
		EventManager.callEvent(new VocalClientDisconnectPostEvent(this));
		EventManager.unregisterListener(this);
	}

	private boolean checkPermission(IVocalMessage request) {
		if (request.getHeader().getIdentifier() == VocalIdentifier.SET_SERVER_JOIN)
			return true;

		if (!isJoined.get())
			return false;

		switch (request.getHeader().getIdentifier()) {
		case GET_SERVER_CONFIGURATION:
		case SET_PLAYER_NAME:
		case SET_PLAYER_MUTE:
		case SET_PLAYER_MUTE_BY:
		case SET_PLAYER_DEAFEN:
			return true;
		default:
			return false;
		}
	}

	private void doIfPlayerJoined(Runnable runnable) {
		if (isJoined.get())
			runnable.run();
	}
}
