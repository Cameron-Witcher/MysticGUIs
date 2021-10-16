package net.mysticcloud.spigot.guis.utils.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.mysticcloud.spigot.guis.utils.Utils;

public class GuiInventory {

	String name;
	String display_name = "";
	String config = "xxxxxx";
	int size = 9;
	Map<String, GuiItem> items = new HashMap<>();

	@Deprecated
	public GuiInventory(String name) {
		this.name = name;
	}

	@Deprecated
	public GuiInventory(String name, String display_name) {
		this(name);
		this.display_name = display_name;
	}

	@Deprecated
	public GuiInventory(String name, String display_name, int size) {
		this(name, display_name);
		this.size = size;
	}

	public GuiInventory(String name, String display_name, int size, String config) {
		this(name, display_name, size);
		this.config = config;
	}

	public void addItem(String identifier, GuiItem item) {
		Utils.log("Gui " + name + ": Adding item " + identifier);
		items.put(identifier, item);
	}

	public Inventory getInventory(Player player) {
		Inventory inv = Bukkit.createInventory(player, size, Utils.colorize(name));
		for (int i = 0; i != config.length(); i++) {
			String key = config.substring(i, i + 1);
			Utils.log("Getting item '" + key + "'");
			inv.setItem(i, getGuiItem(key).getItem(player));
		}
		return inv;
	}

	public GuiItem getGuiItem(String key) {
		return items.get(key);
	}

	public String getKey(ItemStack item, Player player) {
		for (Entry<String, GuiItem> e : items.entrySet()) {
			if (e.getValue().getItem(player).equals(item))
				return e.getKey();
		}
		return "";
	}

	public boolean hasItem(ItemStack item, Player player) {
		for (Entry<String, GuiItem> e : items.entrySet()) {
			if (e.getValue().getItem(player).equals(item))
				return true;
		}
		return false;
	}

	public GuiItem getItem(ItemStack item, Player player) {
		for (Entry<String, GuiItem> e : items.entrySet()) {
			if (e.getValue().getItem(player).equals(item))
				return e.getValue();
		}
		return null;
	}

}
