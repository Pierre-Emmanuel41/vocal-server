package fr.pederobien.vocal.server.impl.request;

import java.util.ArrayList;
import java.util.List;

import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.messages.v10.SetServerJoinV10;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.impl.PlayerVocalClient;
import fr.pederobien.vocal.server.impl.RequestReceivedHolder;
import fr.pederobien.vocal.server.impl.VocalServerMessageFactory;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class RequestManagerV10 extends RequestManager {

	/**
	 * Creates a request manager associated to version 1.0 in order to modify the given server and answer to remote requests.
	 * 
	 * @param server The server to update.
	 */
	public RequestManagerV10(IVocalServer server) {
		super(server, 1.0f);

		// Server message
		getRequests().put(VocalIdentifier.GET_SERVER_CONFIGURATION, holder -> getServerConfiguration(holder));
		getRequests().put(VocalIdentifier.SET_SERVER_JOIN, holder -> setServerJoin(holder));
	}

	@Override
	public IVocalMessage getCommunicationProtocolVersion() {
		return create(getVersion(), VocalIdentifier.GET_CP_VERSIONS);
	}

	@Override
	public IVocalMessage setCommunicationProtocolVersion(float version) {
		return create(getVersion(), VocalIdentifier.SET_CP_VERSION, version);
	}

	@Override
	public IVocalMessage onServerPlayerAdd(IVocalPlayer player) {
		List<Object> properties = new ArrayList<Object>();

		// Player's name
		properties.add(player.getName());

		// Player's mute status
		properties.add(player.isMute());

		// Player's deafen status
		properties.add(player.isDeafen());

		return create(getVersion(), VocalIdentifier.REGISTER_PLAYER_ON_SERVER, properties.toArray());
	}

	@Override
	public IVocalMessage onServerPlayerRemove(IVocalPlayer player) {
		return create(getVersion(), VocalIdentifier.UNREGISTER_PLAYER_FROM_SERVER, player.getName());
	}

	/**
	 * Creates a message that contains the current server configuration.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server answer.
	 */
	private IVocalMessage getServerConfiguration(RequestReceivedHolder holder) {
		List<Object> informations = new ArrayList<Object>();

		// Number of players
		informations.add(getServer().getPlayers().toList().size());

		for (IVocalPlayer player : getServer().getPlayers()) {
			// Player's name
			informations.add(player.getName());

			// Player's mute status
			informations.add(player.isMute());

			// Player's deafen status
			informations.add(player.isDeafen());

			// Case when the connection corresponds to a player connection -> Needs to check if player is mute by the client player.
			RunResult result = runIfInstanceof(holder, PlayerVocalClient.class, client -> player.isMuteBy(client.getPlayer()));
			informations.add(result.getHasRun() ? result.getResult() : false);
		}
		return answer(getVersion(), holder.getRequest(), informations.toArray());
	}

	/**
	 * Let a player joining a vocal server.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server answer.
	 */
	private IVocalMessage setServerJoin(RequestReceivedHolder holder) {
		SetServerJoinV10 request = (SetServerJoinV10) holder.getRequest();
		RunResult result = runIfInstanceof(holder, PlayerVocalClient.class, client -> client.join(request.getPlayerName(), request.isMute(), request.isDeafen()));
		if (result.getHasRun() && !result.getResult())
			return VocalServerMessageFactory.answer(holder.getRequest(), VocalErrorCode.SERVER_ALREADY_JOINED);

		return VocalServerMessageFactory.answer(holder.getRequest());
	}
}
