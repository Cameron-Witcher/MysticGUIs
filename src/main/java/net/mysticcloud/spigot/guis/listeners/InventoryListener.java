package net.mysticcloud.spigot.guis.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json2.JSONObject;

import me.clip.placeholderapi.PlaceholderAPI;
import net.mysticcloud.spigot.guis.utils.GuiManager;
import net.mysticcloud.spigot.guis.utils.Utils;

public class InventoryListener implements Listener {

	public InventoryListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerInventory(InventoryClickEvent e) {

		if (Utils.getGuis().containsKey((GuiManager.getOpenInventory((Player) e.getWhoClicked())))) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null)
				return;
			if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null)
				return;
			if (!e.getCurrentItem().hasItemMeta())
				return;
			Character c = Utils.getGuis().get(GuiManager.getOpenInventory((Player) e.getWhoClicked()))
					.getCharacter(e.getCurrentItem());
			if (c != null) {
				JSONObject json = Utils.getGuis().get(GuiManager.getOpenInventory((Player) e.getWhoClicked()))
						.getMetadata(c);
				if (json.has("action")) {
					switch (json.getString("action").toLowerCase()) {
					case "open_gui":
						try {
							GuiManager.openInventory((Player) e.getWhoClicked(),
									Utils.getGuis().get(json.getString("gui")).getInventory((Player) e.getWhoClicked()),
									json.getString("gui"));
						} catch (NullPointerException ex) {
							e.getWhoClicked()
									.sendMessage(Utils.PREFIX + "There was an error opening that GUI. Does it exist?");
						}
						break;
					case "join_server":
						Utils.sendPluginMessage((Player) e.getWhoClicked(), "BungeeCord", "Connect",
								json.getString("server"));
						break;
					case "buy":
						String action = json.getString("buy_action").toLowerCase();
						double price = Double.parseDouble(json.getString("price"));
						if (Utils.getEconomy().has((Player) e.getWhoClicked(), price)) {
							switch (action) {
							case "item":
								ItemStack item = new ItemStack(Material.AIR);
								String type = json.getString("item");
								if (type.startsWith("CustomItem:")) {
									// TODO custom item code
								}
								if (type.toLowerCase().startsWith("material:")
										|| type.toLowerCase().startsWith("minecraft:"))
									item = new ItemStack(Material.valueOf(type.split(":")[1].toUpperCase()));

								if (item.getType().equals(Material.AIR))
									item = new ItemStack(e.getCurrentItem().getType());
								e.getWhoClicked().getInventory().addItem(item);
								break;
							case "command":
								JSONObject command = json.getJSONObject("command");
								String cmd = Utils.setPlaceholders((Player) e.getWhoClicked(), command
										.getString("command").replaceAll("%player%", e.getWhoClicked().getName()));
								Bukkit.dispatchCommand(command.getString("sender").equalsIgnoreCase("player")
										? (Player) e.getWhoClicked()
										: Bukkit.getConsoleSender(), cmd);
								break;
							}
							Utils.getEconomy().withdrawPlayer((Player) e.getWhoClicked(), price);
							e.getWhoClicked().sendMessage(Utils.PREFIX + "Successfully purchased for $" + price);
						} else {
							e.getWhoClicked().sendMessage(Utils.PREFIX + "Sorry, you can't afford that.");
						}
						break;
					case "close_gui":
						e.getWhoClicked().closeInventory();
						break;
					}
				}
			}
		}

	}

}
