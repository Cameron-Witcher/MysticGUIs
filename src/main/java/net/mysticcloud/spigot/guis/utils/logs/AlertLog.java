package net.mysticcloud.spigot.guis.utils.logs;

public class AlertLog extends Log {

	public static int LOW = 0;
	public static int MEDIUM = 1;
	public static int HIGH = 2;
	public static int EXTEREME = 3;

	int level = 0;

	public AlertLog() {
		setLevel(0);
		global = true;
	}

	public AlertLog(String message) {
		setLevel(0);
		global = true;
		this.message = message;
	}

	public AlertLog setLevel(int level) {
		this.level = level;
		switch (level) {
		default:
		case 0:
			prefix = "&a&lLOW ALERT&7 >&f";
			break;
		case 1:
			prefix = "&e&lMEDIUM ALERT&7 >&f";
			break;
		case 2:
			prefix = "&c&lHIGH ALERT&7 >&f";
			break;
		case 3:
			prefix = "&4&lEXTREME ALERT&7 >&f";
			break;
		}
		return this;
	}

}
