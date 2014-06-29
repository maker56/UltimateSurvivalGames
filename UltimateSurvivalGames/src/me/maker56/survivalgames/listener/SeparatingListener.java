package me.maker56.survivalgames.listener;

import java.util.Iterator;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SeparatingListener {

	private UserManager um = SurvivalGames.getUserManager();
	private static boolean chat;
	
	public SeparatingListener() {
		reinitializeConfig();
	}
	
	public static void reinitializeConfig() {
		FileConfiguration config = SurvivalGames.instance.getConfig();
		chat = config.getBoolean("Separating.Chat.Enabled");
	}

	
	// SEPARATED CHAT
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(chat && !event.isCancelled()) {
			User u = um.getUser(event.getPlayer());
			
			if(u == null) {
				for(Iterator<Player> i = event.getRecipients().iterator(); i.hasNext();) {
					Player p = i.next();
					if(!PermissionHandler.hasPermission(p, Permission.READCHAT)) {
						if(um.isPlaying(p.getName()))
							i.remove();
					}
				}
			} else {
				Game g = u.getGame();
				for(Iterator<Player> i = event.getRecipients().iterator(); i.hasNext();) {
					Player p = i.next();
					if(!PermissionHandler.hasPermission(p, Permission.READCHAT)) {
						User user = um.getUser(event.getPlayer());
						if(user != null) {
							if(user.getGame().equals(g))
								i.remove();
						} else {
							i.remove();
						}
					}
				}
			}
		}

	}

}
