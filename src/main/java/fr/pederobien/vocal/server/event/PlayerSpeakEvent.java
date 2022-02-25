package fr.pederobien.vocal.server.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;

public class PlayerSpeakEvent extends ProjectVocalServerEvent {
	private IVocalPlayer transmitter;
	private Map<String, IVocalPlayer> players;
	private Map<IVocalPlayer, VolumeResult> volumes;
	private byte[] data;

	/**
	 * Creates an event thrown when a player is speaking. The list of players contains a copy of registered players on the server.
	 * There is two possibilities for a player to not speak to another one: </br>
	 * - Removing the player from the {@link #getVolumes()} map.</br>
	 * - Setting the associated {@link VolumeResult} to {@link VolumeResult#NONE}.</br>
	 * 
	 * @param players     The list of players registered on the server.
	 * @param transmitter The speaking player.
	 * @param data        The bytes array that represents an audio sample.
	 */
	public PlayerSpeakEvent(List<IVocalPlayer> players, IVocalPlayer transmitter, byte[] data) {
		this.transmitter = transmitter;
		this.data = data;

		this.players = new HashMap<String, IVocalPlayer>();
		volumes = new HashMap<IVocalPlayer, VolumeResult>();

		for (IVocalPlayer player : players)
			this.players.put(player.getName(), player);
	}

	/**
	 * @return The speaking player.
	 */
	public IVocalPlayer getTransmitter() {
		return transmitter;
	}

	/**
	 * @return A map that associates the player's name to its instance.
	 */
	public Map<String, IVocalPlayer> getPlayers() {
		return players;
	}

	/**
	 * @return A map that associates for a player a specific sound volume.
	 */
	public Map<IVocalPlayer, VolumeResult> getVolumes() {
		return volumes;
	}

	/**
	 * @return A bytes array that represents an audio sample.
	 */
	public byte[] getData() {
		return data;
	}
}
