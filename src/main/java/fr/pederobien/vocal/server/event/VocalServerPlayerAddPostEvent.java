package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.vocal.server.interfaces.IServerPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class VocalServerPlayerAddPostEvent extends VocalServerPlayerListEvent {
	private IVocalPlayer player;

	/**
	 * Creates an event thrown when a player has been added to a player list.
	 * 
	 * @param list   The list to which a player has been added.
	 * @param player The added player.
	 */
	public VocalServerPlayerAddPostEvent(IServerPlayerList list, IVocalPlayer player) {
		super(list);
		this.player = player;
	}

	/**
	 * @return The added player.
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
