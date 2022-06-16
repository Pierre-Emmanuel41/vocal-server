package fr.pederobien.vocal.server.impl.request;

import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class RequestManagerV10 extends RequestManager {

	/**
	 * Creates a request manager associated to version 1.0 in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public RequestManagerV10(IVocalServer server) {
		super(server, 1.0f);
	}

	@Override
	public IVocalMessage getCommunicationProtocolVersion() {
		return create(getVersion(), VocalIdentifier.GET_CP_VERSIONS);
	}

	@Override
	public IVocalMessage setCommunicationProtocolVersion(float version) {
		return create(getVersion(), VocalIdentifier.SET_CP_VERSION, version);
	}
}
