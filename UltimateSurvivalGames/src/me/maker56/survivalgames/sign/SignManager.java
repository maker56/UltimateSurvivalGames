package me.maker56.survivalgames.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SignManager {
	
	private String[] design = new String[4];
	private String[] leaveDesign = new String[4];
	private HashMap<Location, String> signs = new HashMap<>();
	private HashMap<GameState, String> translations = new HashMap<>();
	private boolean arena, playersleft;
	
	public SignManager() {
		reload();
	}
	
	public void reload() {
		FileConfiguration c = SurvivalGames.signs;
		for(int i = 1; i <= 4; i++) {
			design[i - 1] = ChatColor.translateAlternateColorCodes('&', c.getString("Sign.Line." + i));
		}
		
		arena = c.getBoolean("Sign.LeftClick.Show current arena");
		playersleft = c.getBoolean("Sign.LeftClick.Show players remain");
		
		leaveDesign[0] = ChatColor.translateAlternateColorCodes('&', c.getString("Sign.LeavePrefix"));
		for(int i = 2; i <= 4; i++) {
			leaveDesign[i - 1] = ChatColor.translateAlternateColorCodes('&', c.getString("Sign.Leave.Line." + i));
		}

		for(String key : c.getConfigurationSection("Translations.").getKeys(false)) {
			translations.put(GameState.valueOf(key), ChatColor.translateAlternateColorCodes('&', c.getString("Translations." + key)));
		}
		
		List<String> s = c.getStringList("Sign.List");
		int a = 0;
		for(String key : s) {
			String[] split = key.split(":");
			
			Location loc = Util.parseLocation(split[0]);
			if(loc != null) {
				signs.put(loc, split[1]);
			}
			
			a++;
		}
		System.out.println("[SurvivalGames] " + a + " signs loaded!");
	}
	
	public void addSign(Player p, final Location loc, final String lobby) {
		if(!signs.containsKey(loc)) {
			List<String> signs = SurvivalGames.signs.getStringList("Sign.List");
			signs.add(Util.serializeLocation(loc, false) + ":" + lobby);
			SurvivalGames.signs.set("Sign.List", signs);
			SurvivalGames.saveSigns();
			this.signs.put(loc, lobby);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.instance, new Runnable() {
				public void run() {
					updateSign(loc, lobby);
				}
			}, 1L);
			p.sendMessage(MessageHandler.getMessage("prefix") + "You've created the join sign successfully!");
		}
	}
	
	
	public boolean isSign(Location loc) {
		return signs.containsKey(loc);
	}
	
	public void removeSign(Player p, Location loc) {
		if(signs.containsKey(loc)) {
			String lobby = signs.get(loc);
			this.signs.remove(loc);
			List<String> signs = SurvivalGames.signs.getStringList("Sign.List");
			signs.remove(Util.serializeLocation(loc, false) + ":" + lobby);
			SurvivalGames.signs.set("Sign.List", signs);
			SurvivalGames.saveSigns();
			p.sendMessage(MessageHandler.getMessage("prefix") + "You've removed the join sign successfully!");
		}
	}
	
	public String[] getLeaveSignDesign() {
		return leaveDesign;
	}
	
	public String getLobby(Location loc) {
		if(signs.containsKey(loc))
			return signs.get(loc);
		return null;
	}
	
	public void sendInfo(CommandSender sender, String lobby) {
		Game g = SurvivalGames.gameManager.getGame(lobby);
		if(g != null) {
			sender.sendMessage(MessageHandler.getMessage("game-sign-info").replace("%0%", lobby));
			if(g.getState() == GameState.INGAME || g.getState() == GameState.DEATHMATCH || g.getState() == GameState.COOLDOWN) {
				if(arena)
					sender.sendMessage(MessageHandler.getMessage("game-sign-arena").replace("%0%", g.getCurrentArena().getName()));
				if(playersleft) {
					String s = g.getAlivePlayers();
					sender.sendMessage(MessageHandler.getMessage("game-sign-playersleft").replace("%1%", s).replace("%0%", Integer.valueOf(g.getPlayingUsers()).toString()));
				}
			} else {
				sender.sendMessage(MessageHandler.getMessage("game-sign-noinfo"));
			}
		} else {
			sender.sendMessage(MessageHandler.getMessage("join-unknown-game").replace("%0%", lobby));
		}
	}
	
	public void updateSigns() {
		for(Entry<Location, String> s : signs.entrySet()) {
			Location loc = s.getKey();
			if(loc != null && loc.getWorld() != null) {
				updateSign(loc, s.getValue());
			}
		}
	}
	
	public void updateSign(Location loc, String lobby) {
		Block b = loc.getBlock();
		if(b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
			Sign s = (Sign) b.getState();
			Game g = SurvivalGames.gameManager.getGame(lobby);
			if(g != null) {
				String state = translations.get(g.getState());
				for(int i = 0; i < 4; i++) {
					s.setLine(i, design[i].replace("%name%", g.getName()).replace("%state%", state).replace("%currentplayers%", Integer.valueOf(g.getPlayingUsers()).toString()).replace("%requiredplayers%", Integer.valueOf(g.getRequiredPlayers()).toString()).replace("%maxplayers%", Integer.valueOf(g.getMaximumPlayers()).toString()));
				}
				s.update();
			} else {
				if(SurvivalGames.database.contains("Games." + lobby)) {
					s.setLine(1, "§4Game not");
					s.setLine(2, "§4loaded!");
				} else {
					s.setLine(1, "§4Game not");
					s.setLine(2, "§4found!");
				}
				s.update();
			}
		}
	}
	
}
