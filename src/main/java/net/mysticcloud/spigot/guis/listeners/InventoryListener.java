package net.mysticcloud.spigot.guis.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json2.JSONObject;

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
									Utils.getGuis().get(json.getString("gui")).getInventory(), json.getString("gui"));
						} catch (NullPointerException ex) {
							e.getWhoClicked()
									.sendMessage(Utils.prefix + "There was an error opening that GUI. Does it exist?");
						}
						break;
					case "join_server":
						Utils.sendPluginMessage((Player) e.getWhoClicked(), "Connect", json.getString("server"));
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
