package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class PlayerNameChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private String newName;

	/**
	 * Creates an event thrown when the name of a player is about to change.
	 * 
	 * @param player  The player whose the name is about to change.
	 * @param newName The new player name.
	 */
	public PlayerNameChangePreEvent(IVocalPlayer player, String newName) {
		super(player);
		this.newName = newName;
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
	 * @return The new player name.
	 */
	public String getNewName() {
		return newName;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("newName=" + getNewName());
		return String.format("%s_%s", getName(), joiner);
	}
}
