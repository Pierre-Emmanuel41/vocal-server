package fr.pederobien.vocal.server.event;

import java.util.StringJoiner;

import fr.pederobien.utils.ICancellable;
import fr.pederobien.vocal.server.interfaces.IServerPlayerList;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class VocalServerPlayerAddPreEvent extends VocalServerPlayerListEvent implements ICancellable {
	private boolean isCancelled;
	private IVocalPlayer player;

	/**
	 * Creates an event thrown when a player is about to be added on the server.
	 * 
	 * @param list   The list to which a player is about to be added.
	 * @param player The added player.
	 */
	public VocalServerPlayerAddPreEvent(IServerPlayerList list, IVocalPlayer player) {
		super(list);
		this.player = player;
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
	 * @return The player that is about to be added.
	 */
	public IVocalPlayer getPlayer() {
		return player;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("list=" + getList().getName());
		joiner.add("name=" + getPlayer());
		joiner.add("tcpAddress=" + getPlayer().getTcpAddress());
		joiner.add("isMute=" + getPlayer().isMute());
		joiner.add("isDeafen=" + getPlayer().isDeafen());
		return String.format("%s_%s", getName(), joiner);
	}
}
