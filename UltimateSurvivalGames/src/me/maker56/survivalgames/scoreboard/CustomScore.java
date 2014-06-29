package me.maker56.survivalgames.scoreboard;

import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;

import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

public class CustomScore {
	
	private String regex, name, extra;
	private Team team;
	private Score score;
	
	public CustomScore(Score score, String name, String regex, Team team) {
		this(score, name, regex, team, null);
	}
	
	public CustomScore(Score score, String name, String regex, Team team, String extra) {
		this.regex = regex.toLowerCase();
		this.name = name;
		this.team = team;
		this.score = score;
		this.extra = extra;
	}
	
	public Score getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRegex() {
		return regex;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public void update(Game game) {
		score.setScore(getData(game));
	}
	
	public int getData(Game game) {
		int i = -1;
		
		switch (regex) {
		case "%requiredplayers%":
			i = game.getRequiredPlayers();
			break;
		case "%playing%":
			i = game.getPlayingUsers();
			break;
		case "%death%":
			i = game.getDeathAmount();
			break;
		case "%spectators%":
			i = game.getSpecators().size();
			break;
		}
		
		if(i == -1) {
			if(extra != null) {
				if(game.getState() == GameState.VOTING && regex.equals("%votecount%")) {
					i = game.getArena(extra).getVotes();
				}
			}
			
			if(regex.equals("%time%")) {
				switch (game.getState()) {
				case COOLDOWN:
					i = game.getCooldownPhrase().getTime();
					break;
				case VOTING:
					i = game.getVotingPhrase().getTime();
					break;
				case INGAME:
					i = game.getIngamePhrase().getTime();
					break;
				case DEATHMATCH:
					i = game.getDeathmatch().getTime();
					break;
				default:
					break;
				}
			}
		}
		
		return i;
	}

}
