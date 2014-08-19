package me.maker56.survivalgames.statistics;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.maker56.survivalgames.commands.messages.MessageHandler;

public class StatisticManager {
	
	// OFFLINE
	private static HashMap<String, StatisticData> stats = new HashMap<>();
	private static int refreshTime = 60;
	
	@SuppressWarnings("deprecation")
	public static void sendOfflineStatistics(CommandSender sender, String name) {
		OfflinePlayer op = Bukkit.getOfflinePlayer(name);
		
		if(op == null) {
			sender.sendMessage(MessageHandler.getMessage("stats-player-not-found").replace("%0%", name));
			return;
		}
		
		StatisticData sd = null;
		
		if(stats.containsKey(name)) {
			sd = stats.get(name);
			if((System.currentTimeMillis() - sd.creation) / 1000 < refreshTime) {
				sd = null;
			}
			
		}
		
		if(sd == null) {
			sd = new StatisticData(name, op.getUniqueId().toString());
		}
		String s = null;
		s.isEmpty();
		
		// TODO
		
	}

}
