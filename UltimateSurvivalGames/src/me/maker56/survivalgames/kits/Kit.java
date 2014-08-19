package me.maker56.survivalgames.kits;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public class Kit {
	
	private String name, description, permission;
	private double costMoney;
	private int costPoints;
	private PayType type;
	private ItemStack itemstack;
	
	protected int slot = -1;
	
	public Kit(String name, String description, ItemStack itemstack, String permission, double money, int points, PayType type) {
		this.name = name;
		this.description = description;
		this.permission = permission;
		this.costMoney = money;
		this.costPoints = points;
		this.type = type;
		this.itemstack = itemstack;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public ItemStack getItemStack() {
		return itemstack;
	}
	
	public PayType getPayType() {
		return type;
	}
	
	public double getCostMoney() {
		return costMoney;
	}
	
	public int getCostPoints() {
		return costPoints;
	}
	
	public static enum PayType { MONEY, POINTS }
	
	// ABILITIES / ITEMS
	private List<ItemStack> items = new ArrayList<>();
	private List<Abilities> abilities = new ArrayList<>();
	
	public List<ItemStack> getItems() {
		return items;
	}
	
	public List<Abilities> getAbilities() {
		return abilities;
	}
}
