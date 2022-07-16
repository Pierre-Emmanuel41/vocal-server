package fr.pederobien.vocal.server.impl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.vocal.server.event.VocalPlayerDeafenChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerDeafenChangePreEvent;
import fr.pederobien.vocal.server.event.VocalPlayerMuteByChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerMuteByChangePreEvent;
import fr.pederobien.vocal.server.event.VocalPlayerMuteChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerMuteChangePreEvent;
import fr.pederobien.vocal.server.event.VocalPlayerNameChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerNameChangePreEvent;
import fr.pederobien.vocal.server.event.VocalPlayerOnlineChangePostEvent;
import fr.pederobien.vocal.server.event.VocalPlayerOnlineChangePreEvent;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalPlayer implements IVocalPlayer {
	private IVocalServer server;
	private String name;
	private boolean isOnline, isMute, isDeafen;
	private Map<IVocalPlayer, Boolean> isMuteBy;
	private ITcpConnection tcpConnection;
	private InetSocketAddress udpAddress;
	private Lock lock;

	/**
	 * Creates a vocal player based on the following parameters.
	 * 
	 * @param server   The server on which this player is registered.
	 * @param name     The name of this player.
	 * @param isMute   True if the player is mute, false otherwise.
	 * @param isDeafen True if the player is deafen, false otherwise.
	 */
	public VocalPlayer(IVocalServer server, String name, boolean isMute, boolean isDeafen) {
		this.server = server;
		this.name = name;

		this.isMute = isMute;
		this.isDeafen = isDeafen;
		isMuteBy = new HashMap<IVocalPlayer, Boolean>();
		lock = new ReentrantLock(true);
	}

	@Override
	public IVocalServer getServer() {
		return server;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isOnline() {
		return isOnline;
	}

	@Override
	public void setOnline(boolean isOnline) {
		lock.lock();
		try {
			if (this.isOnline == isOnline)
				return;

			boolean oldOnline = this.isOnline;
			EventManager.callEvent(new VocalPlayerOnlineChangePreEvent(this, isOnline), () -> this.isOnline = isOnline,
					new VocalPlayerOnlineChangePostEvent(this, oldOnline));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isMute() {
		return isMute;
	}

	@Override
	public boolean isMuteBy(IVocalPlayer player) {
		Boolean isMute = isMuteBy.get(player);
		return isMute == null ? false : isMute;
	}

	@Override
	public void setMuteBy(IVocalPlayer player, boolean isMute) {
		lock.lock();
		try {
			boolean isMuteby = isMuteBy(player);
			if (isMuteby == isMute)
				return;

			EventManager.callEvent(new VocalPlayerMuteByChangePreEvent(this, player, isMute), () -> setMuteBy0(player, isMute));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isDeafen() {
		return isDeafen;
	}

	@Override
	public InetSocketAddress getTcpAddress() {
		return tcpConnection.getAddress();
	}

	@Override
	public InetSocketAddress getUdpAddress() {
		return udpAddress;
	}

	/**
	 * Set the name of this player. For internal use only.
	 * 
	 * @param name The new player name.
	 */
	public void setName(String name) {
		lock.lock();
		try {
			if (this.name.equals(name))
				return;

			String oldName = this.name;
			Runnable update = () -> this.name = name;
			EventManager.callEvent(new VocalPlayerNameChangePreEvent(this, name), update, new VocalPlayerNameChangePostEvent(this, oldName));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Set the new mute status of this player. For internal use only.
	 * 
	 * @param isMute True if the player is mute, false otherwise.
	 */
	public void setMute(boolean isMute) {
		lock.lock();
		try {
			if (this.isMute == isMute)
				return;

			boolean oldMute = this.isMute;
			Runnable update = () -> this.isMute = isMute;
			EventManager.callEvent(new VocalPlayerMuteChangePreEvent(this, isMute), update, new VocalPlayerMuteChangePostEvent(this, oldMute));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Set the new deafen status of the player. For internal use only.
	 * 
	 * @param isDeafen True if the player is deafen, false otherwise.
	 */
	public void setDeafen(boolean isDeafen) {
		lock.lock();
		try {
			if (this.isDeafen == isDeafen)
				return;

			boolean oldDeafen = this.isDeafen;
			Runnable update = () -> this.isDeafen = isDeafen;
			EventManager.callEvent(new VocalPlayerDeafenChangePreEvent(this, isDeafen), update, new VocalPlayerDeafenChangePostEvent(this, oldDeafen));
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Update the muteBy status of this player for a source player.
	 * 
	 * @param source The source player for which this player is mute or unmute.
	 * @param isMute The new player's mute by status.
	 */
	private void setMuteBy0(IVocalPlayer source, boolean isMute) {
		Boolean status = isMuteBy.get(source);
		boolean oldMute = status == null ? false : status;
		if (oldMute == isMute)
			return;

		lock.lock();
		try {
			isMuteBy.put(source, isMute);
		} finally {
			lock.unlock();
		}

		EventManager.callEvent(new VocalPlayerMuteByChangePostEvent(this, source, oldMute));
	}
}
