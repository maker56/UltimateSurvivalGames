package me.maker56.survivalgames.database.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseThread extends Thread {
	
	private DatabaseCore core;
	private static List<DatabaseTask> tasks = new ArrayList<>();
	
	protected DatabaseThread(DatabaseCore core) {
		this.core = core;
	}
	
	@Override
	public void run() {
		try {
			core.open();
			System.out.println("[SurvivalGames] Successfully connected to database! Using database type " + core.getType() + "!");
		} catch(Exception e) {
			System.err.println("[SurvivalGames] Can't connect to database: " + e.toString());
			if(core.getType().equals("MySQL")) {
				System.out.println("[SurvivalGames] The plugin now use SQLite");
				DatabaseManager.open("SQLITE");
			}
			return;
		}
		
		while(DatabaseManager.run) {
			long sleep = 50;
			if(!tasks.isEmpty()) {
				try {
					Connection con = core.getConnection();
					Statement s = con.createStatement();
					while(!tasks.isEmpty()) {
						DatabaseTask task = null;
						try {
							task = tasks.get(0);
							if(task.select) {
								ResultSet rs = s.executeQuery(task.command);
								task.response.response(task, rs);
								rs.close();
							} else {
								s.executeUpdate(task.command);
							}
						} catch(SQLException e) {
							if(task != null) {
								task.error = e;
							}
							System.err.println("[SurvivalGames] Error while executing task" + (task == null ? ": " : " (" + task.command + "|" + task.select + "): ") + e.toString());
							sleep += 450;
						}
						tasks.remove(task);
					}
					s.close();
				} catch(SQLException e) {
					System.err.println("[SurvivalGames] Error while working on tasks: " + e.toString());
					sleep += 450;
				}
			}
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		core.close();
		System.out.println("[SurvivalGames] Disconnected from database!");
	}
	
	public static void addTask(DatabaseTask task) {
		tasks.add(task);
	}

}
