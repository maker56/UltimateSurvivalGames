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
	
	// OTHER STUFF
	
	public static void load() {
		DatabaseThread.addTask(new DatabaseTask("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "players` (" +
						" `uuid` varchar(36) NOT NULL," +
						" `lastname` varchar(16) NOT NULL," +
						" `kills` int(100) default '0'," +
						" `deaths` int(100) default '0'," +
						" `kdr` float default '0'," +
						" `points` int(100) default '0');"));
		
		DatabaseThread.addTask(new DatabaseTask("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "games` (" +
				" `id` bigint(20)," +
				" `arena` varchar(100) NOT NULL," +
				" `duration` int(100)," +
				" `end` datetime," +
				" `players` int(100)," +
				" PRIMARY KEY (`id`) );"));
		
		DatabaseThread.addTask(new DatabaseTask("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "kills` (" +
				" `player` varchar(36) NOT NULL," +
				" `victim` varchar(36) NOT NULL," +
				" `health` int(100)," +
				" `time` datetime);"));
	}
	
	public static String getTablePrefix() {
		return tablePrefix;
	}
}
