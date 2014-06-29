package me.maker56.survivalgames.reset;

import java.util.ArrayList;
import java.util.List;

import me.maker56.survivalgames.Util;

import org.bukkit.Location;
import org.bukkit.World;

public class Selection {
	
	private Location min, max;
	public int xMin, yMin, zMin, xMax, yMax, zMax;
	
	public Selection(Location min, Location max) {
		setMinimumLocation(min);
		setMaximumLocation(max);
	}
	
	private void redefineLocations() {
		if(min != null && max != null) {
			xMin = Math.min(min.getBlockX(), max.getBlockX());
			yMin = Math.min(min.getBlockY(), max.getBlockY());
			zMin = Math.min(min.getBlockZ(), max.getBlockZ());
			
			xMax = Math.max(min.getBlockX(), max.getBlockX());
			yMax = Math.max(min.getBlockY(), max.getBlockY());
			zMax = Math.max(min.getBlockZ(), max.getBlockZ());
		}
	}
	
	public void setMinimumLocation(Location min) {
		Util.debug("set selection min location: " + Util.serializeLocation(min, false));
		this.min = min;
		redefineLocations();
	}
	
	public void setMaximumLocation(Location max) {
		Util.debug("set selection max location: " + Util.serializeLocation(max, false));
		this.max = max;
		redefineLocations();
	}
	
	public Location getMinimumLocation() {
		return min;
	}
	
	public Location getMaximumLocation() {
		return max;
	}
	
	public int getLength() {
		return xMax - xMin + 1;
	}
	
	public int getWidth() {
		return zMax - zMin + 1;
	}
	
	public int getHeight() {
		return yMax - yMin + 1;
	}
	
	@Deprecated
	public List<Location> getSelectedLocations() {
		ArrayList<Location> al = new ArrayList<>();
		World world = min.getWorld();

		for(int x = xMin; x <= xMax; x++) {
			for(int y = yMin; y <= yMax; y++) {
				for(int z = zMin; z <= zMax; z++) {
					al.add(new Location(world, x, y, z));
				}
			}
		}
		return al;
	}
	
	public boolean contains(Location loc) {
		return(loc.getBlockX() >= xMin && loc.getBlockX() <= xMax && loc.getBlockY() >= yMin && loc.getBlockY() <= yMax && loc.getBlockZ() >= zMin && loc.getBlockZ() <= zMax);
	}
	
	public int getSize() {
		return getLength() * getWidth() * getHeight();
	}
	
	public boolean isFullDefined() {
		return min != null && max != null;
	}

}
