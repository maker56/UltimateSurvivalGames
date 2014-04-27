package me.maker56.survivalgames.arena.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.database.ConfigUtil;

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
		if(title.length() > 16)
			title = title.substring(0, 16);
		
		for(String key : c.getConfigurationSection("Chestloot.").getKeys(false)) {
			List<ItemStack> l = new ArrayList<>();
			for(String itemKey : c.getStringList("Chestloot." + key)) {
				l.add(ConfigUtil.parseItemStack(itemKey));
			}
			items.add(l);
		}
	}
	
	public ChestManager() {
		reinitializeConfig();
	}
	
	public Chest getRandomChest(Player p, Location loc) {
		Inventory i = Bukkit.createInventory(p, 27, title);
		equipInventory(i);
		return new Chest(i, loc);
	}
	
	private void equipInventory(Inventory inv) {
		int stacks = r.nextInt(8) + 1;
		
		List<List<ItemStack>> groups = new ArrayList<>();
		
		for(int i = 0; i < stacks; i++) {
			groups.add(getRandomList());
		}
		
		List<ItemStack> items = new ArrayList<>();
		
		for(List<ItemStack> g : groups) {
			items.add(g.get(r.nextInt(g.size())));
		}
		
		for(ItemStack is : items) {
			inv.setItem(r.nextInt(27), is);
		}
	}
	
	private List<ItemStack> getRandomList() {
		int ri = r.nextInt(100) + 1;

		if(ri <= 40) {
			return items.get(0);
		} else if(ri > 40 & ri <= 70) {
			return items.get(1);
		} else if(ri > 70 & ri <= 85) {
			return items.get(2);
		} else if(ri > 85 & ri <= 95) {
			return items.get(3);
		} else {
			return items.get(4);
		}
	}

}
