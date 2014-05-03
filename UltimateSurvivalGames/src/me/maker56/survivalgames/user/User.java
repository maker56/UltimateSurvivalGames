package me.maker56.survivalgames.user;

import me.maker56.survivalgames.game.Game;

import org.bukkit.entity.Player;

public class User extends UserState {
	
	private Game game;
	
	private int spawnIndex = Integer.MIN_VALUE;
	
	public User(Player player, Game game) {
		super(player);
		this.player = player;
		this.game = game;
	}

	public void setSpawnIndex(int index) {
		this.spawnIndex = index;
	}
	
	public int getSpawnIndex() {
		return spawnIndex;
	}
	
	public Game getGame() {
		return game;
	}

}
