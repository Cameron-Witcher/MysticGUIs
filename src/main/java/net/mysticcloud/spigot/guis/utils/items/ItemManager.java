package net.mysticcloud.spigot.guis.utils.items;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import net.mysticcloud.spigot.guis.utils.Utils;

public class ItemManager {

	private static File itemfile = null;
	private static List<FileConfiguration> itemFiles = new ArrayList<>();
	private static Map<String, CustomItem> items = new HashMap<>();

	public static void init() {
		itemfile = new File(Utils.getPlugin().getDataFolder() + "/items");
		if (!itemfile.exists()) {
			itemfile.mkdirs();
		}
		try {

			for (File file : itemfile.listFiles())
				if (file.getName().startsWith("item") && file.getName().endsWith(".yml"))
					itemFiles.add(YamlConfiguration.loadConfiguration(file));
			itemFiles.size();
		} catch (NullPointerException ex) {
			itemFiles.add(generateDefaultCustomItemFile());
		}
		if (itemFiles.size() == 0)
			itemFiles.add(generateDefaultCustomItemFile());
	}

	private static FileConfiguration generateDefaultCustomItemFile() {
		File file = new File(itemfile.getPath() + "/itemDefault.yml");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);

		fc.set("SuperSword.Id", "DIAMOND_SWORD");
		fc.set("SuperSword.Options.Display", "&6Super Sword");

		try {
			fc.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fc;
	}

