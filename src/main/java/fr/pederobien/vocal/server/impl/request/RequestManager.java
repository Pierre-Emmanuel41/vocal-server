package fr.pederobien.vocal.server.impl.request;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.impl.AbstractVocalConnection;
import fr.pederobien.vocal.server.impl.RequestReceivedHolder;
import fr.pederobien.vocal.server.impl.VocalServerMessageFactory;
import fr.pederobien.vocal.server.interfaces.IRequestManager;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public abstract class RequestManager implements IRequestManager {
	private float version;
	private IVocalServer server;
	private Map<VocalIdentifier, Function<RequestReceivedHolder, IVocalMessage>> requests;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server  The server to update.
	 * @param version The version of the communication protocol associated to this requests manager.
	 */
	public RequestManager(IVocalServer server, float version) {
		this.server = server;
		this.version = version;
		requests = new HashMap<VocalIdentifier, Function<RequestReceivedHolder, IVocalMessage>>();
	}

	@Override
	public float getVersion() {
		return version;
	}

	@Override
	public IVocalMessage answer(RequestReceivedHolder holder) {
		Function<RequestReceivedHolder, IVocalMessage> answer = requests.get(holder.getRequest().getHeader().getIdentifier());
		if (answer == null)
			return VocalServerMessageFactory.answer(holder.getRequest(), VocalErrorCode.IDENTIFIER_UNKNOWN);

		return answer.apply(holder);
	}

	/**
	 * @return The map that contains the code to run according to the identifier of the request sent by the remote.
	 */
	public Map<VocalIdentifier, Function<RequestReceivedHolder, IVocalMessage>> getRequests() {
		return requests;
	}

	/**
	 * @return The server to update.
	 */
	protected IVocalServer getServer() {
		return server;
	}

	/**
	 * Send a message based on the given parameter to the remote.
	 * 
	 * @param identifier The identifier of the request to create.
	 * @param properties The message properties.
	 */
	protected IVocalMessage create(float version, VocalIdentifier identifier, Object... properties) {
		return VocalServerMessageFactory.create(version, identifier, properties);
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
	protected IVocalMessage answer(float version, IVocalMessage message, Object... properties) {
		return VocalServerMessageFactory.answer(version, message, properties);
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
	protected IVocalMessage answer(float version, IVocalMessage message, VocalIdentifier errorCode) {
		return VocalServerMessageFactory.answer(version, message, errorCode);
	}

	/**
	 * Try to execute code according to the instance type of the connection.
	 * 
	 * @param holder   The holder that contains the connection that received the request and the request itself.
	 * @param clazz    The expected connection type.
	 * @param function The code to execute if the connection is an instance of the given class.
	 * 
	 * @return False if the connection is not an instance of the class, the function's result otherwise.
	 */
	protected <T extends AbstractVocalConnection> RunResult runIfInstanceof(RequestReceivedHolder holder, Class<T> clazz, Function<T, Boolean> function) {
		try {
			return new RunResult(true, function.apply(clazz.cast(holder.getConnection())));
		} catch (ClassCastException e) {
			return new RunResult(false, false);
		}
	}

	protected class RunResult {
		private boolean hasRun, result;

		private RunResult(boolean hasRun, boolean result) {
			this.hasRun = hasRun;
			this.result = result;
		}

		/**
		 * @return True if the function has been ran.
		 */
		public boolean getHasRun() {
			return hasRun;
		}

		/**
		 * @return The result of the function.
		 */
		public boolean getResult() {
			return result;
		}
	}
}
