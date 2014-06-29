package me.maker56.survivalgames.events;

import me.maker56.survivalgames.game.Game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserLobbyLeftEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private Player p;
    private Game game;
    
    public UserLobbyLeftEvent(Player p, Game game) {
    	this.p = p;
    	this.game = game;
    }
    
    public Player getPlayer() {
    	return p;
    }
    
    public Game getGame() {
    	return game;
    }

}
