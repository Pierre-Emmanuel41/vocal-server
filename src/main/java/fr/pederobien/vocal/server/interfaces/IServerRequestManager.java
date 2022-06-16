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
}
