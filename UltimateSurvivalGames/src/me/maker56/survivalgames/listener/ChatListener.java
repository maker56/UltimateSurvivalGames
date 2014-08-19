package me.maker56.survivalgames.listener;

import java.util.Iterator;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.chat.JSONMessage;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.user.UserManager;
import me.maker56.survivalgames.user.UserState;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatListener implements Listener {

	private UserManager um = SurvivalGames.getUserManager();
	private static boolean chat, pex;
	private static String design, specPrefix;
	
	public ChatListener() {
		reinitializeConfig();
	}
	
	public static void reinitializeConfig() {
		FileConfiguration config = SurvivalGames.instance.getConfig();
		chat = config.getBoolean("Chat.Enabled");
		design = ChatColor.translateAlternateColorCodes('&', config.getString("Chat.Design"));
		specPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("Chat.Spectator-State"));
		pex = Bukkit.getPluginManager().isPluginEnabled("PermissionsEx");
	}

	
	// SEPARATED CHAT
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(chat && !event.isCancelled()) {
			UserState u = um.getUser(event.getPlayer());
			
			if(u == null)
				u = um.getSpectator(event.getPlayer());
			
			if(u != null) {
				String format = design;
				String[] formats = getFormats(u.getPlayer());
				format = format.replace("{STATE}", u.isSpectator() ? specPrefix : "");
				format = format.replace("{PREFIX}", formats[0]);
				format = format.replace("{PLAYERNAME}", u.getName());
				format = format.replace("{SUFFIX}", formats[1]);
				format = format.replace("{MESSAGE}", event.getMessage());
				
				System.out.println(ChatColor.stripColor(format));
				JSONMessage msg = new JSONMessage(format)
				.tooltip("Click to show " + u.getName() + (u.getName().toLowerCase().endsWith("s") ? "" : "'s") + " statistics")
				.command("/sg stats " + u.getName());
				
				event.setCancelled(true);
				Game g = u.getGame();
				
				if(u.isSpectator()) {
					g.sendSpectators(msg);
				} else {
					g.sendMessage(msg);
				}
			} else {
				for(Iterator<Player> i = event.getRecipients().iterator(); i.hasNext();) {
					Player p = i.next();
					if(um.isPlaying(p.getName()) || um.isSpectator(p.getName()))
						i.remove();
				}
			}
			
		}

	}
	
	public String[] getFormats(Player p) {
		if(pex) {
			PermissionUser pu = PermissionsEx.getPermissionManager().getUser(p);
			return new String[] { ChatColor.translateAlternateColorCodes('&', pu.getPrefix()), ChatColor.translateAlternateColorCodes('&', pu.getSuffix()) };
		}
		
		if(p.isOp())
			return new String[] { "§c", "§7> §r" };	
		return new String[] { "§a", "§7> §r" };	
	}

}
