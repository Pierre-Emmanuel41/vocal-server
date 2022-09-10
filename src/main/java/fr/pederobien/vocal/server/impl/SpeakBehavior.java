package fr.pederobien.vocal.server.impl;

import java.util.Arrays;
import java.util.List;

public enum SpeakBehavior {

	/**
	 * Enumeration to tell to the vocal server that when a player is speaking, he can speaks by default to everyone currently
	 * connected in the server.
	 */
	TO_EVERYONE("to_everyone"),

	/**
	 * Enumeration to tell to the vocal server that when a player is speaking, he cannot speaks by default to everyone currently
	 * connected in the server.
	 */
	TO_NO_ONE("to_no_one");

	public static final List<String> NAMES = Arrays.asList(TO_EVERYONE.getName(), TO_NO_ONE.getName());

	private String name;

	private SpeakBehavior(String name) {
		this.name = name;
	}

	/**
	 * @return The user friendly name of this enumeration field.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the speak behavior enumeration associated to the given name. If the name is not "to_no_one" then the returned behavior is
	 * {@link #TO_EVERYONE}.
	 * 
	 * @param name The name of the behavior to retrieve.
	 * 
	 * @return The behavior associated to the given name.
	 */
	public static SpeakBehavior fromName(String name) {
		return name == SpeakBehavior.TO_NO_ONE.getName() ? SpeakBehavior.TO_NO_ONE : SpeakBehavior.TO_EVERYONE;
	}
}
