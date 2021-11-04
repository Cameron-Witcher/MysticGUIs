package net.mysticcloud.spigot.guis.utils.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.potion.PotionEffect;

public class FoodInfo {

	String name;
	int hunger = 1;
	int health = 0;
	List<PotionEffect> potions = new ArrayList<>();

	public FoodInfo(String name) {
		this.name = name;
	}

	public void setHungerLevel(int level) {
		this.hunger = level;
	}

	public int getHungerLevel() {
		return hunger;
	}

	public void addPotionEffect(PotionEffect effect) {
		potions.add(effect);
	}

	public void setPotionEffects(ArrayList<PotionEffect> potions) {
		this.potions = potions;
	}

	public void setPotionEffects(List<PotionEffect> potions) {
		this.potions = potions;
	}

	public List<PotionEffect> getPotionEffects() {
		return potions;
	}

	public void setHealingFactor(int healing) {
		this.health = healing;
	}

	public double getHealingFactor() {
		return health;
	}

}
