package fr.pederobien.vocal.server.interfaces;

import java.time.LocalTime;

import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.impl.RequestReceivedHolder;

public interface IRequestManager {

	/**
	 * @return The version of the communication protocol associated to this requests manager.
	 */
	float getVersion();

	/**
	 * Performs server configuration update according to the given request.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server response.
	 */
	IVocalMessage answer(RequestReceivedHolder holder);

	/**
	 * @return The message to send to the remote in order to get the latest version of the communication protocol.
	 */
	IVocalMessage getCommunicationProtocolVersion();

	/**
	 * Creates a message in order to set a specific version of the communication protocol.
	 * 
	 * @param version The version to use between the client and the remote.
	 * 
	 * @return The message to send to the remote in order to get the latest version of the communication protocol.
	 */
	IVocalMessage setCommunicationProtocolVersion(float version);

	/**
	 * Creates a message in order to time-synchronize a client with the server.
	 * 
	 * @param time The actual time on the server.
	 * 
	 * @return The message to send to the remote in order to time-synchronize with the server.
	 */
	IVocalMessage onTimeSynchronization(LocalTime time);

	/**
	 * Creates a message in order to register a new player.
	 * 
	 * @param player the added player.
	 * 
	 * @return The message to send to the remote in order to add a player to a server.
	 */
	IVocalMessage onServerPlayerAdd(IVocalPlayer player);

	/**
	 * Creates a message in order to unregister a player from a vocal server.
	 * 
	 * @param player the removed player.
	 * 
	 * @return The message to send to the remote in order to remove a player from a server.
	 */
	IVocalMessage onServerPlayerRemove(IVocalPlayer player);

	/**
	 * Creates a message in order to change the name of a player.
	 * 
	 * @param oldName The old player name.
	 * @param newName The new player name.
	 * 
	 * @return The message to send to the remote in order to update the name of a player.
	 */
	IVocalMessage onPlayerNameChange(String oldName, String newName);

	/**
	 * Creates a message in order to update the player mute status.
	 * 
	 * @param player The player whose the mute status has changed.
	 * 
	 * @return The message to send to the remote in order to update the mute status of a player.
	 */
	IVocalMessage onPlayerMuteChange(IVocalPlayer player);

	/**
	 * Creates a message in order to mute or unmute a target player for a source player.
	 * 
	 * @param target The target player to mute or unmute for a source player.
	 * @param source The source player for which a target player is mute or unmute.
	 * 
	 * @return The message to send to the remote in order to update the muteby status of a player.
	 */
	IVocalMessage onPlayerMuteByChange(IVocalPlayer target, IVocalPlayer source);

	/**
	 * Creates a message in order to update the player deafen status.
	 * 
	 * @param player The player whose the deafen status has changed.
	 * 
	 * @return The message to send to the remote in order to update the deafen status of a player.
	 */
	IVocalMessage onPlayerDeafenChange(IVocalPlayer player);

	/**
	 * Creates a message in order to send an audio sample.
	 * 
	 * @param time        the time at which this message is sent to the client.
	 * @param transmitter The speaking player.
	 * @param data        The bytes array that represents an audio sample.
	 * @param isMono      True if the audio signal is a mono signal, false otherwise.
	 * @param isEncoded   True if the audio sample has been encoded, false otherwise.
	 * @param volume      The different sound volume of the sample.
	 * 
	 * @return The message to send to the remote in order to play an audio sample.
	 */
	IVocalMessage onPlayerSpeak(LocalTime time, IVocalPlayer transmitter, byte[] data, boolean isMono, boolean isEncoded, VolumeResult volume);
}
