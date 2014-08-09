package me.maker56.survivalgames.database.sql;

import java.sql.ResultSet;

public interface DatabaseResponse {
	
	public void response(DatabaseTask task, ResultSet rs);

}
