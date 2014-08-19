package me.maker56.survivalgames.scoreboard;

import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.game.GameState;

public class ScoreBoardManager {
	
	public ScoreBoardManager() {
		reinitializeDatabase();
	}
	
	public ScoreboardPhase getNewScoreboardPhase(GameState state) {
		if(phases.containsKey(state))
			return phases.get(state).clone();
		return null;
	}
	
	// STATIC
	private static HashMap<GameState, ScoreboardPhase> phases = new HashMap<>();
	public static void reinitializeDatabase() {
		FileConfiguration c = SurvivalGames.scoreboard;
		
		phases.clear();
		if(c.contains("Phase")) {
			for(String key : c.getConfigurationSection("Phase.").getKeys(false)) {
				try {
					if(!c.getBoolean("Phase." + key + ".Enabled"))
						continue;
					GameState state = GameState.valueOf(key.toUpperCase());
					String title = c.getString("Phase." + key + ".Title");
					List<String> scores = c.getStringList("Phase." + key + ".Scores");
					ScoreboardPhase sp = new ScoreboardPhase(title, scores);
					phases.put(state, sp);
				} catch(Exception e) {
					System.err.println("[SurvivalGames] Can't load scoreboard phase " + key + " - Mabye this is the reason: " + e.toString());
				}

			}
		}
		System.out.println("[SurvivalGames] " + phases.size() + " scoreboard phases loaded!");
	}

}
