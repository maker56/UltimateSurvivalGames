package me.maker56.survivalgames.commands;

import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.arguments.ArenaArgument;
import me.maker56.survivalgames.commands.arguments.ConfigArgument;
import me.maker56.survivalgames.commands.arguments.LobbyArgument;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.user.User;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSG implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("sg")) {
			
			
			if(args.length == 0) {
				sender.sendMessage(MessageHandler.getMessage("prefix") + "Version " + SurvivalGames.instance.getDescription().getVersion() + " §7§m--§r §ePlugin developed by maker56");
				
				if(PermissionHandler.hasPermission(sender, Permission.JOIN)) {
					sender.sendMessage("§8/§6sg join <LOBBY> §7- §eJoin a game!");
					sender.sendMessage("§8/§6sg leave §7- §eLeave a game!");
					sender.sendMessage("§8/§6sg vote <ID> §7- §eVote for an arena!");
				}
				
				if(PermissionHandler.hasPermission(sender, Permission.LIST)) {
					sender.sendMessage("§8/§6sg list §7- §eList of all available lobbys!");
				}
				
				if(PermissionHandler.hasPermission(sender, Permission.GAME)) {
					sender.sendMessage("§8/§6sg lobby §7- §eShows the lobby helpsite!");
				}
				
				if(PermissionHandler.hasPermission(sender, Permission.ARENA)) {
					sender.sendMessage("§8/§6sg arena §7- §eShows the arena helpsite!");
				}
				
				if(PermissionHandler.hasPermission(sender, Permission.CONFIG)) {
					sender.sendMessage("§8/§6sg config §7- §eShows the configuration management helpsite!");
				}
			} else {
				
				if(args[0].equalsIgnoreCase("arena")) {
					return new ArenaArgument(sender, args).execute();
					
				} else if(args[0].equalsIgnoreCase("lobby") || args[0].equalsIgnoreCase("game")) {
					return new LobbyArgument(sender, args).execute();
					
				} else if(args[0].equalsIgnoreCase("config")) {
					return new ConfigArgument(sender, args).execute();
				}
				
				if(args[0].equalsIgnoreCase("join")) {
					
					Player p = (Player)sender;
					
					if(args.length == 1) {
						p.sendMessage(MessageHandler.getMessage("game-must-enter").replace("%0%", "/sg join <GAMENAME>"));
						return true;
					}
					
					SurvivalGames.userManger.joinGame(p, args[1]);
					return true;
				} else if(args[0].equalsIgnoreCase("leave")) {
					Player p = (Player)sender;
					User user = SurvivalGames.userManger.getUser(p.getName());
					
					if(user == null) {
						p.sendMessage(MessageHandler.getMessage("leave-not-playing"));
						return true;
					}
					
					Game game = user.getGame();
					
					if(game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
						SurvivalGames.userManger.leaveGame(p);
						return true;
					} else {
						IngamePhrase ip = game.getIngamePhrase();
						ip.killUser(user, null, true);
					}
					
					return true;
				} else if(args[0].equalsIgnoreCase("vote")) {
					Player p = (Player)sender;
					
					if(!SurvivalGames.userManger.isPlaying(p.getName())) {
						p.sendMessage(MessageHandler.getMessage("leave-not-playing"));
						return true;
					}
					
					if(args.length == 1) {
						p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a Arena-ID!"));
						return true;
					}
					
					User user = SurvivalGames.userManger.getUser(p.getName());
					
					if(!user.getGame().isVotingEnabled()) {
						p.sendMessage(MessageHandler.getMessage("game-no-voting-enabled"));
						return true;
					}
					
					if(user.getGame().getState() != GameState.VOTING) {
						p.sendMessage(MessageHandler.getMessage("game-no-vote"));
						return true;
					}
					
					VotingPhrase vp = user.getGame().getVotingPhrase();
					
					if(!vp.canVote(p.getName())) {
						p.sendMessage(MessageHandler.getMessage("game-already-vote"));
						return true;
					}
					
					
					int mapid = 0;
					
					try {
						mapid = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", args[1] + " ist not a valid number!"));
						return true;
					}
					

					Arena arena = vp.vote(p.getName(), mapid);
					
					if(arena == null) {
						p.sendMessage(MessageHandler.getMessage("game-bad-vote"));
						return true;
					}
					
					arena.setVotes(arena.getVotes() + 1);
					p.sendMessage(MessageHandler.getMessage("game-success-vote").replace("%0%", arena.getName()));
					return true;
				} else if(args[0].equalsIgnoreCase("list")) {
					if(!PermissionHandler.hasPermission(sender, Permission.LIST)) {
						sender.sendMessage(MessageHandler.getMessage("no-permission"));
						return true;
					}
					
					List<Game> games = SurvivalGames.gameManager.getGames();
					sender.sendMessage(MessageHandler.getMessage("prefix") + "List of all loaded lobbys§8: §7(§b" + games.size() + "§7)");
					for(Game game : games) {
						sender.sendMessage("§7- §6" + game.getName() + "§8: §e" + game.getState().toString() + " §7(§e" + game.getPlayingUsers() + "§7/§e" + game.getMaximumPlayers() + "§7)");
					}
					return true;
				}
				
				
				sender.sendMessage(MessageHandler.getMessage("prefix") + "§cCommand not found! Type /sg for help!");
				return true;
			}
			
			
		}
		
		return false;
	}
	
	
	
	

}
