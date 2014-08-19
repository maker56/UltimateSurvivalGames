package me.maker56.survivalgames.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.maker56.survivalgames.database.sql.DatabaseManager;
import me.maker56.survivalgames.database.sql.DatabaseResponse;
import me.maker56.survivalgames.database.sql.DatabaseTask;
import me.maker56.survivalgames.database.sql.DatabaseThread;

public class StatisticData {
	
	protected long creation = System.currentTimeMillis();
	String name, uuid;
	
	public StatisticData(String name, String uuid) {
		this.name = name;
		this.uuid = uuid;
		sendRequest();
	}
	
	boolean loaded;
	int kills, deaths, points, wins, played;
	float kdr;
	
	private void sendRequest() {
		DatabaseTask task = new DatabaseTask("SELECT * FROM `" + DatabaseManager.tablePrefix + "players` WHERE uuid = '" + uuid + "'", true, rHandler);
		task.obj = new Object[] { this };
		DatabaseThread.addTask(task);
	}
	
	public boolean hasLoadedStatistics() {
		return loaded;
	}
	
	public void updateStatistics() {
		if(loaded) {
			DatabaseThread.addTask(new DatabaseTask("UPDATE `" + DatabaseManager.tablePrefix + "players` SET `lastname`='" + name + "'," +
					"`kills`='" + kills + "',`deaths`='" + deaths + "',`kdr`='" + kdr + "',`wins`='" + wins + "'," +
					"`played`='" + played + "',`points`='" + points + "' WHERE uuid = '" + uuid + "'"));
		}
	}

	// VALUES
	
	public int getKills() {
		return kills;
	}
	
	public void addKill() {
		kills++;
		recalculateKDR();
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void addDeath() {
		deaths++;
		recalculateKDR();
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public int getWins() {
		return wins;
	}
	
	public void addWin() {
		wins++;
	}
	
	public int getPlayed() {
		return played;
	}
	
	public void addPlayed() {
		played++;
	}
	
	public float getKDR() {
		return kdr;
	}
	
	private void recalculateKDR() {
		kdr = deaths == 0 ? (float)kills : (float)kills / (float)deaths;
	}
	
	// VALUES END
	
	private static ResponseHandler rHandler = new ResponseHandler();

}

class ResponseHandler implements DatabaseResponse {

	@Override
	public void response(DatabaseTask task, ResultSet rs) {
		try {
			StatisticData sd = (StatisticData) task.obj[0];
			if(sd != null) {
				if(rs.next()) {
					sd.kills = rs.getInt("kills");
					sd.deaths = rs.getInt("deaths");
					sd.points = rs.getInt("points");
					sd.wins = rs.getInt("wins");
					sd.played = rs.getInt("played");
					sd.kdr = rs.getFloat("kdr");
				} else {
					DatabaseThread.addTask(new DatabaseTask("INSERT INTO `" + DatabaseManager.tablePrefix + "players`(`uuid`, `lastname`) VALUES ('" + sd.uuid + "','" + sd.name + "')"));
				}
				sd.loaded = true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
