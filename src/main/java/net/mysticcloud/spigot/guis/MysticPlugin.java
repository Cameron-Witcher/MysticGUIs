package net.mysticcloud.spigot.guis;

import org.bukkit.plugin.java.JavaPlugin;

import net.mysticcloud.spigot.guis.commands.InventoryCommand;
import net.mysticcloud.spigot.guis.listeners.InventoryListener;

public class MysticPlugin extends JavaPlugin {
	
	@Override
	public void onEnable() {
		new InventoryListener(this);
		new InventoryCommand(this, "inventory");
	}
	

}
