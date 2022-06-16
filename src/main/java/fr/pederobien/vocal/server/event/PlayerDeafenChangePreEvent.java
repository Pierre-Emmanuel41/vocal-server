package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class PlayerDeafenChangePreEvent extends PlayerEvent implements ICancellable {
	private boolean isCancelled;
	private boolean newDeafen;

	/**
	 * Creates an event thrown when the deafen status of a player is about to change.
	 * 
	 * @param player    The player whose the deafen status is about to change.
	 * @param newDeafen The new player's deafen status.
	 */
	public PlayerDeafenChangePreEvent(IVocalPlayer player, boolean newDeafen) {
		super(player);
		this.newDeafen = newDeafen;
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
	 * @return The new player's deafen status.
	 */
	public boolean getNewDeafen() {
		return newDeafen;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("player=" + getPlayer().getName());
		joiner.add("currentDeafen=" + getPlayer().isDeafen());
		joiner.add("newDeafen=" + getNewDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
