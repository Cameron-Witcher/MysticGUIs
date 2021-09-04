package net.mysticcloud.spigot.guis.commands.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.mysticcloud.spigot.guis.utils.Utils;

public class InventoryTabCompleter implements TabCompleter {

	private List<String> invs = new ArrayList<>();

	public InventoryTabCompleter() {
		for (String key : Utils.getGuis().keySet()) {
			invs.add(key);
		}
		invs.add("remove");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		if (cmd.getName().equalsIgnoreCase("usertag")) {
			if (args.length == 1) {
				StringUtil.copyPartialMatches(args[0], invs, completions);
			}
			if (args.length == 2) {
				StringUtil.copyPartialMatches(args[1], getOnlinePlayers(), completions);
			}
		}

		return completions;

	}

	public List<String> getOnlinePlayers() {
		List<String> players = new ArrayList<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			players.add(player.getName());
		}
		return players;
	}

}
