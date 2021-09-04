package net.mysticcloud.spigot.guis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json2.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;

public class Utils {

	static JavaPlugin plugin = null;

	private static Map<String, InventoryCreator> guis = new HashMap<>();

	public static String prefix = colorize("&3&lGuis&r&f >&7 ");

	public static void init(JavaPlugin main) {
		plugin = main;
		if (plugin.getConfig().isSet("guis")) {
			loadGuis();
		} else {

			plugin.getConfig().set("gui.size", 27);
			plugin.getConfig().set("guis.example.items.X", "GRAY_STAINED_GLASS_PANE{\"name\":\"&7Choose an option.\"}");
			plugin.getConfig().set("guis.example.items.A",
					"DIAMOND_SWORD{\"name\":\"&6&lSurvival\",\"lore\":[\"&f\",\"&fClick to join\"],\"action\":\"join_server\",\"server\":\"survivalhub\"}");
			plugin.getConfig().set("guis.example.items.B",
					"GOLDEN_SHOVEL{\"name\":\"&a&lMiniGames\",\"lore\":[\"&f\",\"&fClick to join\"],\"action\":\"join_server\",\"server\":\"minigames\"}");
			plugin.getConfig().set("guis.example.items.C",
					"GRASS_BLOCK{\"name\":\"&b&lAdventure\",\"lore\":[\"&f\",\"&fClick to join\"],\"action\":\"join_server\",\"server\":\"adventure\"}");
			plugin.getConfig().set("guis.example.items.D",
					"CHEST{\"name\":\"&c&lSkyblock\",\"lore\":[\"&f\",\"&fClick to join\"],\"action\":\"join_server\",\"server\":\"skyblock\"}");

			plugin.getConfig().set("guis.example.items.Y",
					"BARRIER{\"name\":\"&cClose menu\",\"action\":\"close_gui\"}");

			List<String> c = new ArrayList<>();
			c.add("XXXXXXXXX");
			c.add("XAXBXCXDX");
			c.add("XXXXYXXXX");

			plugin.getConfig().set("guis.example.config)", c);

			plugin.saveConfig();

			loadGuis();

		}
	}

	public static JavaPlugin getPlugin() {
		return plugin;
	}

	private static void loadGuis() {
		for (String id : plugin.getConfig().getConfigurationSection("guis").getKeys(false)) {
			int size = plugin.getConfig().getInt("guis." + id + ".size", 9);
			String sname = colorize(plugin.getConfig().getString("guis." + id + ".name", "Custom GUI"));
			String array = "";
			for (String s : plugin.getConfig().getStringList("guis." + id + ".config")) {
				array = array + s;
			}

			InventoryCreator gui = new InventoryCreator(sname, null, size);
			int i = 0;
			for (String iid : plugin.getConfig().getConfigurationSection("guis." + id + "items").getKeys(false)) {
				JSONObject json = new JSONObject("{}");
				String name = plugin.getConfig().getString("guis." + id + ".items." + iid);
				if (name.contains("{")) {
					String data = "";
					for (int a = name.indexOf("{"); a != name.length(); a++) {
						data = data + name.charAt(a);
					}
					String tmp = "";

					for (int a = 0; a != name.indexOf("{"); a++) {
						tmp = tmp + name.charAt(a);
					}
					name = tmp;
					json = new JSONObject(data);
				}
				List<String> lore = null;
				if (json.has("lore")) {
					lore = new ArrayList<>();
					for (Object s : json.getJSONArray("lore").toList()) {
						lore.add(colorize((String) s));
					}
				}
				gui.addItem(Material.valueOf(name.toUpperCase()),
						json.has("name") ? json.getString("name") : name.toUpperCase(), iid.charAt(0), lore, true,
						false, (short) 0, json);
				i = i + 1;
			}
			gui.setConfiguration(array.toCharArray());

			guis.put(id, gui);

		}
	}

	public static String colorize(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static List<String> colorizeStringList(List<String> stringList) {
		return colorizeStringList((String[]) stringList.toArray());
	}

	public static List<String> colorizeStringList(String[] stringList) {
		List<String> ret = new ArrayList<>();
		for (String s : stringList) {
			ret.add(colorize(s));
		}
		return ret;
	}

	public static Map<String, InventoryCreator> getGuis() {
		return guis;
	}

	public static void sendPluginMessage(Player player, String channel, String... arguments) {
		if (arguments == null | arguments.length == 0)
			return;
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		for (String s : arguments) {
			out.writeUTF(s);
		}
		player.sendPluginMessage(getPlugin(), channel, out.toByteArray());
	}
}
