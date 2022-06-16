package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class VocalPlayerMuteChangePreEvent extends VocalPlayerEvent implements ICancellable {
	private boolean isCancelled;
	private boolean newMute;

	/**
	 * Creates an event thrown when the mute status of a player is about to change.
	 * 
	 * @param player  The player whose the mute status is about to change.
	 * @param newMute The new player's mute status.
	 */
	public VocalPlayerMuteChangePreEvent(IVocalPlayer player, boolean newMute) {
		super(player);
		this.newMute = newMute;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	/**
	 * @return The new player's mute status.
	 */
	public boolean getNewMute() {
		return newMute;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentMute=" + getPlayer().isMute());
		joiner.add("newMute=" + getNewMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
