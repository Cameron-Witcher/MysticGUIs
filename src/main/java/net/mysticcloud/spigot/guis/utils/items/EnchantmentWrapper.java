package net.mysticcloud.spigot.guis.utils.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class EnchantmentWrapper {
	Enchantment en;
	int strength;
	boolean ambient;

	public EnchantmentWrapper(Enchantment en, int strength, boolean ambient) {
		this.en = en;
		this.strength = strength;
		this.ambient = ambient;
	}

	public Enchantment getEnchantment() {
		return en;
	}

	public int getStrength() {
		return strength;
	}

	public boolean getAmbient() {
		return ambient;
	}

}
