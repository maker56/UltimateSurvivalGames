package me.maker56.survivalgames.user;

import me.maker56.survivalgames.arena.chest.Chest;
import me.maker56.survivalgames.game.Game;

import org.bukkit.entity.Player;

public class User extends UserState {
	
	private int spawnIndex = Integer.MIN_VALUE;
	private Chest currentChest;
	
	public User(Player player, Game game) {
		super(player, game);
		this.player = player;
	}

	public void setSpawnIndex(int index) {
		this.spawnIndex = index;
	}
	
	public int getSpawnIndex() {
		return spawnIndex;
	}
	
	public Chest getCurrentChest() {
		return currentChest;
	}
	
	public void setCurrentChest(Chest chest) {
		this.currentChest = chest;
	}
	
	public boolean isInChest() {
		return currentChest != null;
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

}
