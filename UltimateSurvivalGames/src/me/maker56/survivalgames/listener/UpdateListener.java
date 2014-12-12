package me.maker56.survivalgames.listener;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {
	
	private static String version = null, updateInfo = null;;
	
	public static void update(String version) {
		UpdateListener.version = version;
		System.out.println("[SurvivalGames] A newer version of survivalgames is available. (" + version + ") You can download it here: http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ You're using " + SurvivalGames.version);
		updateInfo = MessageHandler.getMessage("prefix") + "§eA newer version of SurvivalGames is available. §7(§b" + version + "§7) §eYou can download it here: §bhttp://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ §7You're using §o" + SurvivalGames.version;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(PermissionHandler.hasPermission(p, Permission.LOBBY) || PermissionHandler.hasPermission(p, Permission.ARENA)) {
				p.sendMessage(updateInfo);
			}
		}
	
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(PermissionHandler.hasPermission(p, Permission.LOBBY) || PermissionHandler.hasPermission(p, Permission.ARENA)) {
			if(version != null)
				p.sendMessage(updateInfo);
			if(outdated != null) {
				p.sendMessage("");
				p.sendMessage(outdated);
			}
		}
	}
	
	// TEMPORARY UPDATE STUFF
	private static String outdated = null;
	public static void setOutdatedMaps(String s) {
		outdated = s;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(PermissionHandler.hasPermission(p, Permission.LOBBY) || PermissionHandler.hasPermission(p, Permission.ARENA)) {
				p.sendMessage(s);
			}
		}
	}

}
