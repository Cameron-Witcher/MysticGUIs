package net.mysticcloud.spigot.guis.utils.sql;

public enum SQLDriver {

	SQLITE("sqlite"), MYSQL_OLD("mysql_old"), MYSQL("mysql");

	String name;

	SQLDriver(String name) {
		this.name = name;
	}

	public String argname() {
		return name;
	}

}
