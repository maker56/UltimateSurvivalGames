package me.maker56.survivalgames.arena.chest;

import java.util.ArrayList;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestManager {
	
	private static List<List<String>> items = new ArrayList<>();
	private static FileConfiguration c;
	private static String title;
	
	public static void reinitializeConfig() {
		c = SurvivalGames.instance.getConfig();
		for(String key : c.getConfigurationSection("Chestloot.").getKeys(false)) {
			List<String> l = c.getStringList("Chestloot." + key);
			items.add(l);
		}
		title = ChatColor.translateAlternateColorCodes('&', c.getString("Chest-Title", "Survival Chest"));
		if(title.length() > 16)
			title = title.substring(0, 16);
	}
	
	public ChestManager() {
		reinitializeConfig();
	}
	
	public Chest getRandomChest(Player p, Location loc) {
		Inventory i = Bukkit.createInventory(p, 27, title);
		equipInventory(i);
		Chest chest = new Chest(i, loc);
		return chest;
	}
	
	private void equipInventory(Inventory inv) {
		int stacks = (int) (Math.random() * (9 - 2) + 2);
		
		List<List<String>> lstacks = new ArrayList<>();
		
		for(int i = 1; i < stacks; i++) {
			lstacks.add(getRandomList());
		}
		
		List<String> sitems = new ArrayList<>();
		
		for(List<String> ls : lstacks) {
			sitems.add(ls.get((int) (Math.random() * (ls.size() - 0) + 0)));
		}
		
		List<ItemStack> items = new ArrayList<>();
		
		for(String s : sitems) {
			String[] hsplit = s.split(",");
			String[] msplit = hsplit[0].split(":");
			
			@SuppressWarnings("deprecation")
			Material mat = Material.getMaterial(Integer.parseInt(msplit[0]));
			
			if(mat == null)
				continue;
			
			ItemStack is = new ItemStack(mat);
			
			try {
				is.setDurability(Short.parseShort(msplit[1]));
			} catch(ArrayIndexOutOfBoundsException e) { }
			
			is.setAmount(Integer.parseInt(hsplit[1]));
			items.add(is);
		}
		
		for(ItemStack is : items) {
			int slot = (int) (Math.random() * (27 - 0) + 0);
			inv.setItem(slot, is);
		}
	}
	
	private List<String> getRandomList() {
		int r = (int) (Math.random() * (106 - 1) + 1);
		
		if(r <= 40) {
			return items.get(0);
		} else if(r > 40 & r <= 70) {
			return items.get(1);
		} else if(r > 70 & r <= 90) {
			return items.get(2);
		} else if(r > 90 & r <= 100) {
			return items.get(3);
		} else {
			return items.get(4);
		}
	}

}
