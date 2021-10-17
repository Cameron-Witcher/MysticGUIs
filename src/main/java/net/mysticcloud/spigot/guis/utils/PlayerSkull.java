package net.mysticcloud.spigot.guis.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerSkull {

	String player;

	public PlayerSkull(String player) {
		this.player = player;
	}

	@SuppressWarnings("deprecation")
	public ItemStack getSkull(Player player) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(Utils.setPlaceholders(player, this.player));
		skull.setItemMeta(meta);
		return skull;
	}

}
