package fr.pederobien.vocal.server.impl;

import java.net.InetSocketAddress;

import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class VocalPlayer implements IVocalPlayer {
	private String name;
	private InetSocketAddress address;

	/**
	 * Creates a vocal player specified by a name and an address.
	 * 
	 * @param name    The name of this player.
	 * @param address The address of this player.
	 */
	protected VocalPlayer(String name, InetSocketAddress address) {
		this.name = name;
		this.address = address;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return String.format("VocalPlayer={name=%s,address=%s}", getName(), getAddress());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof IVocalPlayer))
			return false;

		IVocalPlayer other = (IVocalPlayer) obj;
		return name.equals(other.getName());
	}
}
