package fr.pederobien.vocal.server.interfaces;

import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.impl.RequestReceivedHolder;

public interface IServerRequestManager {

	/**
	 * @return The latest version of the communication protocol.
	 */
	float getVersion();

	/**
	 * Check if the given version of the communication protocol is supported by this server.
	 * 
	 * @param version The version to check.
	 * 
	 * @return True if supported, false otherwise.
	 */
	boolean isSupported(float version);

	/**
	 * Performs server configuration update according to the given request.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server response.
	 */
	IVocalMessage answer(RequestReceivedHolder holder);

	/**
	 * Creates a message in order to get the latest version of the communication protocol supported by the remote.
	 * 
	 * @return The message to send to the remote in order to get the latest version of the communication protocol.
	 */
	IVocalMessage getCommunicationProtocolVersion();

	/**
	 * Creates a message in order to set a specific version of the communication protocol.
	 * 
	 * @param version The version of the communication protocol to use.
	 * 
	 * @return The message to send to the remote in order to get the latest version of the communication protocol.
	 */
	IVocalMessage setCommunicationProtocolVersion(float version);

	/**
	 * Creates a message in order to register a new player.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * @param player  the added player.
	 * 
	 * @return The message to send to the remote in order to add a player to a server.
	 */
	IVocalMessage onServerPlayerAdd(float version, IVocalPlayer player);

	/**
	 * Creates a message in order to unregister a player from the vocal server.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * @param player  the removed player.
	 * 
	 * @return The message to send to the remote in order to remove a player from a server.
	 */
	IVocalMessage onServerPlayerRemove(float version, IVocalPlayer player);

	/**
	 * Creates a message in order to change the name of a player.
	 * 
	 * @param version The protocol version to use to create a vocal message.
	 * @param oldName The old player name.
	 * @param newName The new player name.
	 * 
	 * @return The message to send to the remote in order to update the name of a player.
	 */
	IVocalMessage onPlayerNameChange(float version, String oldName, String newName);

	/**
	 * Creates a message in order to update the player mute status.
	 * 
	 * @param version The protocol version to use to create a mumble message.
	 * @param player  The player whose the mute status has changed.
	 * 
	 * @return The message to send to the remote in order to update the mute status of a player.
	 */
	IVocalMessage onPlayerMuteChange(float version, IVocalPlayer player);
}
