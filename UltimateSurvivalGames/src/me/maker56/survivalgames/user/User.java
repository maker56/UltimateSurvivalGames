package me.maker56.survivalgames.user;

import java.util.Iterator;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import me.maker56.survivalgames.game.Game;

public class User {
	
	private UserState state;
	private Game game;
	private Player player;
	
	private int spawnIndex = Integer.MIN_VALUE;
	
	public User(Player player, Game game) {
		this.state = new UserState(player);
		this.player = player;
		this.game = game;
	}
	
	@SuppressWarnings("deprecation")
	public void clear() {
		for(Iterator<PotionEffect> i = player.getActivePotionEffects().iterator(); i.hasNext();) {
			player.removePotionEffect(i.next().getType());
		}
		
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setExp(0);
		player.setFireTicks(0);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		player.setAllowFlight(false);
		
		ItemStack[] inv = player.getInventory().getContents();
	    for (int i = 0; i < inv.length; i++) {
	      inv[i] = null;
	    }
	    player.getInventory().setContents(inv);
	    inv = player.getInventory().getArmorContents();
	    for (int i = 0; i < inv.length; i++) {
	      inv[i] = null;
	    }
	    player.getInventory().setArmorContents(inv);
	    player.updateInventory();
	}
	
	public void setSpawnIndex(int index) {
		this.spawnIndex = index;
	}
	
	public int getSpawnIndex() {
		return spawnIndex;
	}
	
	public UserState getState() {
		return state;
	}
	
	public Game getGame() {
		return game;
	}
	
	public String getName() {
		return player.getName();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void sendMessage(String message) {
		player.sendMessage(message);
	}

}
