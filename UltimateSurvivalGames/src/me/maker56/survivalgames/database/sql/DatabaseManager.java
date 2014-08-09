package me.maker56.survivalgames.database.sql;

import org.bukkit.configuration.file.FileConfiguration;

import me.maker56.survivalgames.SurvivalGames;

public class DatabaseManager {
	
	protected static boolean run = true;
	private static DatabaseThread thread = null;
	public static final String tablePrefix = SurvivalGames.instance.getConfig().getString("SQL.TablePrefix");
	
	public static void open() {
		FileConfiguration config = SurvivalGames.instance.getConfig();
		String type = config.getString("SQL.Type").toUpperCase();
		
		if(type == null) {
			System.out.println("[SurvivalGames] Can't parse playerdata database type. Using SQLITE");
			type = "SQLITE";
		}
		
		open(type);
	}
	
	protected static void open(String type) {
		DatabaseCore core = null;
		if(type.equals("SQLITE")) {
			core = new SQLite(SurvivalGames.instance.getDataFolder().getPath());
		} else if(type.equals("MYSQL")) {
			FileConfiguration config = SurvivalGames.instance.getConfig();
			core = new MySQL(config.getString("SQL.MySQL.Host"), config.getInt("SQL.MySQL.Port"),
					config.getString("SQL.MySQL.Database"), config.getString("SQL.MySQL.Username"),
					config.getString("SQL.MySQL.Password"));
		} else {
			System.out.println("[SurvivalGames] Can't parse playerdata database type \"" + type + "\". Using SQLITE");
			open("SQLITE");
			return;
		}
		thread = new DatabaseThread(core);
		thread.start();
	}
	
	public static void close() {
		run = false;
		thread = null;
	}
}
