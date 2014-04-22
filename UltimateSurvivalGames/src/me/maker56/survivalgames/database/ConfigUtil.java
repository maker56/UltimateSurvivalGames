package me.maker56.survivalgames.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class ConfigUtil {
	
	// LOCATION
	
	public static Location parseLocation(String s) {
		try {
			String[] split = s.split(",");
			
			World world = Bukkit.getWorld(split[0]);
			
			try {
				double x = Double.parseDouble(split[1]);
				double y = Double.parseDouble(split[2]);
				double z = Double.parseDouble(split[3]);
				
				float yaw = Float.parseFloat(split[4]);
				float pitch = Float.parseFloat(split[5]);
				return new Location(world, x, y, z, yaw, pitch);
				
			} catch(ArrayIndexOutOfBoundsException e) {
				
				int x = Integer.parseInt(split[1]);
				int y = Integer.parseInt(split[2]);
				int z = Integer.parseInt(split[3]);
				
				return new Location(world, x, y, z);
			}
		} catch(Exception e) {
			return null;
		}
	}
	
	public static String serializeLocation(Location l, boolean exact) {
		String key = new String();
		
		key += l.getWorld().getName() + ",";
		
		if(exact) {
			key += l.getX() + "," + l.getY() + "," + l.getZ() + "," + l.getYaw() + "," + l.getPitch();
		} else {
			key += l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
		}
		
		return key;

	}

}
