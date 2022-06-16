package fr.pederobien.vocal.server.impl;

import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.impl.RequestCallbackMessage;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class VocalCallbackMessage extends RequestCallbackMessage {
	private IVocalMessage message;

	/**
	 * Create a request message to be send to a remote.
	 * 
	 * @param message  The message that contains the bytes to send to the remote and the identifier.
	 * @param callback The callback to run when a response has been received before the timeout.
	 * @param timeout  The request timeout.
	 */
	public VocalCallbackMessage(IVocalMessage message, Consumer<ResponseCallbackArgs> callback, int timeout) {
		super(message.generate(), message.getHeader().getSequence(), callback, timeout);
		this.message = message;
	}

	/**
	 * Create a request message to be send to a remote.
	 * 
	 * @param message  The message that contains the bytes to send to the remote and the identifier.
	 * @param callback The callback to run when a response has been received before the timeout.
	 */
	public VocalCallbackMessage(IVocalMessage message, Consumer<ResponseCallbackArgs> callback) {
		super(message.generate(), message.getHeader().getSequence(), callback);
		this.message = message;
	}

	@Override
	public String toString() {
		return message.toString();
	}
}
