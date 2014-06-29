package me.maker56.survivalgames.game.phases;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;

public class FinishPhase {
	
	private Game game;
	private int time = 10;
	private BukkitTask task;
	
	public FinishPhase(Game game) {
		this.game = game;
	}
	
	public void load() {
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			public void run() {
				if(time == 0) {
					task.cancel();
					game.end();
					return;
				} else {
					if(game.getUsers().size() > 0) {
						Util.shootRandomFirework(game.getUsers().get(0).getPlayer().getLocation(), 1);
					}
				}
				
				if(time == 5) {
					game.sendMessage(MessageHandler.getMessage("game-end").replace("%0%", Util.getFormatedTime(time)));
				}
				time--;
			}
		}, 20L, 20L);
	}
	
	public void cancel() {
		if(task != null) {
			task.cancel();
		}
	}

}
