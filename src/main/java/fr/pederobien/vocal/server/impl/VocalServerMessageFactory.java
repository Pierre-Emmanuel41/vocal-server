package fr.pederobien.vocal.server.impl;

import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.VocalMessageFactory;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class VocalServerMessageFactory {
	private static final VocalMessageFactory FACTORY;

	static {
		FACTORY = VocalMessageFactory.getInstance(10000);
	}

	/**
	 * Creates a message based on the given parameters associated to the latest version of the communication protocol.
	 * 
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 * 
	 * @return The created message.
	 */
	public static IVocalMessage create(VocalIdentifier identifier, Object... properties) {
		return FACTORY.create(identifier, VocalErrorCode.NONE, properties);
	}

	/**
	 * Creates a message based on the given parameters associated to a specific version of the communication protocol.
	 * 
	 * @param version    The protocol version to use for the returned message.
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 * 
	 * @return The created message.
	 */
	public static IVocalMessage create(float version, VocalIdentifier identifier, Object... properties) {
		return FACTORY.create(version, identifier, VocalErrorCode.NONE, properties);
	}

	/**
	 * Parse the given buffer in order to create the associated header and the payload.
	 * 
	 * @param buffer The bytes array received from the remote.
	 * 
	 * @return A new message.
	 */
	public static IVocalMessage parse(byte[] buffer) {
		return FACTORY.parse(buffer);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. Neither the identifier nor the header are
	 * modified. The latest version of the communication protocol is used to create the returned message.
	 * 
	 * @param message    The message to answer.
	 * @param properties The response properties.
	 * 
	 * @return A new message.
	 */
	public static IVocalMessage answer(IVocalMessage message, Object... properties) {
		return FACTORY.answer(message, properties);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. Neither the identifier nor the header are
	 * modified. A specific version of the communication protocol is used to create the returned message.
	 * 
	 * @param version    The protocol version to use for the returned message.
	 * @param message    The message to answer.
	 * @param properties The response properties.
	 * 
	 * @return A new message.
	 */
	public static IVocalMessage answer(float version, IVocalMessage message, Object... properties) {
		return FACTORY.answer(version, message, properties);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented. The latest
	 * version of the communication protocol is used to create the answer.
	 * 
	 * @param request   The request to answer.
	 * @param errorCode The error code of the response.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IVocalMessage answer(IVocalMessage message, VocalErrorCode errorCode) {
		return FACTORY.answer(message, message.getHeader().getIdentifier(), errorCode);
	}

	/**
	 * Creates a new message corresponding to the answer of the <code>message</code>. The identifier is not incremented. A specific
	 * version of the communication protocol is used to create the answer.
	 * 
	 * @param version   The protocol version to use for the returned message.
	 * @param request   The request to answer.
	 * @param errorCode The error code of the response.
	 * 
	 * @return The message associated to the answer.
	 */
	public static IVocalMessage answer(float version, IVocalMessage message, VocalErrorCode errorCode) {
		return FACTORY.answer(version, message, message.getHeader().getIdentifier(), errorCode);
	}
}
