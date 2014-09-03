package me.maker56.survivalgames.commands.permission;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.user.User;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PermissionHandler {
	
	private static boolean usePermission = SurvivalGames.instance.getConfig().getBoolean("use-permissions");
	
	public static void reinitializeUsePermission() {
		usePermission = SurvivalGames.instance.getConfig().getBoolean("use-permissions");
	}
	
	public static boolean hasPermission(CommandSender sender, Permission permission) {
		if(usePermission) {
			return sender.hasPermission(permission.getPermission());
		} else {
			if(sender.isOp()) {
				return true;
			} else {
				return permission == Permission.JOIN || permission == Permission.LIST || permission == Permission.SPECTATE || permission == Permission.STATS;
			}
		}
	}
	
	// DONATOR PERMISSONS
	
	private static List<String> joinPower;
	private static HashMap<String, Integer> votePower = new HashMap<>();
	
	public static void reinitializeDatabase() {
		FileConfiguration c = SurvivalGames.instance.getConfig();
		
		joinPower = c.getStringList("Donator-Permissions.Join-Full-Arena");
		
		votePower.clear();
		for(String key : c.getStringList("Donator-Permissions.Extra-Vote-Power")) {
			try {
				String[] split = key.split("//");
				votePower.put(split[0], Integer.parseInt(split[1]));
			} catch(NumberFormatException | ArrayIndexOutOfBoundsException e) { }
		}
	}
	
	public static int getVotePower(Player p) {
		int l = 1;
		
		for(Entry<String, Integer> vote : votePower.entrySet()) {
			if(p.hasPermission(vote.getKey())) {
				l = vote.getValue();
			}
		}
		
		return l;
	}
	
	public static User canJoin(Player p, Game game) {
		int pLevel = 0;
		int ii = 0;
		for(String key : joinPower) {
			ii++;
			if(p.hasPermission(key)) {
				pLevel = ii;
			}
		}
		
		if(pLevel == 0)
			return null;
		
		Collections.shuffle(game.getUsers());
		
		User lU = null;
		int l = pLevel;
		
		for(User u : game.getUsers()) {
			int level = 0;
			int i = 0;
			for(String key : joinPower) {
				i++;
				if(u.getPlayer().hasPermission(key)) {
					level = i;
				}
			}
			if(level < l) {
				l = level;
				lU = u;
			}
		}
		
		return lU;
	}

}
