package me.maker56.survivalgames.events;

import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.user.User;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UserLobbyJoinedEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private User user;
    private Game game;
    
    public UserLobbyJoinedEvent(User user, Game game) {
    	this.user = user;
    	this.game = game;
    }
    
    public User getUser() {
    	return user;
    }
    
    public Game getGame() {
    	return game;
    }

}
