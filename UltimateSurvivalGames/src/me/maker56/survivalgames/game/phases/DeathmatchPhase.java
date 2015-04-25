package me.maker56.survivalgames.game.phases;

import java.util.Collections;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class DeathmatchPhase {
	
	private int time = 600, starttime = time;
	private BukkitTask task;
	private Game game;
	
	public DeathmatchPhase(Game game) {
		this.game = game;
	}
	
	public void load() {
		game.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.DEATHMATCH));
		start();
	}
	
	public void start() {
		game.sendMessage(MessageHandler.getMessage("game-deathmatch-start"));
		game.setState(GameState.DEATHMATCH);
		
		List<Location> spawns = game.getCurrentArena().getDeathmatchSpawns();
		int i = 0;
		for(User user : game.getUsers()) {
			if(i >= spawns.size())
				i = 0;
			user.getPlayer().teleport(spawns.get(i));
			i++;
		}
		
		Location suloc = spawns.get(0);
		for(SpectatorUser su : game.getSpecators()) {
			su.getPlayer().teleport(suloc);
			Vector v = new Vector(0, 2, 0);
			v.multiply(1.25);
			su.getPlayer().getLocation().setDirection(v);
		}
		
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			public void run() {
				if(time == 60) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout-warning"));
				}
				
				
				if(time % 60 == 0 && time != 0) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Util.getFormatedTime(time)));
				} else if(time % 10 == 0 && time < 60 && time > 10) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Util.getFormatedTime(time)));
				} else if(time <= 10 && time > 0) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Util.getFormatedTime(time)));
				} else if(time == 0) {
					Arena a = game.getCurrentArena();
					User user = null;
					
					if(a.isDomeEnabled()) {
						double nearest = a.getDomeRadius() + 50;
						for(User u : game.getUsers()) {
							double distance = a.domeDistance(u.getPlayer().getLocation());
							if(distance <= nearest) {
								nearest = distance;
								user = u;
							}
						}
					} else {
						Collections.shuffle(game.getUsers());
						user = game.getUsers().get(0);
					}
					
					for(User u : game.getUsers()) {
						if(!u.equals(user)) {
							u.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 10000000, 3));
						}
					}
					
					
				}
				

				game.updateScoreboard();
				time--;
			}
		}, 0L, 20L);
	}
	
	public int getTime() {
		return time;
	}
	
	
	public void cancelTask() {
		if(task != null)
			task.cancel();
	}
	
	public int getStartTime() {
		return starttime;
	}

}
