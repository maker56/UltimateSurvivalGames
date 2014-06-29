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
	
    private long millis, size;
    private int seconds;
    private String arena, lobby, format;
    
	public SaveDoneEvent(String lobby, String arena, long time, long size, String format) {
		this.lobby = lobby;
		this.arena = arena;
		this.size = size;
		this.format = format;
		
		int seconds = (int) (time / 1000);
		time -= seconds * 1000;
		this.seconds = seconds;
		this.millis = time;
	}
	
	public long getFileSize() {
		return size;
	}
	
	public String getFileSizeFormat() {
		return format;
	}
	
	public String getTime() {
		return seconds + " second and " + millis + " milliseconds";
	}
	
	public String getLobby() {
		return lobby;
	}
	
	public String getArena() {
		return arena;
	}

}
