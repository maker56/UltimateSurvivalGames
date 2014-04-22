package me.maker56.survivalgames.commands.permission;

import me.maker56.survivalgames.SurvivalGames;

import org.bukkit.command.CommandSender;

public class PermissionHandler {
	
	private static boolean usePermission = SurvivalGames.instance.getConfig().getBoolean("use-permissions");
	
	public static void reinitializeUsePermission() {
		usePermission = SurvivalGames.instance.getConfig().getBoolean("use-permissions");
	}
	
	public static boolean hasPermission(CommandSender sender, Permission permission) {
		if(usePermission) {
			if(sender.hasPermission(permission.getPermission())) {
				return true;
			} else {
				return false;
			}
		} else {
			if(sender.isOp()) {
				return true;
			} else {
				if(permission == Permission.JOIN || permission == Permission.LIST) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

}
