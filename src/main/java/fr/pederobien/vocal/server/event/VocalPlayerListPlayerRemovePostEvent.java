package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.interfaces.IPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class VocalPlayerListPlayerRemovePostEvent extends VocalPlayerListEvent {
	private IVocalPlayer player;

	/**
	 * Creates an event thrown when a player has been removed to a player list.
	 * 
	 * @param list   The list to which a player has been removed.
	 * @param player The removed player.
	 */
	public VocalPlayerListPlayerRemovePostEvent(IPlayerList list, IVocalPlayer player) {
		super(list);
		this.player = player;
	}

	/**
	 * @return The removed player.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(",", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("player=" + getPlayer().getName());
		return String.format("%s_%s", getName(), joiner);
	}
}
