package me.maker56.survivalgames.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SaveDoneEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
    private long time;
    private int size;
    private String arena, lobby, format;
    
	public SaveDoneEvent(String lobby, String arena, long time, int size, String format) {
		this.lobby = lobby;
		this.arena = arena;
		this.time = time;
		this.size = size;
		this.format = format;
	}
	
	public int getFileSize() {
		return size;
	}
	
	public String getFileSizeFormat() {
		return format;
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
