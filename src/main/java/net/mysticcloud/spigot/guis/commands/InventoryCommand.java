package net.mysticcloud.spigot.guis.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.mysticcloud.spigot.guis.commands.listeners.InventoryTabCompleter;
import net.mysticcloud.spigot.guis.utils.GuiManager;
import net.mysticcloud.spigot.guis.utils.Utils;

public class InventoryCommand implements CommandExecutor {

	public InventoryCommand(JavaPlugin plugin, String cmd) {
		plugin.getCommand(cmd).setExecutor(this);
		plugin.getCommand(cmd).setTabCompleter(new InventoryTabCompleter());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length == 0)
				return false;
			if (sender.hasPermission("guis." + args[0])) {
				Player opener = args.length >= 2 ? Bukkit.getPlayer(args[1]) : (Player) sender;
				if (opener == null) {
					sender.sendMessage(Utils.prefix + "Sorry, that player doesn't seem to be online.");
					return true;
				}
				try {
					GuiManager.openInventory(opener, Utils.getGuis().get(args[0]).getInventory(), args[0]);
				} catch (NullPointerException ex) {
					sender.sendMessage(Utils.prefix + "There was an error opening that GUI. Does it exist?");
				}

			}
		}
		return true;
	}
}
