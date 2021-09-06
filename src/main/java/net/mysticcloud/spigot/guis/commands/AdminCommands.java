package net.mysticcloud.spigot.guis.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.mysticcloud.spigot.guis.MysticPlugin;
import net.mysticcloud.spigot.guis.utils.Perm;
import net.mysticcloud.spigot.guis.utils.Utils;

public class AdminCommands implements CommandExecutor {

	public AdminCommands(JavaPlugin plugin, String... cmds) {
		for (String cmd : cmds) {
			plugin.getCommand(cmd).setExecutor(this);
//			plugin.getCommand(cmd).setTabCompleter(new AdminTabCompleter());
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("guis")) {
			if (sender.hasPermission(Perm.ADMIN)) {
//				boolean isp = sender instanceof Player;
				if (args.length == 0) {
					sender.sendMessage(Utils.colorize(Utils.PREFIX + "Below are all the avalible sub-commands"));
					sender.sendMessage(Utils.colorize(
							"/" + label + " update - Update the current jar automatically. (restart required)"));
					if (Utils.limited())
						sender.sendMessage(Utils.colorize(
								"/" + label + " setkey <license> - Set your license key to unlock the full version."));
					return true;
				}
				if (args[0].equalsIgnoreCase("update")) {
					if (Utils.update()) {
						sender.sendMessage(Utils.colorize(Utils.PREFIX + "Successfully downloaded " + Utils.PLUGIN
								+ ".jar. Please restart the server as soon as possible to avoid any fatal bugs"));
					}
				}
				if (args[0].equalsIgnoreCase("setkey")) {
					if (args.length == 1) {
						if (Utils.limited())
							sender.sendMessage(Utils.colorize("/" + label
									+ " setkey <license> - Set your license key to unlock the full version."));
						else {
							sender.sendMessage(Utils.colorize(
									Utils.PREFIX + "You've already set your license and unlocked the full version."));
						}
						return true;
					}
					Utils.getPlugin().getConfig().set("license", args[1]);
					Utils.getPlugin().saveConfig();
					sender.sendMessage(
							Utils.colorize(Utils.PREFIX + "Saved license to config. Attempting to unlock plugin..."));

					sender.sendMessage(Utils.colorize(Utils.PREFIX + (((MysticPlugin) Utils.getPlugin()).register()
							? "Successfully registered and unlocked with license " + args[0] + "."
							: "There was an error registering using that key. Check your key again, and attempt a restart.")));

					Utils.registerGuis();

				}
			} else {
				sender.sendMessage(
						Utils.colorize(Utils.PREFIX + "Sorry, you don't have permission to use that command."));
			}
		}
		return true;
	}
}
