package fr.pederobien.vocal.server.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import fr.pederobien.communication.ResponseCallbackArgs;
import fr.pederobien.communication.event.UnexpectedDataReceivedEvent;
import fr.pederobien.communication.interfaces.ITcpConnection;
import fr.pederobien.utils.event.EventManager;
import fr.pederobien.utils.event.LogEvent;
import fr.pederobien.vocal.common.impl.VocalCallbackMessage;
import fr.pederobien.vocal.common.impl.messages.v10.GetCommunicationProtocolVersionsV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetCommunicationProtocolVersionV10;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class AbstractVocalConnection {
	private IVocalServer server;
	private ITcpConnection connection;
	private float version;

	/**
	 * Creates a vocal connection in order to send or receive requests from the remote.
	 * 
	 * @param server     The server associated to this connection.
	 * @param connection The TCP connection with the remote.
	 */
	public AbstractVocalConnection(IVocalServer server, ITcpConnection connection) {
		this.server = server;
		this.connection = connection;
	}

	/**
	 * @return The server associated to this vocal connection.
	 */
	public IVocalServer getServer() {
		return server;
	}

	/**
	 * @return The version of the communication protocol to use.
	 */
	public float getVersion() {
		return version;
	}

	/**
	 * @return The TCP connection with the remote.
	 */
	protected ITcpConnection getTcpConnection() {
		return connection;
	}

	/**
	 * Set the TCP connection with the remote.
	 * 
	 * @param connection The TCP connection in order to send or receive requests from the remote.
	 */
	protected void setTcpConnection(ITcpConnection connection) {
		this.connection = connection;
	}

	protected IVocalMessage checkReceivedRequest(UnexpectedDataReceivedEvent event) {
		if (!event.getConnection().equals(getTcpConnection()))
			return null;

		try {
			return VocalServerMessageFactory.parse(event.getAnswer());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return true if a common version of the communication protocol has been found, false otherwise.
	 */
	protected boolean establishCommunicationProtocolVersion() {
		Lock lock = new ReentrantLock(true);
		Condition received = lock.newCondition();

		version = -1;
		getCommunicationProtocolVersion(lock, received);

		lock.lock();
		try {
			if (!received.await(5000, TimeUnit.MILLISECONDS) || version == -1) {
				connection.dispose();
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
		send(server.getRequestManager().getCommunicationProtocolVersion(), args -> {
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
		send(server.getRequestManager().setCommunicationProtocolVersion(version), args -> {
			if (!args.isTimeout()) {
				SetCommunicationProtocolVersionV10 message = (SetCommunicationProtocolVersionV10) VocalServerMessageFactory.parse(args.getResponse().getBytes());
				if (message.getVersion() == version)
					this.version = version;
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
			if (server.getRequestManager().isSupported(versions[i])) {
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
	public void send(IVocalMessage message) {
		send(message, null);
	}

	/**
	 * Send a request to the remote and expect an answer.
	 * 
	 * @param message  The request to send to the remote.
	 * @param callback The callback to run when an answer is received from the server.
	 */
	public void send(IVocalMessage message, Consumer<ResponseCallbackArgs> callback) {
		send(message, callback, 1000);
	}

	/**
	 * Send a request to the remote and expect an answer.
	 * 
	 * @param message  The request to send to the remote.
	 * @param callback The callback to run when an answer is received from the server.
	 * @param timeout  The request timeout.
	 */
	public void send(IVocalMessage message, Consumer<ResponseCallbackArgs> callback, int timeout) {
		if (connection == null || connection.isDisposed())
			return;

		connection.send(new VocalCallbackMessage(message, callback, timeout));
	}
}
