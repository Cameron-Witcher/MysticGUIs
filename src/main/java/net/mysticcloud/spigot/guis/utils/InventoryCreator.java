package net.mysticcloud.spigot.guis.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json2.JSONObject;

import me.clip.placeholderapi.PlaceholderAPI;

public class InventoryCreator {

	String name;
	Player holder;
	Inventory inv;
	List<ItemStack> items = new LinkedList<>();
	Map<Character, ItemStack> identifier = new LinkedHashMap<>();
	Map<Character, JSONObject> metadata = new HashMap<>();

	public InventoryCreator(String name, Player holder, int size) {
		this.name = name;
		this.holder = holder;
		inv = Bukkit.createInventory(holder, size, Utils.colorize(name));
	}

	public ItemStack addItem(Material mat, String name, char identifier) {
		return addItem(mat, name, identifier, (String[]) null);
	}

	public ItemStack addItem(Material mat, String name, char identifier, List<String> lore) {
		return addItem(mat, name, identifier, lore == null ? null : lore.toArray(new String[lore.size()]), false, true);
	}

	public ItemStack addItem(Material mat, String name, char identifier, String[] lore) {
		return addItem(mat, name, identifier, lore, false, true);
	}

	public ItemStack addItem(Material mat, String name, char identifier, List<String> lore, boolean showValues) {
		return addItem(mat, name, identifier, lore == null ? null : lore.toArray(new String[lore.size()]), false,
				showValues);
	}

	public ItemStack addItem(Material mat, String name, char identifier, String[] lore, boolean showValues) {
		return addItem(mat, name, identifier, lore, false, showValues);
	}

	public ItemStack addItem(Material mat, String name, char identifier, List<String> lore, boolean unbreakable,
			boolean showValues) {
		return addItem(mat, name, identifier, lore == null ? null : lore.toArray(new String[lore.size()]), unbreakable,
				showValues);
	}

	public ItemStack addItem(Material mat, String name, char identifier, String[] lore, boolean unbreakable,
			boolean showValues) {
		return addItem(mat, name, identifier, lore, unbreakable, showValues, (short) 0);
	}

	public ItemStack addItem(Material mat, String name, char identifier, List<String> lore, boolean unbreakable,
			boolean showValues, short data) {
		return addItem(mat, name, identifier, lore == null ? null : lore.toArray(new String[lore.size()]), unbreakable,
				showValues, data);
	}

	public ItemStack addItem(Material mat, String name, char identifier, String[] lore, boolean unbreakable,
			boolean showValues, short data) {
		return addItem(mat, name, identifier, lore, unbreakable, showValues, data, new JSONObject("{}"));
	}

	public ItemStack addItem(Material mat, String name, char identifier, List<String> lore, boolean unbreakable,
			boolean showValues, short data, JSONObject metadata) {
		return addItem(mat, name, identifier, lore == null ? null : lore.toArray(new String[lore.size()]), unbreakable,
				showValues, data, metadata);
	}

	@SuppressWarnings("deprecation")
	public ItemStack addItem(Material mat, String name, char identifier, String[] lore, boolean unbreakable,
			boolean showValues, short data, JSONObject metadata) {
		ItemStack item = new ItemStack(mat);
		if (mat != null && (mat != Material.AIR)) {
			ItemMeta im = item.getItemMeta();
			if (data != (short) 0)
				item.setDurability(data);

			im.setDisplayName(Utils.colorize(name));
			if (lore != null) {

				im.setLore(Utils.colorizeStringList(lore));
			} else {
				im.setLore(null);
			}

			im.setUnbreakable(unbreakable);
			if (!showValues) {
				for (ItemFlag flag : ItemFlag.values()) {
					im.addItemFlags(flag);
				}
//				im.addItemFlags(ItemFlag.values());
			}
			item.setItemMeta(im);
		}
		if (metadata != null) {
			this.metadata.put(identifier, metadata);
		}
		this.identifier.put(identifier, item);
		return item;
	}

	public void setConfiguration(char[] c) {
		for (Character ch : c)
			items.add(identifier.get(ch));
	}

	public Inventory getInventory() {
		return getInventory(null);
	}

	public Inventory getInventory(Player player) {
		int a = 0;
		for (ItemStack i : items) {
			if (player != null) {
				if (Utils.dependencyEnabled("placeholderapi")) {
					if (i.hasItemMeta()) {
						ItemMeta im = i.getItemMeta();
						if (im.hasDisplayName())
							im.setDisplayName(PlaceholderAPI.setPlaceholders(player, im.getDisplayName()));

						if (im.hasLore()) {
							List<String> lore = new ArrayList<>();
							for (String s : im.getLore()) {
								lore.add(PlaceholderAPI.setPlaceholders(player, s));
							}
							im.setLore(lore);
						}
						i.setItemMeta(im);
					}
				}
			}

			if (a < inv.getSize())
				inv.setItem(a, i);
			a = a + 1;
		}
		return inv;
	}

	public String getName() {
		return name;
	}

	public Player getHolder() {
		return holder;
	}

	public void setConfiguration(ArrayList<Character> ids) {
		for (Character ch : ids) {
			items.add(identifier.get(ch));
		}
	}

	public JSONObject getMetadata(char ch) {
		return metadata.containsKey(ch) ? metadata.get(ch) : new JSONObject("{}");
	}

	public Character getCharacter(ItemStack item) {
		for (Entry<Character, ItemStack> en : this.identifier.entrySet()) {
			if (en.getValue().equals(item)) {
				return en.getKey();
			}
		}
		return (Character) null;
	}

}