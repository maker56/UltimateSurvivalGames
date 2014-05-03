package me.maker56.survivalgames.game.phrase;

import java.util.Collections;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class DeathmatchPhrase {
	
	private int time = 600;
	private BukkitTask task;
	private Game game;
	
	public DeathmatchPhrase(Game game) {
		this.game = game;
		start();
	}
	
	public void start() {
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
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Integer.valueOf(time).toString()));
				} else if(time % 10 == 0 && time < 60 && time > 10) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Integer.valueOf(time).toString()));
				} else if(time <= 10 && time > 0) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-timeout").replace("%0%", Integer.valueOf(time).toString()));
				} else if(time == 0) {
					
					List<User> users = game.getUsers();
					Collections.shuffle(users);
					
					for(int i = 1; i < users.size(); i++) {
						game.getIngamePhrase().killUser(users.get(i), users.get(0), false);
					}
					
				}
				
				time--;
			}
		}, 0L, 20L);
	}
	
	
	
	public void cancelTask() {
		if(task != null)
			task.cancel();
	}

}
