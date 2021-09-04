package net.mysticcloud.spigot.guis.utils.logs;

import net.mysticcloud.spigot.guis.utils.Utils;

public class Log {

	private String preprefix = "&7[&9&lM&7&lC&7]&r ";

	public String prefix = "&a[Log]&f >&7";
	String message = "";
	boolean global = false;

	public Log() {

	}

	public Log(String message) {
		this.message = message;
	}

	public Log setMessage(String message) {
		this.message = message;
		return this;
	}

	@Override
	public String toString() {
		return Utils.colorize(preprefix + prefix + " " + message);
	}

	public Log setGlobal(boolean global) {
		this.global = global;
		return this;
	}

	public boolean isGlobal() {
		return global;
	}

}
