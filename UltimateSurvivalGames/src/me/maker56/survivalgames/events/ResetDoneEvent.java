package me.maker56.survivalgames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ResetDoneEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
    private long time;
    private String arena, lobby;
    
	public ResetDoneEvent(String lobby, String arena, long time) {
		this.lobby = lobby;
		this.arena = arena;
		this.time = time;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getLobby() {
		return lobby;
	}
	
	public String getArena() {
		return arena;
	}

}
