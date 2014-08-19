package me.maker56.survivalgames;

import java.io.IOException;

import me.maker56.survivalgames.arena.ArenaManager;
import me.maker56.survivalgames.arena.chest.ChestListener;
import me.maker56.survivalgames.arena.chest.ChestManager;
import me.maker56.survivalgames.barapi.BarAPIManager;
import me.maker56.survivalgames.commands.CommandSG;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.database.ConfigLoader;
import me.maker56.survivalgames.database.sql.DatabaseManager;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.listener.ChatListener;
import me.maker56.survivalgames.listener.PlayerListener;
import me.maker56.survivalgames.listener.ResetListener;
import me.maker56.survivalgames.listener.SelectionListener;
import me.maker56.survivalgames.listener.SignListener;
import me.maker56.survivalgames.listener.SpectatorListener;
import me.maker56.survivalgames.listener.UpdateListener;
import me.maker56.survivalgames.metrics.Metrics;
import me.maker56.survivalgames.scoreboard.ScoreBoardManager;
import me.maker56.survivalgames.sign.SignManager;
import me.maker56.survivalgames.user.UserManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SurvivalGames extends JavaPlugin {
	
	public static SurvivalGames instance;
	public static FileConfiguration messages, database, signs, reset, chestloot, scoreboard, barapi, kits;
	
	public static ArenaManager arenaManager;
	public static GameManager gameManager;
	public static ChestManager chestManager;
	public static UserManager userManger;
	public static SignManager signManager;
	public static ScoreBoardManager scoreBoardManager;
	
	public static Economy econ;
	
	public static String version = "SurvivalGames - Version ";
	
	private static PluginManager pm = Bukkit.getPluginManager();
	
	public void onDisable() {
		if(gameManager != null) {
			for(Game game : gameManager.getGames()) {
				game.kickall();
			}
		}
		DatabaseManager.close();
	}
	
	public void onEnable() {
		if(!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
			System.err.println("[SurvivalGames] ##########################################################");
			System.err.println("[SurvivalGames] ######### NO WORLDEDIT FOUND! DISABLE PLUGIN... ##########");
			System.err.println("[SurvivalGames] ##########################################################");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		instance = this;
		version += getDescription().getVersion();
		
		new ConfigLoader().load();
		DatabaseManager.open();
		DatabaseManager.load();
		
		startUpdateChecker();
		
		PermissionHandler.reinitializeDatabase();
		Game.reinitializeDatabase();
		MessageHandler.reload();
		BarAPIManager.reinitializeDatabase();
		
		if(setupEconomy())
			System.out.println("[SurvivalGames] Vault found!");
		
		// TEMPORARY
		Util.checkForOutdatedArenaSaveFiles();

		chestManager = new ChestManager();
		scoreBoardManager = new ScoreBoardManager();
		arenaManager = new ArenaManager();
		gameManager = new GameManager();
		userManger = new UserManager();
		signManager = new SignManager();
		
		getCommand("sg").setExecutor(new CommandSG());
		
		pm.registerEvents(new SelectionListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new ChestListener(), this);
		pm.registerEvents(new SignListener(), this);
		pm.registerEvents(new ResetListener(), this);
		pm.registerEvents(new UpdateListener(), this);
		pm.registerEvents(new SpectatorListener(), this);
		pm.registerEvents(new ChatListener(), this);
		
		try {
			new Metrics(this).start();
		} catch (IOException e) {
			System.err.println("[SurvivalGames] Cannot load metrics: " + e.getMessage());
		}
		
		if(getWorldEdit() != null) {
			System.out.println("[SurvivalGames] Plugin enabled. WorldEdit found!");
		} else {
			System.out.println("[SurvivalGames] Plugin enabled.");
		}
		
		signManager.updateSigns();
	}
	
	// UPDATE CHECKING

	public void startUpdateChecker() {
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				new UpdateCheck(SurvivalGames.instance, 61788);
			}
		}, 0L, 216000);
	}
	
	// VAULT
	
	private boolean setupEconomy() {
		if(Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
	        if (economyProvider != null) {
	            econ = economyProvider.getProvider();
	        }
		}

        return (econ != null);
	}
	
	// FILECONFIGURATION SAVE
	
	public static void saveMessages() {
		try {
			messages.save("plugins/SurvivalGames/messages.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveDataBase() {
		try {
			database.save("plugins/SurvivalGames/database.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveSigns() {
		try {
			signs.save("plugins/SurvivalGames/signs.yml");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveReset() {
		try {
			reset.save("plugins/SurvivalGames/reset.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveChests() {
		try {
			chestloot.save("plugins/SurvivalGames/chestloot.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveScoreboard() {
		try {
			scoreboard.save("plugins/SurvivalGames/scoreboard.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveBarAPI() {
		try {
			barapi.save("plugins/SurvivalGames/barapi.yml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveKits() {
		try {
			kits.save("plugins/SurvivalGames/kits.yml");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	// WORLDEDIT
	
	public static WorldEditPlugin getWorldEdit() {
		if(!pm.isPluginEnabled("WorldEdit")) {
			return null;
		} else {
			return (WorldEditPlugin) pm.getPlugin("WorldEdit");
		}
	}
	
	// API
	
	public static GameManager getGameManager() {
		return gameManager;
	}
	
	public static ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	public static ChestManager getChestManager() {
		return chestManager;
	}
	
	public static UserManager getUserManager() {
		return userManger;
	}
	
	public static SignManager getSignManager() {
		return signManager;
	}
	
	public static ScoreBoardManager getScoreboardManager() {
		return scoreBoardManager;
	}
	
	public static SurvivalGames getInstance() {
		return instance;
	}

}
