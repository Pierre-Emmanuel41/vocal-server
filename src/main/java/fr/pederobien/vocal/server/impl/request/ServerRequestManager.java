package fr.pederobien.vocal.server.impl.request;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;

import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.impl.RequestReceivedHolder;
import fr.pederobien.vocal.server.impl.VocalServerMessageFactory;
import fr.pederobien.vocal.server.interfaces.IRequestManager;
import fr.pederobien.vocal.server.interfaces.IServerRequestManager;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class ServerRequestManager implements IServerRequestManager {
	private NavigableMap<Float, IRequestManager> managers;

	/**
	 * Creates a request management in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public ServerRequestManager(IVocalServer server) {
		managers = new TreeMap<Float, IRequestManager>();
		register(new RequestManagerV10(server));
	}

	@Override
	public float getVersion() {
		return managers.lastKey();
	}

	@Override
	public boolean isSupported(float version) {
		return managers.containsKey(version);
	}

	@Override
	public IVocalMessage answer(RequestReceivedHolder holder) {
		IRequestManager manager = managers.get(holder.getRequest().getHeader().getVersion());

		if (manager == null)
			return VocalServerMessageFactory.answer(holder.getRequest(), VocalErrorCode.INCOMPATIBLE_VERSION);

		return manager.answer(holder);
	}

	@Override
	public IVocalMessage getCommunicationProtocolVersion() {
		return findManagerAndApply(1.0f, manager -> manager.getCommunicationProtocolVersion());
	}

	@Override
	public IVocalMessage setCommunicationProtocolVersion(float version) {
		return findManagerAndApply(1.0f, manager -> manager.setCommunicationProtocolVersion(version));
	}

	private void register(IRequestManager manager) {
		managers.put(manager.getVersion(), manager);
	}

	/**
	 * Apply the function of the manager associated to the given version if registered.
	 * 
	 * @param version  The version of the manager.
	 * @param function The function to apply.
	 * 
	 * @return The created message.
	 */
	private IVocalMessage findManagerAndApply(float version, Function<IRequestManager, IVocalMessage> function) {
		IRequestManager manager = managers.get(version);
		if (manager == null)
			return null;

		return function.apply(manager);
	}
}