package net.mysticcloud.spigot.guis.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json2.JSONObject;

import net.mysticcloud.spigot.guis.utils.Utils;
import net.mysticcloud.spigot.guis.utils.gui.GuiItem;
import net.mysticcloud.spigot.guis.utils.gui.GuiManager;

public class InventoryListener implements Listener {

	public InventoryListener(JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerCloseInventory(InventoryCloseEvent e) {
		if (!e.getPlayer().hasMetadata("switchinv")) {
			GuiManager.closeInventory((Player) e.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerInventory(InventoryClickEvent e) {

		if (((Utils.getGuis().containsKey(GuiManager.getOpenInventory((Player) e.getWhoClicked()))))) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null)
				return;
			if (e.getCurrentItem().getType() == Material.AIR || e.getCurrentItem().getType() == null)
				return;
			if (!e.getCurrentItem().hasItemMeta())
				return;

			if (Utils.getGuis().get(GuiManager.getOpenInventory((Player) e.getWhoClicked())).hasItem(e.getCurrentItem(),
					(Player) e.getWhoClicked())) {
				GuiItem item = Utils.getGuis().get(GuiManager.getOpenInventory((Player) e.getWhoClicked()))
						.getItem(e.getCurrentItem(), (Player) e.getWhoClicked());
				if (item.hasAction()) {
					if (item.isSingleAction()) {
						Utils.processAction((Player) e.getWhoClicked(), item, item.getAction());
					} else {
						item.getActions().forEach(a -> {
							JSONObject action = (JSONObject) a;
							Utils.log(action.toString());
							if (e.getClick().equals(ClickType
									.valueOf(action.getString("click").toUpperCase().replaceAll("_CLICK", "")))) {
								if (!Utils.processAction((Player) e.getWhoClicked(), item, action))
									return;
							}
						});
					}
				}
			}
		}
	}
}
