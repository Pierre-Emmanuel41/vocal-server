package fr.pederobien.vocal.server.impl;

import fr.pederobien.vocal.common.interfaces.IVocalMessage;

public class RequestReceivedHolder {
	private IVocalMessage request;
	private AbstractVocalConnection connection;

	/**
	 * Creates a holder to gather the request received from the remote and the connection that received the request.
	 * 
	 * @param request    The request sent by the remote.
	 * @param connection The connection that has received the request.
	 */
	public RequestReceivedHolder(IVocalMessage request, AbstractVocalConnection connection) {
		this.request = request;
		this.connection = connection;
	}

	/**
	 * @return The request sent by the remote.
	 */
	public IVocalMessage getRequest() {
		return request;
	}

	/**
	 * @return The connection that has received the request.
	 */
	public AbstractVocalConnection getConnection() {
		return connection;
	}
}
