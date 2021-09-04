package net.mysticcloud.spigot.guis.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GuiManager {

	private static Map<UUID, String> invTracker = new HashMap<>();
	private static Inventory waitingInv = null;
	private static boolean init = false;

	public static boolean init() {
		if (!init) {
			InventoryCreator inv = new InventoryCreator(Utils.colorize("&7Waiting..."), null, 9);

			inv.addItem(Material.GRAY_STAINED_GLASS_PANE, "Waiting...", 'X');

			inv.setConfiguration(new char[] { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' });
			waitingInv = inv.getInventory();
			init = true;
		}
		return init;

	}

	public static void openInventory(Player player, Inventory inventory, String title) {
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
