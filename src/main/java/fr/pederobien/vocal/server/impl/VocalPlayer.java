package fr.pederobien.vocal.server.impl;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private InetSocketAddress address;
	private Lock lock;

	/**
	 * Creates a vocal player based on the following parameters.
	 * 
	 * @param server   The server on which this player is registered.
	 * @param name     The name of this player.
	 * @param address  The address of this player.
	 * @param isMute   True if the player is mute, false otherwise.
	 * @param isDeafen True if the player is deafen, false otherwise.
	 */
	public VocalPlayer(IVocalServer server, String name, InetSocketAddress address, boolean isMute, boolean isDeafen) {
		this.name = name;
		this.address = address;

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
	public void setName(String name) {
		lock.lock();
		try {
			if (this.name.equals(name))
				return;

			String oldName = this.name;
			EventManager.callEvent(new VocalPlayerNameChangePreEvent(this, name), () -> this.name = name, new VocalPlayerNameChangePostEvent(this, oldName));
		} finally {
			lock.unlock();
		}
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
			EventManager.callEvent(new VocalPlayerOnlineChangePreEvent(this, isOnline), () -> this.isOnline = isOnline, new VocalPlayerOnlineChangePostEvent(this, oldOnline));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isMute() {
		return isMute;
	}

	@Override
	public void setMute(boolean isMute) {
		lock.lock();
		try {
			if (!this.isMute == isMute)
				return;

			boolean oldMute = this.isMute;
			EventManager.callEvent(new VocalPlayerMuteChangePreEvent(this, isMute), () -> this.isMute = isMute, new VocalPlayerMuteChangePostEvent(this, oldMute));
		} finally {
			lock.unlock();
		}
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
	public void setDeafen(boolean isDeafen) {
		lock.lock();
		try {
			if (this.isDeafen == isDeafen)
				return;

			boolean oldDeafen = this.isDeafen;
			EventManager.callEvent(new VocalPlayerDeafenChangePreEvent(this, isDeafen), () -> this.isDeafen = isDeafen, new VocalPlayerDeafenChangePostEvent(this, oldDeafen));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public InetSocketAddress getAddress() {
		return address;
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
