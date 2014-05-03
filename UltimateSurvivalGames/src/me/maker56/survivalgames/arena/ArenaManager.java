package me.maker56.survivalgames.arena;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.database.ConfigUtil;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.listener.SelectionListener;
import me.maker56.survivalgames.reset.Reset;
import me.maker56.survivalgames.reset.Save;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ArenaManager {
	
	private static FileConfiguration cfg = SurvivalGames.database;
	public HashMap<String, String[]> selectedarena = new HashMap<>();
	
	public static void reinitializeDatabase() {
		cfg = SurvivalGames.database;
	}
	
	public Arena getArena(Location loc) {
		for(Game game : SurvivalGames.gameManager.getGames()) {
			for(Arena arena : game.getArenas()) {
				if(arena.containsBlock(loc))
					return arena;
			}
		}
		return null;
	}
	
	
	// CONFIGURATION ==================================================
		
	// ARENA SAVEN
	
	public void save(Player p) {
		if(!selectedarena.containsKey(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
			return;
		}
		
		String gamename = selectedarena.get(p.getName())[0];
		String arenaname = selectedarena.get(p.getName())[1];
		
		if(Save.isSaveing(gamename, arenaname) || Reset.isResetting(gamename, arenaname)) {
			Bukkit.broadcastMessage(Boolean.valueOf(Save.isSaveing(gamename, arenaname)).toString() + " " + Boolean.valueOf(Reset.isResetting(gamename, arenaname)));
			p.sendMessage(MessageHandler.getMessage("prefix") + "§cThis arena is already saveing or resetting.");
			return;
		}
		
		Game game = SurvivalGames.gameManager.getGame(gamename);
		if(game != null) {
			Arena arena = game.getArena(arenaname);
			if(arena != null) {
				p.sendMessage(MessageHandler.getMessage("prefix") + "§cYou can only save arenas of an unloaded lobby.");
				return;
			}
		}
		
		
		Location min = ConfigUtil.parseLocation(cfg.getString("Games." + gamename + ".Arenas." + arenaname + ".Min"));
		Location max = ConfigUtil.parseLocation(cfg.getString("Games." + gamename + ".Arenas." + arenaname + ".Max"));
		
		if(min == null || max == null) {
			p.sendMessage(MessageHandler.getMessage("prefix") + "The arena isn't defined yet.");
			return;
		}
		
		p.sendMessage(MessageHandler.getMessage("prefix") + "Saveing arena... This may take a while. Laggs can be occure. You'll get a message, if the save is completed.");
		new Save(gamename, arenaname, min, max, p.getName()).start();
	}
	
	// ARENA LÖSCHEN
	
	public void delete(Player p) {
		if(!selectedarena.containsKey(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
			return;
		}
		
		String gamename = selectedarena.get(p.getName())[0];
		String arenaname = selectedarena.get(p.getName())[1];
		
		if(!cfg.contains("Games." + gamename)) {
			p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", gamename));
			return;
		}
		
		if(!cfg.contains("Games." + gamename + ".Arenas." + arenaname)) {
			p.sendMessage(MessageHandler.getMessage("prefix") + "§cArena " + arenaname + " in lobby " + gamename + " not found!");
			return;
		}
		
		Game game = SurvivalGames.gameManager.getGame(gamename);
		if(game != null) {
			p.sendMessage(MessageHandler.getMessage("prefix") + "§cYou can only delete arenas of an unloaded lobby.");
			return;
		}
		
		cfg.set("Games." + gamename + ".Arenas." + arenaname, null);
		SurvivalGames.saveDataBase();
		p.sendMessage(MessageHandler.getMessage("prefix") + "Arena " + arenaname + " was deleted in lobby " + gamename + " successfull!");
	}
	
	// ARENA CHECKEN
	
	public void check(Player p) {
		if(!selectedarena.containsKey(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
			return;
		}
		
		String gamename = selectedarena.get(p.getName())[0];
		String arenaname = selectedarena.get(p.getName())[1];
		
		p.sendMessage(MessageHandler.getMessage("prefix") + "Arena-Check: Arena §e" + arenaname + "§6, Game §e" + gamename);
		String path = "Games." + gamename + ".Arenas." + arenaname + ".";
		
		boolean enabled = cfg.getBoolean(path + "Enabled");
		
		if(enabled) {
			p.sendMessage("§aThis arena is ready to play!");
		}
		
		int spawns = cfg.getStringList(path + "Spawns").size();

		if(spawns < 2) {
			p.sendMessage(" §8§l➥ §bSpawns §7(§c" + spawns + "§7) §eAt least 2 Spawns required");
		} else {
			p.sendMessage(" §8§l➥ §bSpawns §7(§a" + spawns + "§7) §eAt least 2 Spawns required");
		}
		
		boolean deathmatch = cfg.getBoolean(path + "Enable-Deathmatch");
		int dspawns = cfg.getStringList(path + "Deathmatch-Spawns").size();
		
		p.sendMessage(" §8§l–º §bDeathmatch §7(§a" + deathmatch + "§7) §e(optional)");
		
		if(deathmatch == true) {
			if(dspawns < 1) {
				p.sendMessage(" §8§l➥ §bDeathmatch-Spawns §7(§c" + dspawns + "§7) §eAt least 1 Deathmatch Spawn required");	
			} else {
				p.sendMessage(" §8§l➥ §bDeathmatch-Spawns §7(§a" + dspawns + "§7) §eAt least 1 Deathmatch Spawn required");	
			}
		}
		
		p.sendMessage("   ");
		p.sendMessage("§e§lNext step:");
		
		if(spawns < 2) {
			p.sendMessage("§aAt least are 2 Spawns required. Type §b/sg arena addspawn §ato add more spawns!");
		} else if(deathmatch == true && dspawns < 1){
			p.sendMessage("§aAt least are 1 Deathmatch-Spawn required. Type §b/sg arena deathmatch add §ato add more Deathmatch-Spawns!");
		} else {
			p.sendMessage("§aThis arena is ready to play. Just type §b/sg arena finish §ato finish the setup!");
		}
	}
	
	// SETUP FINISHEN
	
	public void finishSetup(Player p) {
		if(!selectedarena.containsKey(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
			return;
		}
		
		String gamename = selectedarena.get(p.getName())[0];
		String arenaname = selectedarena.get(p.getName())[1];
		String path = "Games." + gamename + ".Arenas." + arenaname + ".";
		
		if(SurvivalGames.gameManager.getGame(gamename) != null) {
			p.sendMessage(MessageHandler.getMessage("prefix") + "§cYou can't add an arena to a loaded lobby. Unload the lobby first with /sg lobby unload " + gamename);
		} else {
			cfg.set(path + "Enabled", true);
			SurvivalGames.saveDataBase();
			
			Game game = SurvivalGames.gameManager.getGame(gamename);
			if(game == null) {
				SurvivalGames.gameManager.unload(game);
			}
			SurvivalGames.gameManager.load(gamename);
				
			p.sendMessage(MessageHandler.getMessage("prefix") + "§aYou've finished the setup and activated the arena successfully!");
		}
	}
	
	// DEATHMATCH CHANGEN
	
	public void changeDeathmatch(Player p) {
		if(!selectedarena.containsKey(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
			return;
		}
		
		String gamename = selectedarena.get(p.getName())[0];
		String arenaname = selectedarena.get(p.getName())[1];
		String path = "Games." + gamename + ".Arenas." + arenaname + ".";
		
		boolean deathmatch = cfg.getBoolean(path + "Enable-Deathmatch");
		
		if(deathmatch) {
			cfg.set(path + "Enable-Deathmatch", false);
			p.sendMessage(MessageHandler.getMessage("arena-deathmatch-changed").replace("%0%", "§cFALSE"));
		} else {
			cfg.set(path + "Enable-Deathmatch", true);
			p.sendMessage(MessageHandler.getMessage("arena-deathmatch-changed").replace("%0%", "§aTRUE"));
		}
		SurvivalGames.saveDataBase();
	}
	
	// SPAWN ADDEN
	
	public void addSpawn(Player p, String type) {
		if(!selectedarena.containsKey(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
			return;
		}
		
		String gamename = selectedarena.get(p.getName())[0];
		String arenaname = selectedarena.get(p.getName())[1];
		String path = "Games." + gamename + ".Arenas." + arenaname + ".";
		
		List<String> l = cfg.getStringList(path + type);
		Location loc = p.getLocation();
		l.add(loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
		cfg.set(path + type, l);
		SurvivalGames.saveDataBase();
		p.sendMessage(MessageHandler.getMessage("arena-spawn-added").replace("%0%", Integer.valueOf(l.size()).toString()));
	}
	
	// SPAWN REMVOVEN
	
	public void removeSpawn(Player p, int id, String type) {
		if(!selectedarena.containsKey(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("arena-must-select").replace("%0%", "/sg arena select <LOBBYNAME> <ARENA NAME>"));
			return;
		}
		
		String gamename = selectedarena.get(p.getName())[0];
		String arenaname = selectedarena.get(p.getName())[1];
		String path = "Games." + gamename + ".Arenas." + arenaname + ".";
		

		id--;
		List<String> l = cfg.getStringList(path + type);
		
		try  {
			l.get(id);
		} catch(IndexOutOfBoundsException e) {
			id++;
			p.sendMessage(MessageHandler.getMessage("arena-spawn-notfound").replace("%0%", Integer.valueOf(id).toString()));
			return;
		}
		
		l.remove(id);
		cfg.set(path + type, l);
		SurvivalGames.saveDataBase();
		
		id++;
		p.sendMessage(MessageHandler.getMessage("arena-spawn-removed").replace("%0%", Integer.valueOf(id).toString()));
	}
	
	// ARENA ERSTELLEN
	
	public void createArena(Player p, String arenaname, String gamename) {
		if(!cfg.contains("Games." + gamename)) {
			p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", gamename));
			return;
		}
		
		if(cfg.contains("Games." + gamename + ".Arenas." + arenaname)) {
			p.sendMessage(MessageHandler.getMessage("arena-already-exists").replace("%0%", arenaname).replace("%1%", gamename));
			return;
		}
		
		WorldEditPlugin we = SurvivalGames.getWorldEdit();
		
		Location min = null, max = null;
		
		if(we == null) {
			if(SelectionListener.selection.containsKey(p.getName())) {
				Location[] loc = SelectionListener.selection.get(p.getName());
				
				if(loc[0] == null || loc[1] == null) {
					p.sendMessage(MessageHandler.getMessage("arena-no-selection").replace("%0%", "/sg arena tools"));
					return;
				} else {
					min = new Location(loc[0].getWorld(), Math.min(loc[0].getBlockX(), loc[1].getBlockX()), Math.min(loc[0].getBlockY(), loc[1].getBlockY()), Math.min(loc[0].getBlockZ(), loc[1].getBlockZ()));
					min = new Location(loc[0].getWorld(), Math.max(loc[0].getBlockX(), loc[1].getBlockX()), Math.max(loc[0].getBlockY(), loc[1].getBlockY()), Math.max(loc[0].getBlockZ(), loc[1].getBlockZ()));
				}
			} else {
				p.sendMessage(MessageHandler.getMessage("arena-no-selection").replace("%0%", "/sg arena tools"));
				return;
			}
		} else {
			Selection sel = we.getSelection(p);
			
			if(sel == null || sel.getMinimumPoint() == null || sel.getMaximumPoint() == null) {
				p.sendMessage(MessageHandler.getMessage("arena-no-selection").replace("%0%", "/sg arena tools"));
				return;
			}
			
			min = sel.getMinimumPoint();
			max = sel.getMaximumPoint();
		}
		
		int chesttype = SurvivalGames.instance.getConfig().getInt("Default.Arena.Chests.TypeID");
		int chestdata = SurvivalGames.instance.getConfig().getInt("Default.Arena.Chests.Data");
		
		String path = "Games." + gamename + ".Arenas." + arenaname + ".";
		
		cfg.set(path + "Enabled", false);
		
		cfg.set(path + "Grace-Period", SurvivalGames.instance.getConfig().getInt("Default.Arena.Grace-Period"));
		
		cfg.set(path + "Min", min.getWorld().getName() + "," + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ());
		cfg.set(path + "Max", max.getWorld().getName() + "," + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ());
		
		cfg.set(path + "Allowed-Blocks", SurvivalGames.instance.getConfig().getIntegerList("Default.Arena.Allowed-Blocks"));
		
		cfg.set(path + "Chest.TypeID", chesttype);
		cfg.set(path + "Chest.Data", chestdata);
		
		cfg.set(path + "Spawns", new ArrayList<String>());
		
		cfg.set(path + "Enable-Deathmatch", false);
		
		cfg.set(path + "Player-Deathmatch", SurvivalGames.instance.getConfig().getInt("Default.Arena.Player-Deathmatch-Start"));
		cfg.set(path + "Auto-Deathmatch", SurvivalGames.instance.getConfig().getInt("Default.Arena.Automaticly-Deathmatch-Time"));
		
		cfg.set(path + "Deathmatch-Spawns", new ArrayList<String>());
		
		cfg.set(path + "Money-on-Kill", SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Kill"));
		cfg.set(path + "Money-on-Win", SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Win"));
		
		cfg.set(path + "Midnight-chest-refill", SurvivalGames.instance.getConfig().getBoolean("Default.Midnight-chest-refill"));
		
		SurvivalGames.saveDataBase();
		selectArena(p, arenaname, gamename);
		p.sendMessage(MessageHandler.getMessage("arena-created").replace("%0%", arenaname).replace("%1%", gamename));
		if(SurvivalGames.instance.getConfig().getBoolean("Enable-Arena-Reset"))
			save(p);
		p.sendMessage(MessageHandler.getMessage("arena-check").replace("%0%", "/sg arena check"));
		return;
	}
	
	
	// ARENA AUSWÃ„HLEN
	
	public void selectArena(Player p, String arenaname, String gamename) {
		if(!cfg.contains("Games." + gamename)) {
			p.sendMessage(MessageHandler.getMessage("game-not-found").replace("%0%", gamename));
			return;
		}
		
		if(!cfg.contains("Games." + gamename + ".Arenas." + arenaname)) {
			p.sendMessage(MessageHandler.getMessage("arena-not-found").replace("%0%", arenaname).replace("%1%", gamename));
			return;
		}
		
		selectedarena.put(p.getName(), new String[] { gamename, arenaname });
		p.sendMessage(MessageHandler.getMessage("arena-selected").replace("%0%", arenaname).replace("%1%", gamename));
	}
	
	// ARENA GETTEN
	
	@SuppressWarnings("deprecation")
	public Arena getArena(String game, String arenaname) {
		if(!new File("plugins/SurvivalGames/reset/" + game + arenaname + ".map").exists() && SurvivalGames.instance.getConfig().getBoolean("Enable-Arena-Reset")) {
			System.out.println("[SurvivalGames] Cannot load arena " + arenaname + " in lobby " + game + ": Arena map file is missing! To create a map file, select the arena first with /sg arena select " + game + " " + arenaname + " and type /sg arena save!");
			return null;
		}
		
		String path = "Games." + game + ".Arenas." + arenaname + ".";
		
		Location min = ConfigUtil.parseLocation(cfg.getString(path + "Min"));
		Location max = ConfigUtil.parseLocation(cfg.getString(path + "Max"));
		
		int graceperiod = cfg.getInt(path + "Grace-Period");
		
		Material chesttype = Material.getMaterial(cfg.getInt(path + "Chest.TypeID"));
		int chestdata = cfg.getInt(path + "Chest.Data");
		
		List<Location> spawns = new ArrayList<>();
		
		for(String key : cfg.getStringList(path + "Spawns")) {
			spawns.add(ConfigUtil.parseLocation(key));
		}
		
		boolean deathmatch = cfg.getBoolean(path + "Enable-Deathmatch");
		List<Location> deathmatchspawns = new ArrayList<>();
		
		if(deathmatch) {
			for(String key : cfg.getStringList(path + "Deathmatch-Spawns")) {
				deathmatchspawns.add(ConfigUtil.parseLocation(key));
			}
		}
		
		List<Integer> allowedBlocks = cfg.getIntegerList(path + "Allowed-Blocks");
		
		int autodeathmatch = cfg.getInt(path + "Auto-Deathmatch");
		int playerdeathmatch = cfg.getInt(path + "Player-Deathmatch");
		
		if(!cfg.contains(path + "Money-on-Kill")) {
			cfg.set(path + "Money-on-Kill", SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Kill"));
			cfg.set(path + "Money-on-Win", SurvivalGames.instance.getConfig().getDouble("Default.Money-on-Win"));
			
			cfg.set(path + "Midnight-chest-refill", SurvivalGames.instance.getConfig().getBoolean("Default.Midnight-chest-refill"));
			SurvivalGames.saveDataBase();
		}
		double kill = cfg.getDouble(path + "Money-on-Kill");
		double win = cfg.getDouble(path + "Money-on-Win");
		
		boolean refill = cfg.getBoolean(path + "Midnight-chest-refill");
		
		return new Arena(min, max, spawns, chesttype, chestdata, graceperiod, arenaname, game, deathmatch, deathmatchspawns, allowedBlocks, autodeathmatch, playerdeathmatch, kill, win, refill);
	}

}
