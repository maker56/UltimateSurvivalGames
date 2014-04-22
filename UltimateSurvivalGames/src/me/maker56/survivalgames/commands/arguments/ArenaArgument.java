package me.maker56.survivalgames.commands.arguments;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class ArenaArgument {
	
	private CommandSender sender;
	private String[] args;
	
	public ArenaArgument(CommandSender sender, String[] args) {
		this.sender = sender;
		this.args = args;
	}
	
	@SuppressWarnings("deprecation")
	public boolean execute() {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cThe arena argument can only execute as a Player!");
			return true;
		}
		
		Player p = (Player)sender;
		
		if(!PermissionHandler.hasPermission(p, Permission.ARENA)) {
			p.sendMessage(MessageHandler.getMessage("no-permission"));
			return true;
		}
		
		if(args.length == 1) {
			p.sendMessage(MessageHandler.getMessage("prefix") + "Arena-Setup §7§m---§r §6Helpsite");
			p.sendMessage("§8/§6sg arena create <LOBBYNAME> <ARENA NAME> §7- §eCreates an arena in a specify game!");
			p.sendMessage("§8/§6sg arena select <LOBBYNAME> <ARENA NAME> §7- §eSelects the specify arena!");
			p.sendMessage("§8/§6sg arena tools §7- §eGives you the Arena-Selection Tools!");
			p.sendMessage("§8/§6sg arena check §7- §eShows whats even need to be done on the selected arena!");
			p.sendMessage("§8/§6sg arena addspawn §7- §eAdd a Spawn on the selected arena!");
			p.sendMessage("§8/§6sg arena removespawn <SPAWNID> §7- §eRemoves a spawn from the selected arena!");
			p.sendMessage("§8/§6sg arena deathmatch §7- §eDe/activate the Deathmatch on the spelected arena!");
			p.sendMessage("§8/§6sg arena deathmatch add §7- §eAdd a Deathmatch-Spawn on the selected arena!");
			p.sendMessage("§8/§6sg arena deathmatch remove <SPAWNID> §7- §eRemove an Spawn on the selected arena!");
			p.sendMessage("§8/§6sg arena finish §7- §eFinished the create-setup on the selected arena!");
			p.sendMessage("§8/§6sg arena save §7- §eSaves the blocks of an arena to file for map reset!");
			p.sendMessage("§8/§6sg arena delete §7- §eRemoves an arena in a lobby!");
			return true;
		} else {
			if(args[1].equalsIgnoreCase("delete")) {
				SurvivalGames.arenaManager.delete(p);
				return true;
			} else if(args[1].equalsIgnoreCase("save")) {
				SurvivalGames.arenaManager.save(p);
				return true;
			} else if(args[1].equalsIgnoreCase("create")) {
				
				if(args.length < 4) {
					p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a arenaname and a gamename: /sg arena create <LOBBYNAME> <ARENA NAME>"));
					return true;
				}
				
				String arenaname = getArgs(3);
				String gamename = args[2];
				
				SurvivalGames.arenaManager.createArena(p, arenaname, gamename);
				return true;
				
			} else if(args[1].equalsIgnoreCase("select")) {
				
				if(args.length < 4) {
					p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a arenaname and a gamename: /sg arena select <LOBBYNAME> <ARENA NAME>"));
					return true;
				}
				
				String arenaname = getArgs(3);
				String gamename = args[2];
				
				SurvivalGames.arenaManager.selectArena(p, arenaname, gamename);
				return true;
				
			} else if(args[1].equalsIgnoreCase("check")) {
				SurvivalGames.arenaManager.check(p);
				return true;
				
			} else if(args[1].equalsIgnoreCase("addspawn")) {
				SurvivalGames.arenaManager.addSpawn(p, "Spawns");
				return true;
				
			} else if(args[1].equalsIgnoreCase("removespawn")) {
				if(args.length == 2) {
					p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a number: /sg arena removespawn <ID>"));
					return true;
				}
				
				int id = 0;
				
				try {
					id = Integer.parseInt(args[2]);
				} catch(NumberFormatException e) {
					p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", args[2] + " isn't a valid number!"));
					return true;
				}
				
				SurvivalGames.arenaManager.removeSpawn(p, id, "Spawns");
				return true;
				
			} else if(args[1].equalsIgnoreCase("deathmatch")) {
				
				if(args.length == 2) {
					SurvivalGames.arenaManager.changeDeathmatch(p);
					return true;
				}
				
				if(args[2].equalsIgnoreCase("add")) {
					SurvivalGames.arenaManager.addSpawn(p, "Deathmatch-Spawns");
					return true;
					
				} else if(args[2].equalsIgnoreCase("remove")) {
					
					if(args.length == 3) {
						p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", "You must specify a number: /sg arena deathmatch remove <ID>"));
						return true;
					}
					
					int id = 0;
					
					try {
						id = Integer.parseInt(args[3]);
					} catch(NumberFormatException e) {
						p.sendMessage(MessageHandler.getMessage("cmd-error").replace("%0%", args[3] + " isn't a valid number!"));
						return true;
					}
					
					SurvivalGames.arenaManager.removeSpawn(p, id, "Deathmatch-Spawns");
					return true;
				}
				
				return true;
				
			} else if(args[1].equalsIgnoreCase("finish")) {
				SurvivalGames.arenaManager.finishSetup(p);
				return true;
				
			} else if(args[1].equalsIgnoreCase("tools")) {
				
				WorldEditPlugin we = SurvivalGames.getWorldEdit();
				if(we == null) {
					ItemStack is = new ItemStack(Material.CARROT_STICK);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName("SurvivalGames Selection Tool");
					is.setItemMeta(im);
					p.getInventory().addItem(is);
					p.sendMessage(MessageHandler.getMessage("arena-tools"));
				} else {
					p.getInventory().addItem(new ItemStack(we.getConfig().getInt("wand-item")));
					p.sendMessage(MessageHandler.getMessage("arena-tools-worldedit"));
				}
				return true;
				
			}
			
		}
		
		p.sendMessage(MessageHandler.getMessage("prefix") + "§cCommand not found! Type /sg arena for help!");
		return true;
		
	}
	
	private String getArgs(int i) {
		String s = "";
		for(int a = i; i < args.length; a++) {
			try {
				s += args[a] + " ";
			} catch(ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		return s.substring(0, s.length() - 1);
	}

}
