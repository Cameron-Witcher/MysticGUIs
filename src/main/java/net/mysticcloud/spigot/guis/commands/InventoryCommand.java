package net.mysticcloud.spigot.guis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.mysticcloud.spigot.guis.utils.GuiManager;
import net.mysticcloud.spigot.guis.utils.Utils;

public class InventoryCommand implements CommandExecutor {

	public InventoryCommand(JavaPlugin plugin, String cmd) {
		plugin.getCommand(cmd).setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0)
				return false;
			if (sender.hasPermission("guis." + args[0])) {
				GuiManager.openInventory((Player) sender, Utils.getGuis().get(args[0]).getInventory(), args[0]);
			}
		}
		return true;
	}
}
