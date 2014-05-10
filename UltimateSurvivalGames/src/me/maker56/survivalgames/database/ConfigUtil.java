package me.maker56.survivalgames.database;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ConfigUtil {
	
	// ITEMSTACK
	
	@SuppressWarnings("deprecation")
	public static ItemStack parseItemStack(String s) {
		try {
			String[] gSplit = s.split(" ");
			ItemStack is = null;
			
			// ITEM ID / MATERIAL / SUBID
			String[] idsSplit = gSplit[0].split(":");
			try {
				is = new ItemStack(Integer.parseInt(idsSplit[0]));
			} catch(NumberFormatException e) {
				is = new ItemStack(Material.valueOf(idsSplit[0]));
			}
			
			if(idsSplit.length > 1)
				is.setDurability(Short.parseShort(idsSplit[1]));
			
			// ITEM SUBID
			if(gSplit.length > 1) {
				int metaStart = 2;
				
				// ITEM AMOUNT
				try {
					is.setAmount(Integer.parseInt(gSplit[1]));
				} catch(NumberFormatException e) {
					metaStart = 1;
				}
				
				for(int meta = metaStart; meta < gSplit.length; meta++) {
					String rawKey = gSplit[meta];
					String[] split = rawKey.split(":");
					String key = split[0];

					
					ItemMeta im = is.getItemMeta();
					if(key.equalsIgnoreCase("name")) {
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', split[1]).replace("_", " "));
					} else if(key.equalsIgnoreCase("lore")) {
						List<String> lore = new ArrayList<>();
						for(String line : split[1].split("//")) {
							lore.add(ChatColor.translateAlternateColorCodes('&', line).replace("_", " "));
						}
						im.setLore(lore);
					} else if(key.equalsIgnoreCase("color") && im instanceof LeatherArmorMeta) {
						LeatherArmorMeta lam = (LeatherArmorMeta) im;
						String[] csplit = split[1].split(",");
						Color color = Color.fromBGR(Integer.parseInt(csplit[0]), Integer.parseInt(csplit[1]), Integer.parseInt(csplit[2]));
						lam.setColor(color);
					} else if(key.equalsIgnoreCase("effect") && im instanceof PotionMeta) {
						PotionMeta pm = (PotionMeta) im;
						String[] psplit = split[1].split(",");
						pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(psplit[0]), Integer.parseInt(psplit[1]) * 20, Integer.parseInt(psplit[2])), true);
					} else if(key.equalsIgnoreCase("player") && im instanceof SkullMeta) {
						((SkullMeta)im).setOwner(split[1]);
					} else if(key.equalsIgnoreCase("enchant")) {
						String[] esplit = split[1].split(",");
						im.addEnchant(Enchantment.getByName(esplit[0].toUpperCase()), Integer.parseInt(esplit[1]), true);
					}
					is.setItemMeta(im);
				}
			}
			
			return is;
		} catch(Exception e) {
			System.err.println("[SurvivalGames] Cannot parse ItemStack: " + s + " - Mabye this is the reason: " + e.toString());
			return null;
		}
	}
	
	// LOCATION
	
	public static Location parseLocation(String s) {
		try {
			String[] split = s.split(",");
			
			World world = Bukkit.getWorld(split[0]);
			
			try {
				double x = Double.parseDouble(split[1]);
				double y = Double.parseDouble(split[2]);
				double z = Double.parseDouble(split[3]);
				
				float yaw = Float.parseFloat(split[4]);
				float pitch = Float.parseFloat(split[5]);
				return new Location(world, x, y, z, yaw, pitch);
				
			} catch(ArrayIndexOutOfBoundsException e) {
				
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);
				
				return new Location(world, x, y, z);
			}
		} catch(Exception e) {
			return null;
		}
	}
	
	public static String serializeLocation(Location l, boolean exact) {
		String key = new String();
		
		key += l.getWorld().getName() + ",";
		
		if(exact) {
			key += l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
		} else {
			key += l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
		}
		
		return key;

	}

}
