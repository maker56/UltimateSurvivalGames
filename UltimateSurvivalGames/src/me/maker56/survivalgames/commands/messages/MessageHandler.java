package me.maker56.survivalgames.commands.messages;

import java.util.HashMap;

import me.maker56.survivalgames.SurvivalGames;

import org.bukkit.ChatColor;

public class MessageHandler {
	
	private static HashMap<String, String> messages = new HashMap<>();
	
	public static void reload() {
		messages.clear();
		for(String key : SurvivalGames.messages.getConfigurationSection("").getKeys(false)) {
			messages.put(key, replaceColors(SurvivalGames.messages.getString(key)));
		}
		System.out.println("[SurvivalGames] " + messages.size() + " messages loaded!");
	}
	
	public static String getMessage(String name) {
		if(messages.containsKey(name)) {
			if(name.equalsIgnoreCase("prefix")) {
				return messages.get(name);
			} else {
				return messages.get("prefix") + messages.get(name);
			}
		} else {
			return "§cMessage not found!";
		}
	}
	
	public static String replaceColors(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

}
