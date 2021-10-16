package net.mysticcloud.spigot.guis.utils.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.mysticcloud.spigot.guis.utils.Utils;

public class GuiManager {

	private static Map<UUID, String> invTracker = new HashMap<>();
	private static Inventory waitingInv = null;
	private static boolean init = false;

	public static boolean init() {
		if (!init) {
			GuiInventory gui = new GuiInventory("waiting", "&7Waiting...", 9, "XXXXXXXXX");
			GuiItem item = new GuiItem("X");
			item.setDisplayName("&7Waiting...");
			gui.addItem("X", item);
			waitingInv = gui.getInventory(null);
			init = true;
		}
		return init;

	}

	public static void openInventory(Player player, Inventory inventory, String title) {
		if (inventory == null)
			return;
		if (invTracker.containsKey(player.getUniqueId())) {
			switchInventory(player, inventory, title);
			return;
		}
		player.openInventory(inventory);
		invTracker.put(player.getUniqueId(), title);
	}

	public static String getOpenInventory(Player player) {
		return invTracker.containsKey(player.getUniqueId()) ? invTracker.get(player.getUniqueId()) : "none";
	}

	public static void switchInventory(Player player, Inventory inventory, String title) {
		if (inventory == null)
			return;
		if (waitingInv == null) {
			init();
		}
		player.openInventory(waitingInv);
		invTracker.put(player.getUniqueId(), "waiting");
		Bukkit.getScheduler().runTaskLater(Utils.getPlugin(), new Runnable() {

			@Override
			public void run() {
				player.openInventory(inventory);
				invTracker.put(player.getUniqueId(), title);
			}

		}, 5);
	}

	public static void closeInventory(Player player) {
		if (invTracker.containsKey(player.getUniqueId())) {
			if (invTracker.get(player.getUniqueId()) != "none") {
				invTracker.put(player.getUniqueId(), "none");
				player.closeInventory();
			}

		} else {
			try {
			} catch (Exception ex) {
			}
			invTracker.put(player.getUniqueId(), "none");
		}

	}

}