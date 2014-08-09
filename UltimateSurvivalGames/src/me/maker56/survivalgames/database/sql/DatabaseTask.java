package me.maker56.survivalgames.database.sql;

public class DatabaseTask {
	
	protected String command;
	protected boolean select;
	protected DatabaseResponse response;
	public Exception error;
	
	public DatabaseTask(String command) {
		this(command, false, null);
	}
	
	public DatabaseTask(String command, boolean select, DatabaseResponse response) {
		this.command = command;
		this.select = select;
		this.response = response;
	}
	
	public Object[] obj;

}
