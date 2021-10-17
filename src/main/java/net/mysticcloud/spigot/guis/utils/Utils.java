package net.mysticcloud.spigot.guis.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json2.JSONArray;
import org.json2.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.mysticcloud.spigot.guis.utils.gui.GuiInventory;
import net.mysticcloud.spigot.guis.utils.gui.GuiItem;
import net.mysticcloud.spigot.guis.utils.gui.GuiManager;
import net.mysticcloud.spigot.guis.utils.logs.AlertLog;
import net.mysticcloud.spigot.guis.utils.logs.Log;

public class Utils {

	public static final String PREFIX = colorize("&3&lGuis&r&f >&7 ");
	public static final String PLUGIN = "MysticGuis";
	private static final int MAX_LIMITED_GUIS = 4;

	static JavaPlugin plugin = null;

	private static Map<String, GuiInventory> guis = new HashMap<>();

	private static Economy econ;
	private static Permission perms;
	private static Chat chat;

	private static boolean limited = false;

	private static Map<String, Boolean> deps = new HashMap<>();

	private static File guiFolder = null;

	public static void init(JavaPlugin main) {
		plugin = main;
		guiFolder = new File(plugin.getDataFolder().getPath() + "/guis");

		deps.put("vault-econ", setupEconomy());
		deps.put("vault-chat", setupChat());
		deps.put("vault-perm", setupPermissions());
		deps.put("pa", Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);
		deps.put("mvdwpa", Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null);

		registerGuis();
		for (Entry<String, Boolean> e : deps.entrySet())
			log("Dependency check (" + e.getKey() + "): " + e.getValue());

	}

	public static void registerGuis() {
		deps.clear();
		guis.clear();
		GuiInventory gui = new GuiInventory("waiting", "&7Waiting...", 9, "XXXXXXXXX");
		GuiItem item = new GuiItem("X");
		item.setDisplayName("&7Waiting...");
		gui.addItem("X", item);
		guis.put("waiting", gui);
		try {

			if (!guiFolder.exists()) {
				guiFolder.mkdir();
				InputStream in = null;
				FileOutputStream out = null;

				try {

					URL myUrl = new URL("https://downloads.mysticcloud.net/MysticGuis/examples.yml");
					HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
					conn.setDoOutput(true);
					conn.setReadTimeout(30000);
					conn.setConnectTimeout(30000);
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setRequestProperty("Content-Type", "application/json");
					conn.setRequestProperty("Accept-Charset", "UTF-8");
					conn.setRequestMethod("GET");
					in = conn.getInputStream();
					out = new FileOutputStream("plugins/" + PLUGIN + "/guis/examples.yml");
					int c;
					byte[] b = new byte[1024];
					while ((c = in.read(b)) != -1)
						out.write(b, 0, c);

				}

				catch (Exception ex) {
					log(new AlertLog("There was an error updating. Check console for details."));
					ex.printStackTrace();
				}

				finally {
					if (in != null)
						try {
							in.close();
						} catch (IOException e) {
							log(new AlertLog("There was an error updating. Check console for details."));
							e.printStackTrace();
						}
					if (out != null)
						try {
							out.close();
						} catch (IOException e) {
							log(new AlertLog("There was an error updating. Check console for details."));
							e.printStackTrace();
						}
				}
			}

			for (File file : guiFolder.listFiles()) {
				if (file.getName().toLowerCase().endsWith(".yml")) {
					loadGuis(file);
				}
			}
		} catch (Exception e) {
			log(new AlertLog("There was an error registering guis."));
			e.printStackTrace();
		}

	}

	public static boolean limited() {
		return limited;
	}

	public static void limit(boolean limit) {
		limited = limit;
	}

	public static boolean dependencyEnabled(String key) {
		key = key.toLowerCase();
		return deps.containsKey(key) ? deps.get(key) : false;
	}

