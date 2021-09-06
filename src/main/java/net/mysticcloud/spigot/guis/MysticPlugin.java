package net.mysticcloud.spigot.guis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;
import org.json2.JSONException;
import org.json2.JSONObject;

import net.mysticcloud.spigot.guis.commands.InventoryCommand;
import net.mysticcloud.spigot.guis.listeners.InventoryListener;
import net.mysticcloud.spigot.guis.utils.Utils;
import net.mysticcloud.spigot.guis.utils.logs.AlertLog;

public class MysticPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		Utils.limit(!register());

		// TODO add heartbeat that checks if limit has been removed. Erase key if limit
		// is removed maliciously

		Utils.log("&aEnabling");

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		Utils.init(this);

		new InventoryListener(this);
		new InventoryCommand(this, "inventory");

		Utils.log("Enabled.");
	}

	private boolean register() {
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
			return false;
		}

		JSONObject json = checkKey(license);

		if (json != null) {
			List<String> ips = new ArrayList<>();
			;
			if (json.getJSONObject("json").has("ips")) {
				for (Object o : json.getJSONObject("json").getJSONArray("ips").toList()) {
					ips.add((String) o);
				}
			}
			try {
				if (!ips.contains(Inet4Address.getLocalHost().getHostAddress())) {
					ips.add(Inet4Address.getLocalHost().getHostAddress());
				}

				Utils.log("&a&lSuccess&7 > &fFound license key (" + license + ") registered to email: "
						+ json.getString("email"));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Utils.log(new AlertLog(
					"Could not find that license in the database. Please verify you've entered it correctly before contacting support at https://www.quickscythe.com")
							.setLevel(AlertLog.EXTEREME));
			setEnabled(false);
			return false;
		}
		String update = "UPDATE mysticguis SET json=\""
				+ json.getJSONObject("json").toString().replaceAll("\"", "\\\\\"") + "\" WHERE license='" + license
				+ "';";
		Utils.log(update);
		return true;
	}

	private JSONObject checkKey(String key) {

		JSONObject json = null;
		try {
			boolean nsfw = true;
			int loops = 0;
			while (nsfw) {
				URL apiUrl = new URL("https://api.mysticcloud.net/license/guis/" + key);
				URLConnection yc = apiUrl.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null)
					json = new JSONObject(inputLine);
			}

		} catch (Exception e1) {
			json = new JSONObject("{'ERROR':'Could not find license'}");
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return json;
	}

}
