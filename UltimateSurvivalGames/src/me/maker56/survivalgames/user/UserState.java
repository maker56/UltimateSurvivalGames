package me.maker56.survivalgames.user;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class UserState {
	
  private double health;
  private int food;
  private float exp;
  private int level;
  private int fireticks;
  private ItemStack[][] inventory;
  private Location loc;
  private GameMode gamemode;
  private boolean allowFlying, flying;
  private Collection<PotionEffect> ape;
  private float fall;

  public UserState(Player p) {
    this.health = ((Damageable) p).getHealth();
    this.food = p.getFoodLevel();
    this.exp = p.getExp();
    this.level = p.getLevel();
    this.fireticks = p.getFireTicks();
    this.loc = p.getLocation();
    this.gamemode = p.getGameMode();
    this.allowFlying = p.getAllowFlight();
    this.flying = p.isFlying();
    this.ape = p.getActivePotionEffects();
    this.fall = p.getFallDistance();

    ItemStack[][] store = new ItemStack[2][1];
    store[0] = p.getInventory().getContents();
    store[1] = p.getInventory().getArmorContents();
    this.inventory = store;
  }
  
  public float getFallDistance() {
	  return fall;
  }
  
  public ItemStack[] getContents() {
	  return inventory[0];
  }
  
  public ItemStack[] getArmorContents() {
	  return inventory[1];
  }
  
  public Collection<PotionEffect> getActivePotionEffects() {
	  return ape;
  }
  
  public GameMode getGameMode() {
	  return gamemode;
  }
  
  public boolean getAllowFlight() {
	  return allowFlying;
  }
  
  public boolean isFlying() {
	  return flying;
  }

  public Location getLocation() {
    return this.loc;
  }

  public double getHealth() {
    return this.health;
  }

  public int getFoodLevel() {
    return this.food;
  }

  public float getExp() {
    return this.exp;
  }

  public int getLevel() {
    return this.level;
  }

  public int getFireTicks() {
    return this.fireticks;
  }

  public ItemStack[][] getInventory() {
    return this.inventory;
  }
}