package me.maker56.survivalgames.user;

import me.maker56.survivalgames.game.Game;

import org.bukkit.entity.Player;

public class SpectatorUser extends UserState {
	
	public SpectatorUser(Player p, Game game) {
		super(p, game);
	}

}