	private static boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			log("Vault not installed. Disabling Vault economy functions.");
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			log(new AlertLog("No existing economy found. Disabling Vault economy functions."));
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private static boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
		try {
			chat = rsp.getProvider();
		} catch (NullPointerException ex) {
			return false;
		}
		return chat != null;
	}

	private static boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager()
				.getRegistration(Permission.class);
		try {
			perms = rsp.getProvider();
		} catch (NullPointerException ex) {
			return false;
		}
		return perms != null;
	}

	public static Permission getPermissions() {
		return perms;
	}

	public static Chat getChat() {
		return chat;
	}

	public static Economy getEconomy() {
		return econ;
	}

	private static void loadGuis(File file) {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

		log("Loading GUIs... (" + file.getName() + ")");
		int x = 0;
		for (String name : fc.getConfigurationSection("guis").getKeys(false)) {
			if (x == MAX_LIMITED_GUIS && limited)
				break;
			log(" - Loading " + name + "...");
			int size = fc.getInt("guis." + name + ".size", 9);
			String sname = colorize(fc.getString("guis." + name + ".name", "Custom GUI"));
			String array = "";
			for (String s : fc.getStringList("guis." + name + ".config")) {
				array = array + s;
			}
			GuiInventory gui = new GuiInventory(name, sname, size, array);
			for (String iid : fc.getConfigurationSection("guis." + name + ".items").getKeys(false)) {
				log("  - Adding item: " + iid);
				GuiItem item = new GuiItem(iid);
				if (fc.isSet("guis." + name + ".items." + iid + ".name"))
					item.setDisplayName(fc.getString("guis." + name + ".items." + iid + ".name"));
				if (fc.isSet("guis." + name + ".items." + iid + ".type"))
					item.setMaterial(
							Material.valueOf(fc.getString("guis." + name + ".items." + iid + ".type").toUpperCase()));
				if (fc.isSet("guis." + name + ".items." + iid + ".lore"))
					item.setLore(fc.getStringList("guis." + name + ".items." + iid + ".lore"));
				if (fc.isSet("guis." + name + ".items." + iid + ".buy"))
					item.setBuyPrice(fc.getString("guis." + name + ".items." + iid + ".buy"));
				if (fc.isSet("guis." + name + ".items." + iid + ".sell"))
					item.setSellPrice(fc.getString("guis." + name + ".items." + iid + ".sell"));

				if (fc.isSet("guis." + name + ".items." + iid + ".action")) {
					item.setSingleAction(true);
					JSONObject json = new JSONObject("{}");
					if (fc.isSet("guis." + name + ".items." + iid + ".action.action"))
						json.put("action", fc.getString("guis." + name + ".items." + iid + ".action.action"));
					if (fc.isSet("guis." + name + ".items." + iid + ".action.server"))
						json.put("server", fc.getString("guis." + name + ".items." + iid + ".action.server"));
					if (fc.isSet("guis." + name + ".items." + iid + ".action.item"))
						json.put("item", fc.getString("guis." + name + ".items." + iid + ".action.item"));
					if (fc.isSet("guis." + name + ".items." + iid + ".action.amount"))
						json.put("amount", fc.getString("guis." + name + ".items." + iid + ".action.amount"));
					if (fc.isSet("guis." + name + ".items." + iid + ".action.message"))
						json.put("message", fc.getString("guis." + name + ".items." + iid + ".action.message"));
					item.setSingleAction(json);
				}

				if (fc.isSet("guis." + name + ".items." + iid + ".actions")) {
					item.setSingleAction(false);
					JSONArray actions = new JSONArray();

					for (String clickAction : fc.getConfigurationSection("guis." + name + ".items." + iid + ".actions")
							.getKeys(false)) {
						for (String a : fc
								.getConfigurationSection("guis." + name + ".items." + iid + ".actions." + clickAction)
								.getKeys(false)) {
							String key = "guis." + name + ".items." + iid + ".actions." + clickAction;
							JSONObject action = new JSONObject("{}");
							action.put("click", clickAction);
							if (fc.isSet(key + "." + a + ".action"))
								action.put("action", fc.getString(key + "." + a + ".action"));
							if (fc.isSet(key + "." + a + ".server"))
								action.put("server", fc.getString(key + "." + a + ".server"));
							if (fc.isSet(key + "." + a + ".item"))
								action.put("item", fc.getString(key + "." + a + ".item"));
							if (fc.isSet(key + "." + a + ".amount"))
								action.put("amount", fc.getString(key + "." + a + ".amount"));
							if (fc.isSet(key + "." + a + ".message"))
								action.put("message", fc.getString(key + "." + a + ".message"));
							if (fc.isSet(key + "." + a + ".command"))
								action.put("command", fc.getString(key + "." + a + ".command"));
							actions.put(action);
						}
					}
					item.setActions(actions);
				}

				gui.addItem(item.getIdentifier(), item);

			}

			guis.put(name, gui);
			log("Successfully loaded " + name);
			x = x + 1;
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

	public static Map<String, GuiInventory> getGuis() {
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

	public static Plugin getPlugin() {
		return plugin;
	}

	public static ItemStack getSkull(String player) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(player);
		skull.setItemMeta(meta);
		return skull;
	}

	public static boolean update() {

		boolean success = true;
		InputStream in = null;
		FileOutputStream out = null;

		try {

			URL myUrl = new URL(
					"https://jenkins.mysticcloud.net/job/MysticGuis/lastSuccessfulBuild/artifact/target/MysticGuis.jar");
			HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();
			conn.setDoOutput(true);
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept-Charset", "UTF-8");
			conn.setRequestMethod("GET");
			in = conn.getInputStream();
			out = new FileOutputStream("plugins/" + PLUGIN + ".jar");
			int c;
			byte[] b = new byte[1024];
			while ((c = in.read(b)) != -1)
				out.write(b, 0, c);

		}

		catch (Exception ex) {
			log(new AlertLog("There was an error updating. Check console for details."));
			ex.printStackTrace();
			success = false;
		}

		finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					log(new AlertLog("There was an error updating. Check console for details."));
					e.printStackTrace();
				}
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					log(new AlertLog("There was an error updating. Check console for details."));
					e.printStackTrace();
				}
		}
		return success;
	}

	public static void log(String log) {
		log(new Log().setMessage(log).setGlobal(false));
	}

	public static void log(Log log) {
		if (log.isGlobal())
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission(Perm.ADMIN)) {
					player.sendMessage(log + "");
				}
			}
		System.out.println(log);
	}

	public static String setPlaceholders(Player player, String string) {
		string = string.replaceAll("%player%", player.getName());
		string = colorize(string);
		if (dependencyEnabled("mvdwpa")) {
			string = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
		}
		if (dependencyEnabled("pa")) {
			string = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
		}
		return string;
	}

	public static boolean processAction(Player player, GuiItem item, JSONObject action) {

		switch (action.getString("action").toLowerCase()) {
		case "sell":
			ItemStack t = item.getItem(player);
			if (action.has("amount"))
				t.setAmount(Integer.parseInt(action.getString("amount")));
			if (player.getInventory().contains(t)) {
				player.getInventory().remove(t);
				Utils.getEconomy().depositPlayer(player, item.getSellPrice());
				return true;
			} else
				return false;
		case "send_message":
			player.sendMessage(colorize(action.getString("message")));
			return true;
		case "open_gui":
			try {
				GuiManager.openInventory(player, Utils.getGuis().get(action.getString("gui")).getInventory(player),
						action.getString("gui"));
			} catch (NullPointerException ex) {
				player.sendMessage(Utils.PREFIX + "There was an error opening that GUI. Does it exist?");
			}
			return true;
		case "join_server":
			Utils.sendPluginMessage(player, "BungeeCord", "Connect", action.getString("server"));
			return true;
		case "buy":
			int amount = action.has("amount") ? Integer.parseInt(action.getString("amount")) : 1;
			double price = item.getBuyPrice() * amount;
			if (Utils.getEconomy().has(player, price)) {
				Utils.getEconomy().withdrawPlayer(player, price);
				if (action.has("item")) {
					ItemStack i = decodeItem(Utils.setPlaceholders(player, action.getString("item")));
					i.setAmount(amount);
					player.getInventory().addItem(i);
					return true;
				}
				if (action.has("command")) {
					String sender = action.has("sender") ? action.getString("sender") : "player";
					String cmd = Utils.setPlaceholders(player, action.getString("command"));
					Bukkit.dispatchCommand(sender.equalsIgnoreCase("CONSOLE") ? Bukkit.getConsoleSender() : player,
							cmd);
				}
				return true;
			} else
				return false;
		case "command":
			String sender = action.has("sender") ? action.getString("sender") : "player";
			String cmd = Utils.setPlaceholders(player, action.getString("command"));
			Bukkit.dispatchCommand(sender.equalsIgnoreCase("CONSOLE") ? Bukkit.getConsoleSender() : player, cmd);
			return true;
		case "close_gui":
			player.closeInventory();
			return true;
		}
		if (action.has("error_message"))
			player.sendMessage(Utils.setPlaceholders(player, action.getString("error_message")));
		return false;
	}

	public static ItemStack decodeItem(String itemdata) {
		ItemStack i = new ItemStack(Material.AIR);
		if (itemdata.startsWith("CustomItem:")) {
			// TODO
			return i;
		}
		if (itemdata.startsWith("PlayerSkull:")) {
			i = Utils.getSkull(itemdata.split(":")[1]);
			return i;
		}
		i = new ItemStack(Material.valueOf(itemdata.toUpperCase()));
		return i;
	}
}
