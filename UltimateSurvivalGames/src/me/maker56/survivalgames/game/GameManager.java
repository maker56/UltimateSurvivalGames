package me.maker56.survivalgames.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.phases.CooldownPhase;
import me.maker56.survivalgames.game.phases.DeathmatchPhase;
import me.maker56.survivalgames.game.phases.IngamePhase;
import me.maker56.survivalgames.game.phases.VotingPhase;
import me.maker56.survivalgames.reset.Reset;

public class GameManager {
	
	private List<Game> games = new ArrayList<>();
	private static FileConfiguration cfg;
	
	public GameManager() {
		reinitializeDatabase();
		loadAll();
	}
	
	public static void reinitializeDatabase() {
		cfg = SurvivalGames.database;
	}
	
	public void createGame(Player p, String lobbyname) {
		String path = "Games." + lobbyname;
		
		if(cfg.contains(path)) {
			p.sendMessage(MessageHandler.getMessage("game-already-exists").replace("%0%", lobbyname));
			return;
		}
		
		path += ".";
		FileConfiguration config = SurvivalGames.instance.getConfig();
		
		boolean enableVoting = config.getBoolean("Default.Enable-Voting");
		int lobbytime = config.getInt("Default.Lobby-Time");
		int maxVotingArenas = config.getInt("Default.Max-Voting-Arenas");
		int reqPlayers = config.getInt("Default.Required-Players-to-start");
		
		cfg.set(path + "Enable-Voting", enableVoting);
		cfg.set(path + "Lobby-Time", lobbytime);
		cfg.set(path + "Max-Voting-Arenas", maxVotingArenas);
		cfg.set(path + "Required-Players-to-start", reqPlayers);
		cfg.set(path + "Lobby", Util.serializeLocation(p.getLocation(), true));
		SurvivalGames.saveDataBase();
		
		p.sendMessage(MessageHandler.getMessage("game-created").replace("%0%", lobbyname));
		p.sendMessage(MessageHandler.getMessage("game-set-spawn").replace("%0%", lobbyname));
		return;
	}
	
	public void setSpawn(Player p, String lobbyname) {
		if(!cfg.contains("Games." + lobbyname)) {
			p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", lobbyname));
			return;
		}
		
		Location loc = p.getLocation();
		
		String s = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
		cfg.set("Games." + lobbyname + ".Lobby", s);
		SurvivalGames.saveDataBase();
		p.sendMessage(MessageHandler.getMessage("game-spawn-set").replace("%0%", lobbyname));
		return;
	}
	
	public void loadAll() { 
		int loaded = 0;
		
		if(cfg.contains("Games")) {
			for(String key : cfg.getConfigurationSection("Games.").getKeys(false)) {
				if(load(key))
					loaded++;
			}
		}
		
		System.out.println("[SurvivalGames] " + loaded + " lobbys loaded!");
	}
	
	public void unload(Game game) {
		if(game != null) {
			if(game.getPlayingUsers() > 0)
				game.kickall();
			VotingPhase vp = game.getVotingPhrase();
			if(vp != null) {
				vp.cancelTask();
			}
			CooldownPhase cp = game.getCooldownPhrase();
			if(cp != null)
				cp.cancelTask();
			IngamePhase ip = game.getIngamePhrase();
			if(ip != null) {
				ip.cancelDeathmatchTask();
				ip.cancelLightningTask();
				ip.cancelTask();
			}
			DeathmatchPhase dp = game.getDeathmatch();
			if(dp != null) {
				dp.cancelTask();
			}
			games.remove(game);
		}
	}
	
	public boolean load(String name) {
		if(getGame(name) != null) {
			System.out.println("[SurvivalGames] Lobby " + name + " is already loaded!");
			return false;
		}
		
		String path = "Games." + name;
		
		if(!cfg.contains(path)) {
			System.out.println("[SurvivalGames] Lobby " + name + " does not exist!");
			return false;
		}
		
		path += ".";
		
		if(!cfg.contains(path + "Arenas")) {
			System.out.println("[SurvivalGames] Lobby " + name + " has no arenas!");
			return false;
		}

		boolean reset = false;		
		if(SurvivalGames.reset.contains("Startup-Reset." + name)) {
			for(String key : SurvivalGames.reset.getConfigurationSection("Startup-Reset." + name + ".").getKeys(false)) {
				reset = true;
				new Reset(Util.parseLocation(cfg.getString(path + "Arenas." + key + ".Min")).getWorld(), name, key, SurvivalGames.reset.getStringList("Startup-Reset." + name + "." + key)).start();
			}
		}
		
		if(reset) {
			System.out.println("[SurvivalGames] Lobby " + name + " does not exist!");
			return false;
		}
		

		List<Arena> arenas = new ArrayList<>();
		
		for(String key : cfg.getConfigurationSection(path + "Arenas.").getKeys(false)) {
			if(!cfg.getBoolean(path + "Arenas." + key + ".Enabled")) {
				continue;
			}
	
			Arena arena = SurvivalGames.arenaManager.getArena(name, key);
			
			if(arena != null) {
				arenas.add(arena);
			}
		}
		
		if(arenas.size() == 0) {
			System.out.println("[SurvivalGames] No arena in lobby " + name + " loaded!");
			return false;
		}
		
		if(!cfg.contains(path + "Lobby") && arenas.size() != 1) {
			System.out.println("[SurvivalGames] The spawn point in lobby " + name + " isn't defined!");
			return false;
		}
		
		Location lobby = Util.parseLocation(cfg.getString(path + "Lobby"));
		boolean voting = cfg.getBoolean(path + "Enable-Voting");
		int lobbytime = cfg.getInt(path + "Lobby-Time");
		int maxVotingArenas = cfg.getInt(path + "Max-Voting-Arenas");
		int reqplayers = cfg.getInt(path + "Required-Players-to-start");
		boolean resetEnabled = SurvivalGames.instance.getConfig().getBoolean("Enable-Arena-Reset");
		
		games.add(new Game(name, lobby, voting, lobbytime, maxVotingArenas, reqplayers, arenas, resetEnabled));
		return true;
	}
	
	public List<Game> getGames() {
		return games;
	}
	
	public Game getGame(String name) {
		for(Game game : games) {
			if(game.getName().equalsIgnoreCase(name))
				return game;
		}
		return null;
	}

}
