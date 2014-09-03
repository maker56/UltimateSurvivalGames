package me.maker56.survivalgames.statistics;

import java.util.HashMap;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.user.UserState;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatisticManager {
	
	// OFFLINE
	protected static HashMap<String, StatisticData> stats = new HashMap<>();
	private static int refreshTime = 60;
	
	public static void sendStatistics(CommandSender sender, String name) {
		if(name.length() < 4 || name.length() > 16 || !name.matches("[a-zA-Z0-9_]*")) {
			sender.sendMessage(MessageHandler.getMessage("stats-player-not-found").replace("%0%", name));
			return;
		}
		
		String realname = name;
		name = name.toLowerCase();
		
		Player pl = Bukkit.getPlayer(realname);
		
		// playing players
		if(pl != null) {
			UserState state = SurvivalGames.userManger.getUser(realname);
			
			if(state == null)
				state = SurvivalGames.userManger.getSpectator(realname);
			
			if(state != null) {
				if(!state.areStatisticsLoaded()) {
					sender.sendMessage(MessageHandler.getMessage("stats-player-not-loaded").replace("%0%", realname));
					return;
				}
				
				StatisticData sd = state.getStatistics();
				sendStatistics(sender, sd);
				return;
			}
		}
		
		if(stats.containsKey(name)) {
			StatisticData sd = stats.get(name);
			if((System.currentTimeMillis() - sd.creation) / 1000 >= refreshTime) {
				StatisticLoader.load(sender, name);
			}
			sendStatistics(sender, sd);
		} else {
			StatisticLoader.load(sender, name);
		}
		
		
	}
	
	public static void setStatistics(CommandSender p, StatisticData sd) {
		String name = sd.name.toLowerCase();
		if(stats.containsKey(name))
			stats.remove(name);
		stats.put(name, sd);
		sendStatistics(p, sd);
	}
	
	public static void sendStatistics(CommandSender sender, StatisticData sd) {
		sender.sendMessage(MessageHandler.getMessage("stats-header").replace("%0%", sd.name));
		sender.sendMessage(MessageHandler.getMessage("stats-kills").replace("%0%", Integer.valueOf(sd.getKills()).toString()));
		sender.sendMessage(MessageHandler.getMessage("stats-deaths").replace("%0%", Integer.valueOf(sd.getDeaths()).toString()));
		sender.sendMessage(MessageHandler.getMessage("stats-kdr").replace("%0%", Float.valueOf((float) (Math.round( sd.getKDR() * 100. ) / 100.)).toString().replace(".", ",")));
		sender.sendMessage(MessageHandler.getMessage("stats-wins").replace("%0%", Integer.valueOf(sd.getWins()).toString()));
		sender.sendMessage(MessageHandler.getMessage("stats-played").replace("%0%", Integer.valueOf(sd.getPlayed()).toString()));
		sender.sendMessage(MessageHandler.getMessage("stats-points").replace("%0%", Integer.valueOf(sd.getPoints()).toString()));
		sender.sendMessage(MessageHandler.getMessage("stats-footer").replace("%0%", sd.name));
	}

}
