package me.maker56.survivalgames.database;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DatabaseLoader {
	
	private File rawFile;
	private String path, fileName;
	
	@Deprecated
	public DatabaseLoader(String path, String target, String res) {
		this.path = path;
		this.fileName = target;
	}
	
	public DatabaseLoader(String path, String target) {
		this.path = path;
		this.fileName = target;
	}
	
	public FileConfiguration getFileConfiguration() {
		rawFile = new File(path, fileName);
		
		if(!rawFile.exists()) {
			new File(path).mkdirs();
			
			try {
				
				rawFile.createNewFile();
				
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		
		FileConfiguration fc = new YamlConfiguration();
		
		try {
			fc.load(rawFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		return fc;
	}

}
