package fr.pederobien.vocal.server.event;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import fr.pederobien.vocal.common.impl.VolumeResult;
import fr.pederobien.vocal.server.impl.SpeakBehavior;
import fr.pederobien.vocal.server.interfaces.IVocalPlayer;
import fr.pederobien.vocal.server.interfaces.IVocalServer;

public class VocalPlayerSpeakEvent extends ProjectVocalServerEvent {
	private IVocalServer server;
	private IVocalPlayer transmitter;
	private Map<String, IVocalPlayer> players;
	private Map<IVocalPlayer, VolumeResult> volumes;
	private byte[] data;
	private boolean isMono, isEncoded;

	/**
	 * Creates an event thrown when a player is speaking. The list of players contains a copy of registered players on the server.
	 * There is two possibilities for a player to not speak to another one: </br>
	 * - Removing the player from the {@link #getVolumes()} map.</br>
	 * - Setting the associated {@link VolumeResult} to {@link VolumeResult#NONE}.</br>
	 * 
	 * @param server      The server involved in this event.
	 * @param transmitter The speaking player.
	 * @param data        The bytes array that represents an audio sample.
	 * @param isMono      True if the audio signal is a mono signal, false otherwise.
	 * @param isEncoded   True if the audio sample has been encoded, false otherwise.
	 */
	public VocalPlayerSpeakEvent(IVocalServer server, IVocalPlayer transmitter, byte[] data, boolean isMono, boolean isEncoded) {
		this.server = server;
		this.transmitter = transmitter;
		this.data = data;
		this.isMono = isMono;
		this.isEncoded = isEncoded;

		this.players = new HashMap<String, IVocalPlayer>();
		volumes = new HashMap<IVocalPlayer, VolumeResult>();

		server.getPlayers().stream().filter(receiver -> !receiver.isDeafen() && !transmitter.isMuteBy(receiver))
				.forEach(receiver -> players.put(receiver.getName(), receiver));

		if (server.getSpeakBehavior() != SpeakBehavior.TO_EVERYONE)
			return;

		server.getPlayers().stream().filter(receiver -> !receiver.equals(transmitter) && !receiver.isDeafen() && !transmitter.isMuteBy(receiver))
				.forEach(receiver -> volumes.put(receiver, new VolumeResult(1.0)));
	}

	/**
	 * @return The server involved in this event.
	 */
	public IVocalServer getServer() {
		return server;
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

	/**
	 * @return True if the audio signal is a mono signal, false otherwise.
	 */
	public boolean isMono() {
		return isMono;
	}

	/**
	 * @return True if the audio sample has been encoded, false otherwise.
	 */
	public boolean isEncoded() {
		return isEncoded;
	}

	@Override
	public String toString() {
		StringJoiner joiner = new StringJoiner(", ", "{", "}");
		joiner.add("isMono=" + isMono());
		joiner.add("isEncoded=" + isEncoded());
		joiner.add("Transmitter=" + getTransmitter().getName());
		StringJoiner receivers = new StringJoiner(", ", "{", "}");
		String format = "name=%s, volume={global=%s, left=%s, right=%s}";
		for (Map.Entry<IVocalPlayer, VolumeResult> entry : getVolumes().entrySet())
			receivers.add(String.format(format, entry.getKey().getName(), entry.getValue().getGlobal(), entry.getValue().getLeft(), entry.getValue().getRight()));
		joiner.add("Receivers=" + receivers);
		return String.format("%s_%s", getName(), joiner);
	}
}
