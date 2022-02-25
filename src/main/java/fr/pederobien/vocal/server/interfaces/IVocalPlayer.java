package fr.pederobien.vocal.server.interfaces;

import java.net.InetSocketAddress;

public interface IVocalPlayer {

	/**
	 * @return The name of this player
	 */
	String getName();

	/**
	 * @return The address used for the UDP communication.
	 */
	InetSocketAddress getAddress();
}
