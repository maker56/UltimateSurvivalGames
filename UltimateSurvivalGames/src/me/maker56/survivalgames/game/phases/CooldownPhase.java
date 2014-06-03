package me.maker56.survivalgames.game.phases;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.User;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

public class CooldownPhase {
	
	private Game game;
	private BukkitTask task;
	private boolean running;
	private int time;
	private Arena arena;
	
	public CooldownPhase(Game game, Arena arena) {
		this.game = game;
		this.arena = arena;
	}
	
	public void load() {
		game.setCurrentArena(arena);
		time = game.getCooldownTime();
		game.setState(GameState.COOLDOWN);
		game.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.COOLDOWN));
		game.updateScoreboard();
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
				
				if(time > 0 && (time % 5 == 0 || (time <= 10 && time > 0))) {
					game.sendMessage(MessageHandler.getMessage("game-cooldown").replace("%0%", Util.getFormatedTime(time)));
				}
				
				if(time <= 5 && time > 0) {
					for(User user : game.getUsers()) {
						user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.NOTE_STICKS, 8.0F, 1.0F);
					}
				} else if(time == 0) {
					for(User user : game.getUsers()) {
						user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.NOTE_PLING, 8.0F, 1.0F);
						user.clearInventory();
					}
					task.cancel();
					running = false;
					time = game.getCooldownTime();
					game.startIngame();
					return;
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
		running = false;
		time = game.getCooldownTime();
	}
	
	public boolean isRunning() {
		return running;
	}

}
