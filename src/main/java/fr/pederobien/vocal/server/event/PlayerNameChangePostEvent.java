package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class PlayerNameChangePostEvent extends PlayerEvent {
	private String oldName;

	/**
	 * Creates an event thrown when the name of a player has changed.
	 * 
	 * @param player  The player whose the name is about to change.
	 * @param oldName The new player name.
	 */
	public PlayerNameChangePostEvent(IVocalPlayer player, String oldName) {
		super(player);
		this.oldName = oldName;
	}

	/**
	 * @return The old player name.
	 */
	public String getOldName() {
		return oldName;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("oldName=" + getOldName());
		return String.format("%s_%s", getName(), joiner);
	}
}
