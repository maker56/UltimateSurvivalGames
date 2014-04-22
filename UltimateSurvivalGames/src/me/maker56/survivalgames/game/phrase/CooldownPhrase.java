package me.maker56.survivalgames.game.phrase;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.User;

public class CooldownPhrase {
	
	private Game game;
	private BukkitTask task;
	private boolean running;
	private int time;
	private Arena arena;
	
	public CooldownPhrase(Game game, Arena arena) {
		this.game = game;
		game.setCurrentArena(arena);
		this.arena = arena;
		time = game.getCooldownTime();
		game.setState(GameState.COOLDOWN);
		start();
	}
	
	public void start() {
		running = true;
		
		if(game.getArenas().size() > 1) {
			for(int i = 0; i < game.getUsers().size(); i++) {
				User user = game.getUsers().get(i);
				
				user.setSpawnIndex(i);
				user.getPlayer().teleport(arena.getSpawns().get(i));
				
				for(User ouser : game.getUsers()) {
					user.getPlayer().showPlayer(ouser.getPlayer());
				}
			}
		}
		
		
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			public void run() {
				
				for(User user : game.getUsers()) {
					user.getPlayer().setLevel(time);
					user.getPlayer().setExp(0);
				}
				
				if(time == 27) {
					game.sendMessage(MessageHandler.getMessage("prefix") + "MAPINFO §7- §eName: §b" + arena.getName());
				}
				
				if(time % 5 == 0 && time != 10 && time != 5 && time != 0) {
					game.sendMessage(MessageHandler.getMessage("game-cooldown-big").replace("%0%", Integer.valueOf(time).toString()));
				} else if(time <= 10 && time > 0) {
					game.sendMessage(MessageHandler.getMessage("game-cooldown-little").replace("%0%", Integer.valueOf(time).toString()));
					if(time <= 5) {
						for(User user : game.getUsers()) {
							user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.NOTE_STICKS, 8.0F, 1.0F);
						}
					}
				} else if(time == 0) {
					for(User user : game.getUsers()) {
						user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.NOTE_PLING, 8.0F, 1.0F);
					}
					task.cancel();
					running = false;
					time = game.getCooldownTime();
					game.startIngame();
					return;
				}
				time--;
			}
		}, 0L, 20L);
	}
	
	public void cancelTask() {
		if(task != null)
			task.cancel();
		running = false;
		time = game.getCooldownTime();
	}
	
	public boolean isRunning() {
		return running;
	}

}
