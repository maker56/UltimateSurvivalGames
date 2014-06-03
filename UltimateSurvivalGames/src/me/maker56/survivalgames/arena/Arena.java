package me.maker56.survivalgames.arena;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

public class Arena implements Cloneable {

	private Location min, max;
	private List<Location> spawns;
	private int graceperiod;
	private String name;
	private String game;
	private List<Integer> allowedBlocks;
	private double moneyKill, moneyWin;
	
	private Material chesttype;
	private int chestdata;
	
	private boolean deathmatch, refill;
	private List<Location> deathmatchSpawns;
	
	private int autodeathmatch, playerdeathmatch;
	
	private int votes = 0;
	
	public Arena(Location min, Location max, List<Location> spawns, Material chesttype, int chestdata, int graceperiod, String name, String game, boolean deathmatch, List<Location> deathmatchspawns, List<Integer> allowedBlocks, int autodeathmatch, int playerdeathmatch, double moneyKill, double moneyWin, boolean chestrefill) {
		this.min = min;
		this.max = max;
		this.spawns = spawns;
		this.graceperiod = graceperiod;
		this.name = name;
		this.game = game;
		this.allowedBlocks = allowedBlocks;
		this.chesttype = chesttype;
		this.chestdata = chestdata;
		this.deathmatch = deathmatch;
		this.deathmatchSpawns = deathmatchspawns;
		this.autodeathmatch = autodeathmatch;
		this.playerdeathmatch = playerdeathmatch;
		this.moneyKill = moneyKill;
		this.moneyWin = moneyWin;
		this.refill = chestrefill;
		
		if(min != null)
			min.getWorld().setStorm(false);
	}
	
	public int getAutomaticlyDeathmatchTime() {
		return autodeathmatch;
	}
	
	public int getPlayerDeathmatchAmount() {
		return playerdeathmatch;
	}
	
	public boolean isDeathmatchEnabled() {
		return deathmatch;
	}
	
	public List<Location> getDeathmatchSpawns() {
		return deathmatchSpawns;
	}
	
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	public int getVotes() {
		return votes;
	}
	
	public Material getChestType() {
		return chesttype;
	}
	
	public int getChestData() {
		return chestdata;
	}
	
	public List<Integer> getAllowedMaterials() {
		return allowedBlocks;
	}
	
	public double getMoneyOnKill() {
		return moneyKill;
	}
	
	public double getMoneyOnWin() {
		return moneyWin;
	}
	
	public boolean chestRefill() {
		return refill;
	}
	
	public boolean containsBlock(Location loc) {
		if(!loc.getWorld().equals(min.getWorld()))
			return false;
		
		if(loc.getBlockX() >= min.getBlockX() && loc.getBlockX() <= max.getBlockX()) {
			if(loc.getBlockY() >= min.getBlockY() && loc.getBlockY() <= max.getBlockY()) {
				if(loc.getBlockZ() >= min.getBlockZ() && loc.getBlockX() <= max.getBlockZ()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Location getMinimumLocation() {
		return min;
	}
	
	public Location getMaximumLocation() {
		return max;
	}
	
	public List<Location> getSpawns() {
		return spawns;
	}
	
	public int getGracePeriod() {
		return graceperiod;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGame() {
		return game;
	}
	
}
