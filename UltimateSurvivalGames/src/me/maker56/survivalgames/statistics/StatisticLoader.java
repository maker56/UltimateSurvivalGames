package me.maker56.survivalgames.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.database.sql.DatabaseManager;
import me.maker56.survivalgames.database.sql.DatabaseResponse;
import me.maker56.survivalgames.database.sql.DatabaseTask;
import me.maker56.survivalgames.database.sql.DatabaseThread;
import me.maker56.survivalgames.user.UserState;

public class StatisticLoader {
	
	public static void load(UserState state) {
		DatabaseTask task = new DatabaseTask("SELECT * FROM `" + DatabaseManager.tablePrefix + "players` WHERE uuid = '" + state.getUUID() + "'", true, rHandler);
		task.obj = new Object[] { state };
		DatabaseThread.addTask(task);
	}
	
	public static void load(CommandSender p, String name) {
		DatabaseTask task = new DatabaseTask("SELECT * FROM `" + DatabaseManager.tablePrefix + "players` WHERE lastname = '" + name + "'", true, rHandler);
		task.obj = new Object[] { p, name };
		DatabaseThread.addTask(task);
	}
	
	private static DatabaseResponse rHandler = new ResponseHandler();

}

class ResponseHandler implements DatabaseResponse {

	@Override
	public void response(DatabaseTask task, ResultSet rs) {
		try {
			int kills = 0, deaths = 0, points = 0, wins = 0, played = 0;
			float kdr = 0F;
			
			UserState state = null;
			CommandSender p = null;
			String name, uuid = null;
			
			if(task.obj.length == 1) {
				state = (UserState) task.obj[0];
				
				if(state == null || state.getPlayer() == null)
					return;
				
				name = state.getName();
				uuid = state.getUUID();
			} else {
				p = (Player) task.obj[0];
				
				if(p == null)
					return;
				
				name = (String) task.obj[1];
			}
			
			if(rs.next()) { 
				kills = rs.getInt("kills");
				deaths = rs.getInt("deaths");
				points = rs.getInt("points");
				wins = rs.getInt("wins");
				played = rs.getInt("played");
				kdr = rs.getFloat("kdr");
				if(uuid == null)
					uuid = rs.getString("uuid");
			} else {
				Player pl = Bukkit.getPlayer(name);
				if(p != null && pl == null) {
					p.sendMessage(MessageHandler.getMessage("stats-player-not-found").replace("%0%", name));
					return;
				} else if(uuid == null) {
					uuid = pl.getUniqueId().toString();
				}
				
				DatabaseThread.addTask(new DatabaseTask("INSERT INTO `" + DatabaseManager.tablePrefix + "players`(`uuid`, `lastname`) VALUES ('" + uuid + "','" + name + "')"));
			}
			
			StatisticData sd = new StatisticData(name, uuid, kills, deaths, points, wins, played, kdr);
			if(state != null) {
				StatisticManager.stats.remove(name.toLowerCase());
				state.setStatistics(sd);
			} else {
				StatisticManager.setStatistics(p, sd);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
