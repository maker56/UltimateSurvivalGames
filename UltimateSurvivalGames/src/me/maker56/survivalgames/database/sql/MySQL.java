package me.maker56.survivalgames.database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL implements DatabaseCore {
	
	private final String url, username, password;
	private Connection con;
	
	protected MySQL(String host, int port, String database, String username, String password) {
		this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		this.username = username;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		return con;
	}

	@Override
	public boolean checkConnection() {
		return con != null;
	}
	
	@Override
	public void open() throws SQLException, ClassNotFoundException {
		if(con != null)
			close();
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(url, username, password);
	}

	@Override
	public void close() {
		if(con != null) {
			try {
				con.close();
				con = null;
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

	@Override
	public String getType() {
		return "MySQL";
	}

}
