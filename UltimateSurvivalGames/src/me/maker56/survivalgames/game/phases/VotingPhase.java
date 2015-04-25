package me.maker56.survivalgames.game.phases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.User;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class VotingPhase {
	
	// STATIC VARIABLES
	private static ItemStack voteItem, arenaItem;
	private static String title;
	
	public static ItemStack getVotingOpenItemStack() {
		return voteItem;
	}
	
	public static String getVotingInventoryTitle() {
		return title;
	}
	
	public static void reinitializeDatabase() {
		voteItem = Util.parseItemStack(SurvivalGames.instance.getConfig().getString("Voting.Item"));
		arenaItem = Util.parseItemStack(SurvivalGames.instance.getConfig().getString("Voting.ArenaItem"));
		title = SurvivalGames.instance.getConfig().getString("Voting.InventoryTitle");
		if(title.length() > 32) {
			title = title.substring(0, 32);
		}
		title = ChatColor.translateAlternateColorCodes('&', title);
	}
	
	private Game game;
	private BukkitTask task;
	private boolean running = false;
	private int time;
	
	public ArrayList<Arena> voteArenas = new ArrayList<Arena>();
	private Inventory voteInventory;
	
	
	public VotingPhase(Game game) {
		reinitializeDatabase();
		this.game = game;

	}
	
	public void load() {
		game.setState(GameState.VOTING);
		chooseRandomArenas();
		game.setScoreboardPhase(SurvivalGames.getScoreboardManager().getNewScoreboardPhase(GameState.VOTING));
		time = game.getLobbyTime();
		start();
	}
	
	public void start() {
		running = true;
		
		if(game.isVotingEnabled()) {
			if(voteItem != null) {
				generateInventory();
				for(User user : game.getUsers()) {
					equipPlayer(user);
				}
			}
		}
		
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			public void run() {
				
				for(User user : game.getUsers()) {
					user.getPlayer().setLevel(time);
					user.getPlayer().setExp(Util.getExpPercent((float)time, (float)game.getLobbyTime()));
				}
				
				if(time % 10 == 0 && time > 10) {
					game.sendMessage(MessageHandler.getMessage("game-voting-cooldown").replace("%0%", Util.getFormatedTime(time)));
				}
				
				if(time % 15 == 0 && time != 0) {
					sendVoteMessage();
				} else if(time <= 10 && time > 0){
					game.sendMessage(MessageHandler.getMessage("game-voting-cooldown").replace("%0%", Util.getFormatedTime(time)));
				} else if(time == 0) {
					for(User user : game.getUsers()) {
						user.getPlayer().getInventory().setItem(1, null);
						user.getPlayer().updateInventory();
					}
					
					task.cancel();
					running = false;
					time = game.getLobbyTime();
					game.sendMessage(MessageHandler.getMessage("game-voting-end"));
					Arena winner = getMostVotedArena();
					winner.getSpawns().get(0).getWorld().setTime(0);
					
					game.startCooldown(winner);
					for(Arena arena : voteArenas) {
						arena.setVotes(0);
					}
					voteArenas.clear();
					game.getVotedUsers().clear();
					return;
				}
				
				game.updateBossBarMessage();
				game.updateScoreboard();
				time--;

			}
		}, 0L, 20L);
	}
	
	public Inventory getVotingInventory() {
		return voteInventory;
	}
	
	public void equipPlayer(User user) {
		user.getPlayer().getInventory().setItem(1, voteItem);
		user.getPlayer().updateInventory();
	}
	
	public List<Arena> getArenas() {
		return voteArenas;
	}
	
	public void generateInventory() {
		int arenas = voteArenas.size();
		int size = 9;
		
		if(arenas >= 9) {
			size = 9;
		} else if(arenas >= 18) {
			size = 18;
		} else if(arenas >= 27) {
			size = 27;
		} else if(arenas >= 36) {
			size = 36;
		} else if(arenas >= 45) {
			size = 45;
		} else if(arenas >= 54) {
			size = 54;
		}
		
		voteInventory = Bukkit.createInventory(null, size, title);
		
		int place = size / arenas; 
		int c = 0;
		
		for(int i = 0; i < size; i++) {
			Arena a;
			try {
				a = voteArenas.get(i);
			} catch(IndexOutOfBoundsException e) {
				break;
			}
			
			if(a == null)
				break;
			
			ItemStack is = arenaItem.clone();
			ItemMeta im = is.getItemMeta();
			im.setDisplayName((i + 1) + ". §e§l" + a.getName());
			is.setItemMeta(im);
			
			voteInventory.setItem(c, is);
			c += place;
		}
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public Arena getMostVotedArena() {
		Arena mostVoted = null;

		int votes = 0;
		for (Arena arena : voteArenas) {
			if (arena.getVotes() > votes) {
				votes = arena.getVotes();
				mostVoted = arena;
			}
		}
		
		if(mostVoted == null)
			mostVoted = voteArenas.get(0);
		return mostVoted;
	}
	
	public boolean canVote(String player) {
		return !game.getVotedUsers().contains(player);
	}


	public Arena vote(Player p, int id) {
		try {
			Arena a = voteArenas.get(id - 1);
			if(a != null) {
				int amount = PermissionHandler.getVotePower(p);
				a.setVotes(a.getVotes() + amount);
				game.getVotedUsers().add(p.getName());
				p.sendMessage(MessageHandler.getMessage("game-success-vote").replace("%0%", a.getName()));
				if(amount > 1) {
					p.sendMessage(MessageHandler.getMessage("game-extra-vote").replace("%0%", Integer.valueOf(amount).toString()));
				}
				game.updateScoreboard();
			}
				
			return a;
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	private void sendVoteMessage() {
		if(game.isVotingEnabled()) {
			if(voteItem == null) {
				game.sendMessage(MessageHandler.getMessage("game-vote"));
			}
			
			int i = 1;
			for(Arena arena : voteArenas) {
				game.sendMessage(new ComponentBuilder("§3" + i + "§7. §6" + arena.getName() + " §7(§e" + arena.getVotes() + "§7)")
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to vote for arena " + arena.getName()).create()))
				.event(new ClickEvent(Action.RUN_COMMAND, "/sg vote " + i)).create());
				i++;
			}
		}
	}
	
	private void chooseRandomArenas() {
		List<Arena> arenas = game.getArenas();

		voteArenas.clear();
		Collections.shuffle(arenas);

		int i = 0;
		for(Arena a : arenas) {
			if(i == game.getMaxVotingArenas())
				break;
			voteArenas.add(a);
			i++;
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void cancelTask() {
		if(task != null)
			task.cancel();
		running = false;
		voteArenas.clear();
		time = game.getLobbyTime();
		return;
	}

}
