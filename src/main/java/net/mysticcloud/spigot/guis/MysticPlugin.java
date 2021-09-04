package net.mysticcloud.spigot.guis;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;
import org.json2.JSONObject;

import net.mysticcloud.spigot.guis.commands.InventoryCommand;
import net.mysticcloud.spigot.guis.listeners.InventoryListener;
import net.mysticcloud.spigot.guis.utils.Utils;
import net.mysticcloud.spigot.guis.utils.logs.AlertLog;
import net.mysticcloud.spigot.guis.utils.sql.IDatabase;
import net.mysticcloud.spigot.guis.utils.sql.SQLDriver;

public class MysticPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		String license = "<Insert Key Here>";
		if (getConfig().isSet("license"))
			license = getConfig().getString("license");
		else {
			getConfig().set("license", license);
			saveConfig();
			Utils.log(new AlertLog(
					"You license hasn't been set. You must enter your license into the config.yml file before the plugin can enable.")
							.setLevel(AlertLog.MEDIUM));
			setEnabled(false);
			return;
		}

		JSONObject json = checkKey(license);

		if (json != null) {
			Utils.log("&a&lSuccess&7 > &ffound license key (" + license + " registered to email: "
					+ json.getString("email"));
			Utils.log("&aEnabling");
		} else {
			Utils.log(new AlertLog(
					"Could not find that license in the database. Please verify you've entered it correctly before contacting support at https://www.quickscythe.com")
							.setLevel(AlertLog.EXTEREME));
			setEnabled(false);
			return;
		}

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		Utils.init(this);

		new InventoryListener(this);
		new InventoryCommand(this, "inventory");
	}

	private JSONObject checkKey(String key) {
		IDatabase db = new IDatabase(SQLDriver.MYSQL, "sql.mysticcloud.net", "s16_plugins", 3306, "u16_npw9pfa6hB",
				"Oys6JTVv7cFN4Z349!5ahDj2");
		try {
			if (db.init()) {
				ResultSet rs = db.query("SELECT * FROM mysticguis WHERE license='" + key + "';");
				if (rs != null) {
					while (rs.next()) {
						JSONObject json = new JSONObject("{}");
						json.put("license", key);
						json.put("email", rs.getString("email"));
						json.put("json", new JSONObject(rs.getString("json")));

						return json;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
