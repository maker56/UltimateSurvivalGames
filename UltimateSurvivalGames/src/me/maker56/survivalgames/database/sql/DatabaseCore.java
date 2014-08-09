package me.maker56.survivalgames.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DatabaseCore {
	
	public abstract String getType();
	
	public abstract Connection getConnection();

	public abstract boolean checkConnection();

	public abstract void open() throws SQLException, ClassNotFoundException;
	
	public abstract void close();

	public abstract ResultSet select(Statement statement, String query) throws SQLException;

	public abstract void execute(Statement statement, String query) throws SQLException;

	public abstract PreparedStatement prepareStatement(String statement) throws SQLException;

}
