package me.maker56.survivalgames.commands.arguments;

import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.statistics.StatisticManager;

import org.bukkit.command.CommandSender;

public class StatsArgument {
	
	private CommandSender sender;
	private String[] args;
	
	public StatsArgument(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public boolean execute() {
		if(!PermissionHandler.hasPermission(sender, Permission.JOIN)) {
			sender.sendMessage(MessageHandler.getMessage("no-permission"));
			return true;
		}
		
		String name = sender.getName();
		
		if(args.length > 1) {
			name = args[1];
		}
		
		StatisticManager.sendStatistics(sender, name);
		return true;
	}


}
