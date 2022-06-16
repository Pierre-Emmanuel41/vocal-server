package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class PlayerOnlineChangePostEvent extends PlayerEvent {
	private boolean oldOnline;

	/**
	 * Creates an event thrown when the online status of a player has changed.
	 * 
	 * @param player    The player whose the online status has changed.
	 * @param oldOnline The old player's online status.
	 */
	public PlayerOnlineChangePostEvent(IVocalPlayer player, boolean oldOnline) {
		super(player);
		this.oldOnline = oldOnline;
	}

	/**
	 * @return The old player's online status.
	 */
	public boolean getOldOnline() {
		return oldOnline;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentOnline=" + getPlayer().isOnline());
		joiner.add("oldOnline=" + getOldOnline());
		return String.format("%s_%s", getName(), joiner);
	}
}
