package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class PlayerMuteChangePostEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private boolean oldMute;

	/**
	 * Creates an event thrown when the mute status of a player has changed.
	 * 
	 * @param player  The player whose the mute status has changed.
	 * @param oldMute The old player's mute status.
	 */
	public PlayerMuteChangePostEvent(IVocalPlayer player, boolean oldMute) {
		super(player);
		this.oldMute = oldMute;
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
	 * @return The old player's mute status.
	 */
	public boolean getOldMute() {
		return oldMute;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentMute=" + getPlayer().isMute());
		joiner.add("oldMute=" + getOldMute());
		return String.format("%s_%s", getName(), joiner);
	}
}
