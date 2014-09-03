package me.maker56.survivalgames.statistics;

import me.maker56.survivalgames.database.sql.DatabaseManager;
import me.maker56.survivalgames.database.sql.DatabaseTask;
import me.maker56.survivalgames.database.sql.DatabaseThread;

public class StatisticData {
	
	protected long creation = System.currentTimeMillis();
	String name, uuid;
	int kills, deaths, points, wins, played;
	float kdr;
	
	public StatisticData(String name, String uuid, int kills, int deaths, int points, int wins, int played, float kdr) {
		this.name = name;
		this.uuid = uuid;
		this.kills = kills;
		this.deaths = deaths;
		this.points = points;
		this.wins = wins;
		this.played = played;
		this.kdr = kdr;
	}
	
	public void updateStatistics() {
		DatabaseThread.addTask(new DatabaseTask("UPDATE `" + DatabaseManager.tablePrefix + "players` SET `lastname`='" + name + "'," +
				"`kills`='" + kills + "',`deaths`='" + deaths + "',`kdr`='" + kdr + "',`wins`='" + wins + "'," +
				"`played`='" + played + "',`points`='" + points + "' WHERE uuid = '" + uuid + "'"));
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

}

