package me.maker56.survivalgames.game.phrase;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.chest.Chest;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class IngamePhrase {

	private Game game;
	public BukkitTask task;
	private boolean running;
	private int time;
	private UserManager um = SurvivalGames.userManger;
	private boolean braodcastWin = SurvivalGames.instance.getConfig().getBoolean("broadcast-win");
	
	private boolean lightningOD = SurvivalGames.instance.getConfig().getBoolean("Lightning.on-death");
	private boolean lightningFP = SurvivalGames.instance.getConfig().getBoolean("Lightning.on-few-players");
	private int lightningFPc = SurvivalGames.instance.getConfig().getInt("Lightning.few-players");
	private int lightningFPt = SurvivalGames.instance.getConfig().getInt("Lightning.few-players-time");
	private BukkitTask lTask;
	
	public boolean grace = false;
	private int period; 
	
	private BukkitTask deathmatch, chestrefill, gracetask;
	
	public IngamePhrase(Game game) {
		this.game = game;
		this.period = game.getCurrentArena().getGracePeriod();
		this.time = game.getCurrentArena().getAutomaticlyDeathmatchTime();
		start();
	}
	
	public void start() {
		game.getCurrentArena().getMinimumLocation().getWorld().setTime(0);
		
		if(game.getCurrentArena().chestRefill()) {
			chestrefill = Bukkit.getScheduler().runTaskLater(SurvivalGames.instance, new Runnable() {
				public void run() {
					long time = game.getCurrentArena().getMinimumLocation().getWorld().getTime();
					if(time >= 18000 && time <= 18200) {
						for(Chest c : game.getRegisteredChests()) {
							c.getLocation().getWorld().playEffect(c.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
							c.getLocation().getWorld().playSound(c.getLocation(), Sound.LEVEL_UP, 4.0F, 1.0F);
						}
						game.getRegisteredChests().clear();
						game.sendMessage(MessageHandler.getMessage("game-chestrefill"));
					}
				}
			}, 18001);
		}
		
		game.setState(GameState.INGAME);
		game.sendMessage(MessageHandler.getMessage("game-start").replace("%0%", Integer.valueOf(game.getPlayingUsers()).toString()));
		running = true;
		
		if(period != 0) {
			game.sendMessage(MessageHandler.getMessage("game-grace-period").replace("%0%", Integer.valueOf(period).toString()));
			grace = true;
			
			gracetask = Bukkit.getScheduler().runTaskLater(SurvivalGames.instance, new Runnable() {
				public void run() {
					game.sendMessage(MessageHandler.getMessage("game-grace-period-ended"));
					grace = false;
					startTask();
				}
			}, period * 20);
		} else {
			startTask();
		}
	}
	
	private void startTask() {
		if(lightningFP) {
			startLightningTask();
		}
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			public void run() {
				
				if(game.getCurrentArena().isDeathmatchEnabled()) {
					if(time % 600 == 0 && time != 0) {
						game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-minutes").replace("%0%", Integer.valueOf(time / 60).toString()));
					} else if(time < 301 && time % 300 == 0 && time != 0) {
						game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-minutes").replace("%0%", Integer.valueOf(time / 60).toString()));
					} else if(time < 60 && time % 10 == 0 && time > 10) {
						game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-seconds").replace("%0%", Integer.valueOf(time).toString()));
					} else if(time <= 10 && time > 0) {
						game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-little").replace("%0%", Integer.valueOf(time).toString()));
					} else if(time == 0) {
						cancelTask();
						cancelLightningTask();
						game.startDeathmatch();
						return;
					}
				}
				
				time--;
			}
		}, 0L, 20L);
	}
	
	public void killUser(User user, User killer, boolean leave) {
		int remain = game.getUsers().size() - 1;
		
		if(leave) {
			game.sendMessage(MessageHandler.getMessage("game-player-left").replace("%0%", user.getName()));
		} else {
			if(killer == null) {
				game.sendMessage(MessageHandler.getMessage("game-player-die-damage").replace("%0%", user.getName()));
			} else {
				game.sendMessage(MessageHandler.getMessage("game-player-die-killer").replace("%0%", user.getName()).replace("%1%", killer.getName()));
				double killMoney = game.getCurrentArena().getMoneyOnKill();
				if(killMoney > 0 && SurvivalGames.econ != null) {
					SurvivalGames.econ.depositPlayer(killer.getName(), killMoney);
					killer.sendMessage(MessageHandler.getMessage("arena-money-kill").replace("%0%", Double.valueOf(killMoney).toString()).replace("%1%", user.getName()));
				}
			}
		}
		
		game.sendMessage(MessageHandler.getMessage("game-remainplayers").replace("%0%", Integer.valueOf(remain).toString()));
		if(lightningOD)
			user.getPlayer().getWorld().strikeLightningEffect(user.getPlayer().getLocation());
		
		for(ItemStack is : user.getPlayer().getInventory().getContents()) {
			if(is == null || is.getType() == Material.AIR)
				continue;
			user.getPlayer().getWorld().dropItemNaturally(user.getPlayer().getLocation(), is);
		}
		
		for(ItemStack is : user.getPlayer().getInventory().getArmorContents()) {
			if(is == null || is.getType() == Material.AIR)
				continue;
			user.getPlayer().getWorld().dropItemNaturally(user.getPlayer().getLocation(), is);
		}
		
		um.leaveGame(user.getPlayer());
		
		if(remain == 1) {
			User winner = game.getUsers().get(0);
			
			if(braodcastWin) {
				Bukkit.broadcastMessage(MessageHandler.getMessage("game-win").replace("%0%", winner.getName()).replace("%1%", game.getCurrentArena().getName()).replace("%2%", game.getName()));
			}
			
			winner.sendMessage(MessageHandler.getMessage("game-win-winner-message").replace("%0%", game.getCurrentArena().getName()));
			double winMoney = game.getCurrentArena().getMoneyOnWin();
			if(winMoney > 0 && SurvivalGames.econ != null) {
				SurvivalGames.econ.depositPlayer(winner.getName(), winMoney);
				winner.sendMessage(MessageHandler.getMessage("arena-money-win").replace("%0%", Double.valueOf(winMoney).toString()));
			}
			
			um.leaveGame(winner.getPlayer());
			game.end();
		} else if(remain == game.getCurrentArena().getPlayerDeathmatchAmount())
			startDeathmatchTask();
	}
	
	public void startDeathmatchTask() {
		if(game.getDeathmatch() != null)
			return;
		
		cancelTask();
		
		deathmatch = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			int time = 60;
			public void run() {
				
				if(time % 10 == 0 && time > 10) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-big-seconds").replace("%0%", Integer.valueOf(time).toString()));
				} else if(time <= 10 && time > 0) {
					game.sendMessage(MessageHandler.getMessage("game-deathmatch-cooldown-little").replace("%0%", Integer.valueOf(time).toString()));
				} else if(time == 0) {
					cancelDeathmatchTask();
					cancelLightningTask();
					game.startDeathmatch();
					return;
				}
				
				time--;
			}
		}, 0L, 20L);
	}
	
	public void startLightningTask() {
		lTask = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			public void run() {
				if(game.getPlayingUsers() <= lightningFPc && !game.getCurrentArena().isDeathmatchEnabled()) {
					for(User user : game.getUsers()) {
						user.getPlayer().getWorld().strikeLightningEffect(user.getPlayer().getLocation());
					}
				}
			}
		}, lightningFPt * 20L, lightningFPt * 20L);
	}
	
	public void cancelLightningTask() {
		if(lTask != null)
			lTask.cancel();
	}
	
	public void cancelTask() {
		if(task != null)
			task.cancel();
		if(chestrefill != null)
			chestrefill.cancel();
		if(gracetask != null)
			gracetask.cancel();
	}
	
	public void cancelDeathmatchTask() {
		if(deathmatch != null)
			deathmatch.cancel();
	}
	
	public boolean isRunning() {
		return running;
	}
}
