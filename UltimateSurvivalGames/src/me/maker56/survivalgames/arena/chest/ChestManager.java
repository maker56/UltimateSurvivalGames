package me.maker56.survivalgames.arena.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.SurvivalGames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestManager {
	
	private static List<List<ItemStack>> items = new ArrayList<>();
	private static FileConfiguration c;
	private static String title;
	
	private Random r = new Random();
	
	public static void reinitializeConfig() {
		c = SurvivalGames.chestloot;
		title = ChatColor.translateAlternateColorCodes('&', c.getString("Chest-Title", "Survival Chest"));
		if(title.length() > 32)
			title = title.substring(0, 32);
		
		for(String key : c.getConfigurationSection("Chestloot.").getKeys(false)) {
			List<ItemStack> l = new ArrayList<>();
			for(String itemKey : c.getStringList("Chestloot." + key)) {
				l.add(Util.parseItemStack(itemKey));
			}
			items.add(l);
		}
	}
	
	public ChestManager() {
		reinitializeConfig();
	}
	
	public Chest getRandomChest(Player p, Location loc, boolean rare) {
		Inventory i = Bukkit.createInventory(p, 27, title);
		equipInventory(i, rare);
		return new Chest(i, loc);
	}
	
	private void equipInventory(Inventory inv, boolean rare) {
		int stacks = r.nextInt(rare ? 4 : 8) + 1;
		
		List<List<ItemStack>> groups = new ArrayList<>();
		
		for(int i = 0; i < stacks; i++) {
			groups.add(getRandomList(rare));
		}
		
		List<ItemStack> items = new ArrayList<>();
		
		for(List<ItemStack> g : groups) {
			items.add(g.get(r.nextInt(g.size())));
		}
		
		for(ItemStack is : items) {
			inv.setItem(r.nextInt(27), is);
		}
	}
	
	private List<ItemStack> getRandomList(boolean rare) {
		int ri = r.nextInt(100) + 1;
		if(!rare) {
			if(ri <= 40) {
				return items.get(0);
			} else if(ri <= 70) {
				return items.get(1);
			} else if(ri <= 85) {
				return items.get(2);
			} else if(ri <= 95) {
				return items.get(3);
			} else {
				return items.get(4);
			}
		} else {
			if(ri <= 60) {
				return items.get(3);
			} else if(ri <= 80) {
				return items.get(2);
			} else {
				return items.get(4);
			}
		}
		
	}

}
