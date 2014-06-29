package me.maker56.survivalgames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.listener.UpdateListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.schematic.SchematicFormat;

public class Util {
	
	// ITEMSTACK
	public static boolean debug = false;
	private static Random random = new Random();
	
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
			
			if(gSplit.length > 1) {
				int metaStart = 2;
				
				try {
					is.setAmount(Integer.parseInt(gSplit[1]));
				} catch(NumberFormatException e) {
					metaStart = 1;
				}
				ItemMeta im = is.getItemMeta();
				for(int meta = metaStart; meta < gSplit.length; meta++) {
					String rawKey = gSplit[meta];
					String[] split = rawKey.split(":");
					String key = split[0];
					
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
						im.addEnchant(getEnchantment(esplit[0]), Integer.parseInt(esplit[1]), true);
					}
					
				}
				is.setItemMeta(im);
			}
			
			return is;
		} catch(Exception e) {
			System.err.println("[SurvivalGames] Cannot parse ItemStack: " + s + " - Mabye this is the reason: " + e.toString());
			return null;
		}
	}
	
	public static void shootRandomFirework(Location loc, int height) {
		Firework f = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fm = f.getFireworkMeta();
		fm.setPower(height);
		int effectAmount = random.nextInt(3) + 1;
		for(int i = 0; i < effectAmount; i++) {
			Builder b = FireworkEffect.builder();
			int colorAmount = random.nextInt(3) + 1;
			for(int ii = 0; ii < colorAmount; ii++) {
				b.withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
			}
			b.with(Type.values()[random.nextInt(Type.values().length)]);
			b.flicker(random.nextInt(2) == 0 ? false : true);
			b.trail(random.nextInt(2) == 0 ? false : true);
			fm.addEffect(b.build());
		}
		f.setFireworkMeta(fm);
	}
	
	// ENCHANTMENT
	public static Enchantment getEnchantment(String enc) {
		enc = enc.toUpperCase();
		Enchantment en = Enchantment.getByName(enc);
		
		if(en == null) {
			switch (enc) {
			case "PROTECTION":
				en = Enchantment.PROTECTION_ENVIRONMENTAL;
				break;
			case "FIRE_PROTECTION":
				en = Enchantment.PROTECTION_FIRE;
				break;
			case "FEATHER_FALLING":
				en = Enchantment.PROTECTION_FALL;
				break;
			case "BLAST_PROTECTION":
				en = Enchantment.PROTECTION_EXPLOSIONS;
				break;
			case "PROJECTILE_PROTCETION":
				en = Enchantment.PROTECTION_PROJECTILE;
				break;
			case "RESPIRATION":
				en = Enchantment.OXYGEN;
				break;
			case "AQUA_AFFINITY":
				en = Enchantment.WATER_WORKER;
				break;
			case "SHARPNESS":
				en = Enchantment.DAMAGE_ALL;
				break;
			case "SMITE":
				en = Enchantment.DAMAGE_UNDEAD;
				break;
			case "BANE_OF_ARTHROPODS":
				en = Enchantment.DAMAGE_ARTHROPODS;
				break;
			case "LOOTING":
				en = Enchantment.LOOT_BONUS_MOBS;
				break;
			case "EFFICIENCY":
				en = Enchantment.DIG_SPEED;
				break;
			case "UNBREAKING":
				en = Enchantment.DURABILITY;
				break;
			case "FORTUNE":
				en = Enchantment.LOOT_BONUS_BLOCKS;
				break;
			case "POWER":
				en = Enchantment.ARROW_DAMAGE;
				break;
			case "PUNCH":
				en = Enchantment.ARROW_KNOCKBACK;
				break;
			case "FLAME":
				en = Enchantment.ARROW_FIRE;
				break;
			case "INFINITY":
				en = Enchantment.ARROW_INFINITE;
				break;
			case "LUCK_OF_THE_SEA":
				en = Enchantment.LUCK;
				break;
			}
		}
		
		return en;
	}
	// TIME
	
	public static String getFormatedTime(int seconds) {
		int minutes = seconds / 60;
		int hours = minutes / 60;
		int days = hours / 24;
		
		seconds -= minutes * 60;
		minutes -= hours * 60;
		hours -= days * 24;
		
		String s = "";
		if(days > 0)
			s += days + "d";
		if(hours > 0)
			s += hours + "h";
		if(minutes > 0)
			s += minutes + "m";
		if(seconds > 0) 
			s += seconds + "s";
	
		return s;
	}
	
	// LOCATION
	
	public static Location parseLocation(String s) {
		String[] split = s.split(",");
		Location loc = null;
		
		
		try {
			World world = Bukkit.getWorld(split[0]);
			if(split.length == 6) {
				double x = Double.parseDouble(split[1]);
				double y = Double.parseDouble(split[2]);
				double z = Double.parseDouble(split[3]);
				
				float yaw = Float.parseFloat(split[4]);
				float pitch = Float.parseFloat(split[5]);
				loc = new Location(world, x, y, z, yaw, pitch);
			} else if(split.length == 4) {
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);
				
				loc = new Location(world, x, y, z);
			}
		} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
			System.err.println("[SurvivalGames] Cannot parse location from string: " + s);
		}
		
		return loc;
	}
	
	public static String serializeLocation(Location l, boolean exact) {
		if(l != null) {
			String key = l.getWorld().getName() + ",";
			if(exact) {
				key += l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
			} else {
				key += l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
			}
			
			return key;
		}
		return null;
	}
	
	public static void debug(Object object) {
		if(debug) {
			System.out.println("[SurvivalGames] [Debug] " + object.toString());
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.isOp()) 
					p.sendMessage("§7[Debug] " + object.toString());
			}
		}
	}
	
	// TEMPORARY UPDATE METHODS
	
	public static void checkForOutdatedArenaSaveFiles() {
		File f = new File("plugins/SurvivalGames/reset/");
		List<String> outdated = new ArrayList<>();
		if(f.exists()) {
			for(String key : f.list()) {
				if(!key.endsWith(".map"))
					continue;
				File file = new File("plugins/SurvivalGames/reset/" + key);
				SchematicFormat sf = SchematicFormat.getFormat(file);
				if(sf == null) {
					outdated.add(key);
				}
			}
		}
		String s = null;
		if(!outdated.isEmpty()) {
			s = MessageHandler.getMessage("prefix") + "§cThe format of " + outdated.size() + " map saves is outdated§7: §e";
			for(int i = 0; i < outdated.size(); i++) {
				s+= outdated.get(i);
				if(i != outdated.size() - 1) {
					s+= "§7, §e";
				} else {
					s+= " §c! ";
				}

			}
			s+= "Select all the arenas with §l/sg arena select §cand type §c§l/sg arena save§c! In the old format, the arenas will not reset!";
		}
		UpdateListener.setOutdatedMaps(s);
	}

}
