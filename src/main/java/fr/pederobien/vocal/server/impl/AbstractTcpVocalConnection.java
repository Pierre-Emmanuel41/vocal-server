package fr.pederobien.vocal.server.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.common.impl.VocalCallbackMessage;
import fr.pederobien.vocal.common.impl.messages.v10.GetCommunicationProtocolVersionsV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetCommunicationProtocolVersionV10;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class AbstractTcpVocalConnection extends AbstractVocalConnection {

	public AbstractTcpVocalConnection(IVocalServer server, ITcpConnection connection) {
		super(server, connection);
	}

	/**
	 * @return true if a common version of the communication protocol has been found, false otherwise.
	 */
	protected boolean establishCommunicationProtocolVersion() {
		Lock lock = new ReentrantLock(true);
		Condition received = lock.newCondition();

		setVersion(-1.0f);
		getCommunicationProtocolVersion(lock, received);

		lock.lock();
		try {
			if (!received.await(5000, TimeUnit.MILLISECONDS) || getVersion() == -1) {
				getConnection().dispose();
				return false;
			}
		} catch (InterruptedException e) {
			// Do nothing
		} finally {
			lock.unlock();
		}

		return true;
	}

	private void getCommunicationProtocolVersion(Lock lock, Condition received) {
		// Step 1: Asking the latest version of the communication protocol supported by the remote
		send(getServer().getRequestManager().getCommunicationProtocolVersion(), args -> {
			if (args.isTimeout()) {
				EventManager.callEvent(new LogEvent("Client did not answer to GET_COMMUNICATION_PROTOCOL_VERSIONS in time, disposing connection ..."));
				// No need to wait more
				exit(lock, received);
			} else {
				GetCommunicationProtocolVersionsV10 message = (GetCommunicationProtocolVersionsV10) VocalServerMessageFactory.parse(args.getResponse().getBytes());
				setCommunicationProtocolVersion(lock, received, findHighestVersion(message.getVersions()));
			}
		});
	}

	private void setCommunicationProtocolVersion(Lock lock, Condition received, float version) {
		// Step 2: Setting a specific version of the communication protocol to use for the client-server communication.
		send(getServer().getRequestManager().setCommunicationProtocolVersion(version), args -> {
			if (!args.isTimeout()) {
				SetCommunicationProtocolVersionV10 message = (SetCommunicationProtocolVersionV10) VocalServerMessageFactory.parse(args.getResponse().getBytes());
				if (message.getVersion() == version)
					setVersion(version);
			} else
				EventManager.callEvent(new LogEvent("Client did not answer to SET_COMMUNICATION_PROTOCOL_VERSION in time, disposing connection ..."));

			exit(lock, received);
		});
	}

	private void exit(Lock lock, Condition received) {
		lock.lock();
		try {
			received.signal();
		} finally {
			lock.unlock();
		}
	}

	private float findHighestVersion(float[] versions) {
		float version = -1;
		for (int i = versions.length - 1; 0 < i; i--) {
			if (getServer().getRequestManager().isSupported(versions[i])) {
				version = versions[i];
				break;
			}
		}

		return version == -1 ? 1.0f : version;
	}

	/**
	 * Send a request to the remote without expecting an answer.
	 * 
	 * @param message The request to send to the remote.
	 */
	protected void send(IVocalMessage message) {
		send(message, null);
	}

	/**
	 * Send a request to the remote and expect an answer.
	 * 
	 * @param message  The request to send to the remote.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	protected void send(IVocalMessage message, Consumer<ResponseCallbackArgs> callback) {
		send(message, callback, 1000);
	}

	/**
	 * Send a request to the remote and expect an answer.
	 * 
	 * @param message  The request to send to the remote.
	 * @param callback The callback to run when an answer is received from the server.
	 * @param timeout  The request timeout.
	 */
	protected void send(IVocalMessage message, Consumer<ResponseCallbackArgs> callback, int timeout) {
		if (message == null || getConnection() == null || getConnection().isDisposed())
			return;

		((ITcpConnection) getConnection()).send(new VocalCallbackMessage(message, callback, timeout));
	}
}
