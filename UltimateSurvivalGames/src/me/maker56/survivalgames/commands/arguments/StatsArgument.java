package me.maker56.survivalgames.commands.arguments;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.statistics.StatisticData;
import me.maker56.survivalgames.statistics.StatisticManager;
import me.maker56.survivalgames.user.UserState;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsArgument {
	
	private CommandSender sender;
	private String[] args;
	
	public StatsArgument(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public boolean execute() {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cThe config argument can only execute as a Player!");
			return true;
		}
		
		Player p = (Player)sender;
		String name = p.getName();
		
		if(args.length > 1) {
			name = args[1];
		}
		
		showStatistics(name);
		return true;
	}
	
	void showStatistics(String name) {
		UserState state = SurvivalGames.userManger.getUser(name);
		
		if(state == null)
			state = SurvivalGames.userManger.getSpectator(name);
		
		if(state == null) {
			StatisticManager.sendOfflineStatistics(sender, name);
			return;
		}
		StatisticData sd = state.getStatistics();
		
		if(sd.hasLoadedStatistics()) {
			sender.sendMessage(MessageHandler.getMessage("stats-header").replace("%0%", name));
			sender.sendMessage(MessageHandler.getMessage("stats-kills").replace("%0%", Integer.valueOf(sd.getKills()).toString()));
			sender.sendMessage(MessageHandler.getMessage("stats-deaths").replace("%0%", Integer.valueOf(sd.getDeaths()).toString()));
			sender.sendMessage(MessageHandler.getMessage("stats-kdr").replace("%0%", Float.valueOf((float) (Math.round( sd.getKDR() * 100. ) / 100.)).toString().replace(".", ",")));
			sender.sendMessage(MessageHandler.getMessage("stats-wins").replace("%0%", Integer.valueOf(sd.getWins()).toString()));
			sender.sendMessage(MessageHandler.getMessage("stats-played").replace("%0%", Integer.valueOf(sd.getPlayed()).toString()));
			sender.sendMessage(MessageHandler.getMessage("stats-points").replace("%0%", Integer.valueOf(sd.getPoints()).toString()));
			sender.sendMessage(MessageHandler.getMessage("stats-footer").replace("%0%", name));
		} else {
			sender.sendMessage(MessageHandler.getMessage("stats-player-not-loaded").replace("%0%", name));
			return;
		}
	}
	


}
