package me.maker56.survivalgames.database.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite implements DatabaseCore {

	private Connection connection;
	private String dbLocation;
	private String dbName = "PlayerData";
	private File file;
	
	protected SQLite(String loc) {
		this.dbLocation = loc;
	}
	
	@Override
	public void open() throws SQLException, ClassNotFoundException {
		if(file == null) {
			File folder = new File(dbLocation);
			if(!folder.exists()) {
				folder.mkdir();
			}
			file = new File(folder.getAbsolutePath() + File.separator + dbName + ".db");
		}
		Class.forName("org.sqlite.JDBC");
		this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
	}
	


	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public boolean checkConnection() {
		return getConnection() != null;
	}

	@Override
	public void close() {
		if(connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public ResultSet select(Statement statement, String query) throws SQLException {
		return statement.executeQuery(query);
	}
	@Override
	public void execute(Statement statement, String query) throws SQLException {
		statement.execute(query);
	}
	
	@Override
	public PreparedStatement prepareStatement(String statement) throws SQLException {
		return getConnection().prepareStatement(statement);
	}
	
	public File getFile() {
		return file;
	}

	@Override
	public String getType() {
		return "SQLite";
	}

}