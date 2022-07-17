package fr.pederobien.vocal.server.impl;

import fr.pederobien.communication.event.DataReceivedEvent;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class RequestReceivedHolder {
	private DataReceivedEvent event;
	private IVocalMessage request;
	private AbstractVocalConnection connection;

	/**
	 * Creates a holder to gather the request received from the remote and the connection that received the request.
	 * 
	 * @param request    The request sent by the remote.
	 * @param connection The connection that has received the request.
	 */
	public RequestReceivedHolder(AbstractVocalConnection connection, DataReceivedEvent event, IVocalMessage request) {
		this.connection = connection;
		this.event = event;
		this.request = request;
	}

	/**
	 * @return The connection that has received the request.
	 */
	public AbstractVocalConnection getConnection() {
		return connection;
	}

	/**
	 * @return The event thrown by a connection.
	 */
	public DataReceivedEvent getEvent() {
		return event;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IVocalMessage getRequest() {
		return request;
	}
}