	public static CustomItem getCustomItem(String iname) {
		CustomItem i = new CustomItem(Material.STONE);
		int amount = 1;
		if (iname.contains("-")) {
			amount = Integer.parseInt(iname.split("-")[1]);
			iname = iname.split("-")[0];
		}
		if (items.containsKey(iname)) {

			i = items.get(iname).clone();
			i.setAmount(amount);
			return i;
		}
//		boolean food = false;
//		FoodInfo info = new FoodInfo(iname);
		boolean found = false;

		for (FileConfiguration item : itemFiles) {
			if (!item.isSet(iname))
				continue;
			found = true;

			if (item.isSet(iname + ".Id"))
				i.setType(Material.valueOf(item.getString(iname + ".Id")));

			i.setDisplayName(item.getString(iname + ".Options.Display",
					ChatColor.RESET + (i.getType() + "").substring(0, 1).toUpperCase()
							+ (i.getType() + "").substring(1, (i.getType() + "").length()).toLowerCase()));

			if (item.isSet(iname + ".Options.Unbreakable"))
				i.setUnbreakable(Boolean.parseBoolean(item.getString(iname + ".Options.Unbreakable")));

			if (item.isSet(iname + ".Options.Enchantments")) {

				for (String b : item.getStringList(iname + ".Options.Enchantments")) {
					for (Enchantment en : Enchantment.values()) {
						if (en.getName().equalsIgnoreCase(b.split(":")[0])) {
							i.addEnchant(en, Integer.parseInt(b.split(":")[1]), true);
							break;
						}
					}

				}
			}

			if (item.isSet(iname + ".Options.Lore")) {
				List<String> lore = new ArrayList<>();
				if (item.get(iname + ".Options.Lore") instanceof List<?>) {
					for (String s : item.getStringList(iname + ".Options.Lore")) {
						lore.add(Utils.colorize(s));
					}
				}
				if (item.get(iname + ".Options.Lore") instanceof String) {
					lore.add(Utils.colorize(item.getString(iname + ".Options.Lore")));
				}
				if (!lore.isEmpty())
					i.setLore(lore);
			}

//			if (item.isSet(iname + ".Food.Hunger")) {
//				food = true;
//				info.setHungerLevel(item.getInt(iname + ".Food.Hunger"));
//			}
//			if (item.isSet(iname + ".Food.Potion")) {
//				food = true;
//				for (String s : item.getStringList(iname + ".Food.Potion"))
//					info.addPotionEffect(new PotionEffect(PotionEffectType.getByName(s.split("-")[0].toUpperCase()),
//							Integer.parseInt(s.split("-")[1]), Integer.parseInt(s.split("-")[2])));
//
//			}
//			if (item.isSet(iname + ".Food.Health")) {
//				food = true;
//				info.setHealingFactor(item.getInt(iname + ".Food.Health"));
//			}
//			if (food) {
//				List<String> lore = a.hasLore() ? a.getLore() : new ArrayList<>();
//				lore.add(ChatColor.DARK_GRAY + "Food:" + iname);
//				a.setLore(lore);
//			}
			i.setAmount(amount);

//			if (item.isSet(iname + ".Attributes.MainHand.Damage")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Attack Damage",
//						item.getDouble(iname + ".Attributes.MainHand.Damage"), Operation.ADD_NUMBER,
//						EquipmentSlot.HAND);
//				a.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, am);
//			}
//			if (item.isSet(iname + ".Attributes.MainHand.AttackSpeed")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Attack Speed",
//						item.getDouble(iname + ".Attributes.MainHand.AttackSpeed"), Operation.ADD_NUMBER,
//						EquipmentSlot.HAND);
//				a.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, am);
//			}
//			if (item.isSet(iname + ".Attributes.Helmet.Protection")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Helmet Protection",
//						item.getDouble(iname + ".Attributes.Helmet.Protection"), Operation.ADD_NUMBER,
//						EquipmentSlot.HEAD);
//				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);
//			}
//			if (item.isSet(iname + ".Attributes.Chestplate.Protection")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Chestplate Protection",
//						item.getDouble(iname + ".Attributes.Chestplate.Protection"), Operation.ADD_NUMBER,
//						EquipmentSlot.CHEST);
//				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);
//			}
//			if (item.isSet(iname + ".Attributes.Pants.Protection")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Pants Protection",
//						item.getDouble(iname + ".Attributes.Pants.Protection"), Operation.ADD_NUMBER,
//						EquipmentSlot.LEGS);
//				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);
//			}
//			if (item.isSet(iname + ".Attributes.Boots.Protection")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Boots Protection",
//						item.getDouble(iname + ".Attributes.Boots.Protection"), Operation.ADD_NUMBER,
//						EquipmentSlot.FEET);
//				a.addAttributeModifier(Attribute.GENERIC_ARMOR, am);
//
//			}
//
//			// Attribute.
//			if (item.isSet(iname + ".Attributes.Helmet.MovementSpeed")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Helmet Movement Speed",
//						item.getDouble(iname + ".Attributes.Helmet.MovementSpeed"), Operation.ADD_NUMBER,
//						EquipmentSlot.HEAD);
//				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
//			}
//			if (item.isSet(iname + ".Attributes.Chestplate.MovementSpeed")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Chestplate Movement Speed",
//						item.getDouble(iname + ".Attributes.Chestplate.MovementSpeed"), Operation.ADD_NUMBER,
//						EquipmentSlot.CHEST);
//				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
//			}
//			if (item.isSet(iname + ".Attributes.Pants.MovementSpeed")) {
//
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Pants Movement Speed",
//						item.getDouble(iname + ".Attributes.Pants.MovementSpeed"), Operation.ADD_NUMBER,
//						EquipmentSlot.LEGS);
//				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
//			}
//			if (item.isSet(iname + ".Attributes.Boots.MovementSpeed")) {
//				AttributeModifier am = new AttributeModifier(UUID.randomUUID(), "Boots Movement Speed",
//						item.getDouble(iname + ".Attributes.Boots.MovementSpeed"), Operation.ADD_NUMBER,
//						EquipmentSlot.FEET);
//				a.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, am);
//			}

			if (item.isSet(iname + ".Options.Hide"))
				if (item.get(iname + ".Options.Hide") != "All")
					for (String s : item.getStringList(iname + ".Options.Hide"))
						i.addItemFlags(ItemFlag.valueOf("HIDE_" + s.toUpperCase()));
				else
					i.addItemFlags(ItemFlag.values());

		}
		i.setDisplayName(i.hasDisplayName() ? i.getDisplayName() : Utils.colorize("&cERROR"));
		if (!found)

		{
			Utils.log("Item was not found.");

			return i.clone();
		}

		Utils.log("Item loaded from config, and saved to cache.");
//		if (food) {
//			foods.put(iname, i.clone());
//			debug("Item " + iname + " was food.");
//		}
		items.put(iname, i.clone());

//		if (food)
//			CoreUtils.food.put(iname, info);
		return i.clone();
	}

}
