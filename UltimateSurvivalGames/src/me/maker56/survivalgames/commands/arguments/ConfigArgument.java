package me.maker56.survivalgames.commands.arguments;

import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.database.ConfigReloader;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigArgument {
	
	private CommandSender sender;
	private String[] args;
	
	public ConfigArgument(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	public boolean execute() {
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cThe lobby argument can only execute as a Player!");
			return true;
		}
		
		Player p = (Player)sender;
		
		if(!PermissionHandler.hasPermission(p, Permission.CONFIG)) {
			p.sendMessage(MessageHandler.getMessage("no-permission"));
			return true;
		}
		
		if(args.length == 1) {
			p.sendMessage(MessageHandler.getMessage("prefix") + "Configuration §7§m---§r §6Helpsite");
			p.sendMessage("§8/§6sg config reload [MESSAGES/SIGNS/DATABASE/CONFIG/CHESTLOOT/SCOREBOARD] §7- §eReloads the specify config!");
		} else {
			if(args[1].equalsIgnoreCase("reload")) {
				if(args.length == 2) {
					ConfigReloader.reloadMessage();
					ConfigReloader.reloadConfig();
					ConfigReloader.reloadDatabase();
					ConfigReloader.reloadSigns();
					ConfigReloader.reloadChestloot();
					ConfigReloader.reloadScoreboard();
					p.sendMessage(MessageHandler.getMessage("prefix") + "You've reloaded all configuration files successfully!");
					return true;
				}
				String con = args[2];
				if(con.equalsIgnoreCase("messages")) {
					ConfigReloader.reloadMessage();
					p.sendMessage(MessageHandler.getMessage("prefix") + "You've reloaded the messages.yml successfully!");
			
				} else if(con.equalsIgnoreCase("signs")) {
					ConfigReloader.reloadSigns();
					p.sendMessage(MessageHandler.getMessage("prefix") + "You've reloaded the signs.yml successfully!");
					
				} else if(con.equalsIgnoreCase("database")) {
					ConfigReloader.reloadDatabase();
					p.sendMessage(MessageHandler.getMessage("prefix") + "The settings are applied to a lobby after a lobby-reload or the end of a survival game.");
				} else if(con.equalsIgnoreCase("config")) {
					ConfigReloader.reloadConfig();
					p.sendMessage(MessageHandler.getMessage("prefix") + "You've reloaded the config.yml successfully!");
				} else if(con.equalsIgnoreCase("chestloot")) {
					ConfigReloader.reloadChestloot();
					p.sendMessage(MessageHandler.getMessage("prefix") + "You've reloaded the chestloot.yml successfully!");
				} else if(con.equalsIgnoreCase("scoreboard")) {
					ConfigReloader.reloadScoreboard();
					p.sendMessage(MessageHandler.getMessage("prefix") + "You've reloaded the scoreboard.yml successfully!");
				} else {
					p.sendMessage(MessageHandler.getMessage("config-error-name").replace("%0%", "/sg config reload [MESSAGES/SIGNS/DATABASE/CONFIG/CHESTLOOT]"));
					return true;
				}
				
			}
		}
		return true;
	}

}
