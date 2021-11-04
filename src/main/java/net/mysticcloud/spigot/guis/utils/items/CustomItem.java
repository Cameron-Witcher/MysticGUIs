package net.mysticcloud.spigot.guis.utils.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.mysticcloud.spigot.guis.utils.Utils;

public class CustomItem {
	Material type;
	int amount = 1;
	String dname = "default_name";
	boolean unbreakable = false;
	List<EnchantmentWrapper> enchantments = new ArrayList<>();
	List<String> lore = new ArrayList<>();
	List<ItemFlag> itemFlags = new ArrayList<>();

	public CustomItem(Material type) {
		this.type = type;
	}

	public void setType(Material type) {
		this.type = type;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public CustomItem clone() {
		return this;
	}

	public Material getType() {
		return type;
	}

	public void setDisplayName(String dname) {
		this.dname = Utils.colorize(dname);
	}

	public void setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
	}

	public void addEnchant(Enchantment en, int strength, boolean ambient) {
		enchantments.add(new EnchantmentWrapper(en, strength, ambient));
	}

	public void setLore(List<String> lore) {
		this.lore = lore;
	}

	public void addItemFlags(ItemFlag... flags) {
		for (ItemFlag flag : flags)
			itemFlags.add(flag);
	}

	public boolean hasDisplayName() {
		return !dname.equals("default_name");
	}

	public String getDisplayName() {
		return dname;
	}

	public ItemStack getItem(Player player) {
		List<String> lore = new ArrayList<>();
		for (String s : this.lore)
			lore.add(Utils.setPlaceholders(player, s));
		ItemStack item = new ItemStack(type);
		item.setAmount(amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.setPlaceholders(player, dname));
		meta.setUnbreakable(unbreakable);
		for (EnchantmentWrapper enw : enchantments) {
			meta.addEnchant(enw.getEnchantment(), enw.getStrength(), enw.getAmbient());
		}
		if (!lore.isEmpty())
			meta.setLore(lore);
		if (!itemFlags.isEmpty())
			for (ItemFlag flag : itemFlags)
				meta.addItemFlags(flag);

		item.setItemMeta(meta);
		return item;
	}

}
