package fr.pederobien.vocal.server.impl.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.pederobien.utils.event.EventManager;
import fr.pederobien.vocal.common.impl.VocalErrorCode;
import fr.pederobien.vocal.common.impl.VocalIdentifier;
import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.common.impl.messages.v10.PlayerSpeakInfoMessageV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerDeafenStatusV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerMuteByStatusV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerMuteStatusV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetPlayerNameV10;
import fr.pederobien.vocal.common.impl.messages.v10.SetServerJoinV10;
import fr.pederobien.vocal.common.interfaces.IVocalMessage;
import fr.pederobien.vocal.server.event.VocalPlayerSpeakEvent;
import fr.pederobien.vocal.server.impl.PlayerVocalClient;
import fr.pederobien.vocal.server.impl.RequestReceivedHolder;
import fr.pederobien.vocal.server.impl.VocalPlayer;
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

		// Player messages
		getRequests().put(VocalIdentifier.SET_PLAYER_NAME, holder -> setPlayerName(holder));
		getRequests().put(VocalIdentifier.SET_PLAYER_MUTE, holder -> setPlayerMute(holder));
		getRequests().put(VocalIdentifier.SET_PLAYER_MUTE_BY, holder -> setPlayerMuteBy(holder));
		getRequests().put(VocalIdentifier.SET_PLAYER_DEAFEN, holder -> setPlayerDeafen(holder));

		// Audio message
		getRequests().put(VocalIdentifier.PLAYER_SPEAK_INFO, holder -> setPlayerSpeak(holder));
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

	@Override
	public IVocalMessage onPlayerNameChange(String oldName, String newName) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_NAME, oldName, newName);
	}

	@Override
	public IVocalMessage onPlayerMuteChange(IVocalPlayer player) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_MUTE, player.getName(), player.isMute());
	}

	@Override
	public IVocalMessage onPlayerMuteByChange(IVocalPlayer target, IVocalPlayer source) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_MUTE_BY, target.getName(), source.getName(), target.isMuteBy(source));
	}

	@Override
	public IVocalMessage onPlayerDeafenChange(IVocalPlayer player) {
		return create(getVersion(), VocalIdentifier.SET_PLAYER_DEAFEN, player.getName(), player.isDeafen());
	}

	@Override
	public IVocalMessage onPlayerSpeak(IVocalPlayer transmitter, byte[] data, VolumeResult volume) {
		return create(getVersion(), VocalIdentifier.PLAYER_SPEAK_SET, transmitter.getName(), data, volume);
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

	/**
	 * Set the name of a player.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server answer.
	 */
	private IVocalMessage setPlayerName(RequestReceivedHolder holder) {
		SetPlayerNameV10 request = (SetPlayerNameV10) holder.getRequest();
		RunResult result = runIfInstanceof(holder, PlayerVocalClient.class, client -> client.getPlayer().getName().equals(request.getOldName()));
		Optional<IVocalPlayer> optPlayer;

		// Case when the connection corresponds to a player connection -> Needs to check player's name match.
		if (result.getHasRun()) {
			if (!result.getResult())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_DOES_NOT_MATCH);
			else
				optPlayer = Optional.of(((PlayerVocalClient) holder.getConnection()).getPlayer());
		}
		// Case when the connection corresponds to a stand-alone connection -> Needs to check if the player exist.
		else {
			optPlayer = getServer().getPlayers().get(request.getOldName());
			if (!optPlayer.isPresent())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_NOT_FOUND);
		}

		Optional<IVocalPlayer> optCheck = getServer().getPlayers().get(request.getNewName());
		if (optCheck.isPresent())
			return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_ALREADY_EXISTS);

		optPlayer.get().setName(request.getNewName());

		if (!optPlayer.get().getName().equals(request.getNewName()))
			return answer(getVersion(), request, VocalErrorCode.REQUEST_CANCELLED);

		return answer(getVersion(), request, request.getProperties());
	}

	/**
	 * Set the mute status of a player.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server answer.
	 */
	private IVocalMessage setPlayerMute(RequestReceivedHolder holder) {
		SetPlayerMuteStatusV10 request = (SetPlayerMuteStatusV10) holder.getRequest();
		RunResult result = runIfInstanceof(holder, PlayerVocalClient.class, client -> client.getPlayer().getName().equals(request.getPlayerName()));
		Optional<IVocalPlayer> optPlayer;

		// Case when the connection corresponds to a player connection -> Needs to check player's name match.
		if (result.getHasRun()) {
			if (!result.getResult())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_DOES_NOT_MATCH);
			else
				optPlayer = Optional.of(((PlayerVocalClient) holder.getConnection()).getPlayer());
		}
		// Case when the connection corresponds to a stand-alone connection -> Needs to check if the player exist.
		else {
			optPlayer = getServer().getPlayers().get(request.getPlayerName());
			if (!optPlayer.isPresent())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_NOT_FOUND);
		}

		optPlayer.get().setMute(request.isMute());

		if (optPlayer.get().isMute() != request.isMute())
			return answer(getVersion(), request, VocalErrorCode.REQUEST_CANCELLED);

		return answer(getVersion(), request, request.getProperties());
	}

	/**
	 * Update the mute status of a player for another player.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server answer.
	 */
	private IVocalMessage setPlayerMuteBy(RequestReceivedHolder holder) {
		SetPlayerMuteByStatusV10 request = (SetPlayerMuteByStatusV10) holder.getRequest();

		RunResult result = runIfInstanceof(holder, PlayerVocalClient.class, client -> client.getPlayer().getName().equals(request.getSource()));
		Optional<IVocalPlayer> optSource;

		// Case when the connection corresponds to a player connection -> Needs to check player's name match.
		if (result.getHasRun()) {
			if (!result.getResult())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_DOES_NOT_MATCH);
			else
				optSource = Optional.of(((PlayerVocalClient) holder.getConnection()).getPlayer());
		}
		// Case when the connection corresponds to a stand-alone connection -> Needs to check if the player exist.
		else {
			optSource = getServer().getPlayers().get(request.getSource());
			if (!optSource.isPresent())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_NOT_FOUND);
		}

		Optional<IVocalPlayer> optTarget = getServer().getPlayers().get(request.getTarget());
		if (!optTarget.isPresent())
			return answer(getVersion(), request, VocalErrorCode.PLAYER_NOT_FOUND);

		optTarget.get().setMuteBy(optSource.get(), request.isMute());
		if (optTarget.get().isMuteBy(optSource.get()) != request.isMute())
			return answer(getVersion(), request, VocalErrorCode.REQUEST_CANCELLED);

		return answer(getVersion(), request, request.getProperties());
	}

	/**
	 * Set the deafen status of a player.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server answer.
	 */
	private IVocalMessage setPlayerDeafen(RequestReceivedHolder holder) {
		SetPlayerDeafenStatusV10 request = (SetPlayerDeafenStatusV10) holder.getRequest();
		RunResult result = runIfInstanceof(holder, PlayerVocalClient.class, client -> client.getPlayer().getName().equals(request.getPlayerName()));
		Optional<IVocalPlayer> optPlayer;

		// Case when the connection corresponds to a player connection -> Needs to check player's name match.
		if (result.getHasRun()) {
			if (!result.getResult())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_DOES_NOT_MATCH);
			else
				optPlayer = Optional.of(((PlayerVocalClient) holder.getConnection()).getPlayer());
		}
		// Case when the connection corresponds to a stand-alone connection -> Needs to check if the player exist.
		else {
			optPlayer = getServer().getPlayers().get(request.getPlayerName());
			if (!optPlayer.isPresent())
				return answer(getVersion(), holder.getRequest(), VocalErrorCode.PLAYER_NOT_FOUND);
		}

		optPlayer.get().setDeafen(request.isDeafen());

		if (optPlayer.get().isDeafen() != request.isDeafen())
			return answer(getVersion(), request, VocalErrorCode.REQUEST_CANCELLED);

		return answer(getVersion(), request, request.getProperties());
	}

	/**
	 * Throw a PlayerSpeakEvent.
	 * 
	 * @param holder The holder that contains the connection that received the request and the request itself.
	 * 
	 * @return The server answer.
	 */
	private IVocalMessage setPlayerSpeak(RequestReceivedHolder holder) {
		PlayerSpeakInfoMessageV10 request = (PlayerSpeakInfoMessageV10) holder.getRequest();

		Optional<IVocalPlayer> optPlayer = getServer().getPlayers().get(request.getPlayerName());
		if (!optPlayer.isPresent())
			return null;

		VocalPlayer player = (VocalPlayer) optPlayer.get();
		player.setUdpAddress(holder.getEvent().getAddress());
		EventManager.callEvent(new VocalPlayerSpeakEvent(getServer(), player, request.getData()));
		return null;
	}
}
