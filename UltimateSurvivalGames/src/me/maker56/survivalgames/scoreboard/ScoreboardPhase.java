package me.maker56.survivalgames.scoreboard;

import java.util.ArrayList;
import java.util.List;

import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardPhase {
	
	private String title;
	private List<String> scores = new ArrayList<>();
	
	private Scoreboard scoreboard;
	private Objective sidebar;
	private List<CustomScore> Sscore;
	
	protected ScoreboardPhase(String title, List<String> scores) {
		if(title.length() > 32)
			title = title.substring(0, 32);
		this.title = ChatColor.translateAlternateColorCodes('&', title);
		
		for(String score : scores) {
			String[] split = score.split("//");
			if(split[0].length() > 48)
				split[0] = split[0].substring(0, 48);
			score = split[0] + "//" + split[1];
			this.scores.add(ChatColor.translateAlternateColorCodes('&', score));
		}
	}
	
	@SuppressWarnings("deprecation")
	public Scoreboard initScoreboard(Game game) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		sidebar.setDisplayName(title);
		
		Sscore = new ArrayList<>();
		int tName = 0;
		for(int i = 0; i < scores.size(); i++) {
			String score = scores.get(i);
			try {
				String[] split = score.split("//");
				String name = split[0];
				String extra = null;
				if(name.contains("%arena%")) {
					Arena a = null;
					if(game.getState() == GameState.VOTING) {
						List<Arena> arenas = game.getVotingPhrase().getArenas();
						
						if(i >= arenas.size()) {
							a = arenas.get(arenas.size() - 1);
						} else {
							a = arenas.get(i);
						}
					} else {
						a = game.getCurrentArena();
					}

					if(a != null) {
						extra = a.getName();
						name = name.replace("%arena%", a.getName());
						
						if(name.length() > 48)
							name = name.substring(0, 48);
					}
				}
				
				String regex = split[1];
				String scoreName = name;
				
				Team team = null;
				if(name.length() > 16) {
					team = scoreboard.registerNewTeam(Integer.valueOf(tName).toString());
					
					team.setPrefix(name.substring(0, 16));
					
					if(name.length() > 32) {
						scoreName = name.substring(16, 32);
						team.setSuffix(name.substring(32));
					} else {
						scoreName = name.substring(16);
					}
					
					tName++;
				}
				Score s = sidebar.getScore(Bukkit.getOfflinePlayer(scoreName));
				
				if(team != null)
					team.addPlayer(s.getPlayer());
				
				s.setScore(-1);
				Sscore.add(new CustomScore(s, name, regex, team, extra));
				
				
			} catch(Exception e) {
				e.printStackTrace();
				System.err.println("[SurvivalGames] Cannot load Scoreboard phase " + title + " - Mabye this is the reason: " + e.toString());
				return null;
			}
		}
		return scoreboard;
	}
	
	public List<CustomScore> getScores() {
		return Sscore;
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public ScoreboardPhase clone() {
		return new ScoreboardPhase(title, scores);
	}

}
