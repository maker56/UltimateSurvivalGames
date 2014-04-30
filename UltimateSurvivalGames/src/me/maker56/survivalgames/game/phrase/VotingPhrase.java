package me.maker56.survivalgames.game.phrase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.database.ConfigUtil;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.User;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class VotingPhrase {
	
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
		voteItem = ConfigUtil.parseItemStack(SurvivalGames.instance.getConfig().getString("Voting.Item"));
		arenaItem = ConfigUtil.parseItemStack(SurvivalGames.instance.getConfig().getString("Voting.ArenaItem"));
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
	
	
	public VotingPhrase(Game game) {
		reinitializeDatabase();
		this.game = game;
		time = game.getLobbyTime();
		game.setState(GameState.VOTING);
		chooseRandomArenas();
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
			@SuppressWarnings("deprecation")
			public void run() {
				
				for(User user : game.getUsers()) {
					user.getPlayer().setLevel(time);
					user.getPlayer().setExp(0);
				}
				
				if(game.isVotingEnabled()) {
					
					if(time % 10 == 0 && time != 10 && time != 0 && time != game.getLobbyTime()) {
						game.sendMessage(MessageHandler.getMessage("game-voting-cooldown-big").replace("%0%", Integer.valueOf(time).toString()));
					} else if(time % 15 == 0 && time != 0) {
						sendVoteMessage();
					} else if(time <= 10 && time > 0){
						game.sendMessage(MessageHandler.getMessage("game-voting-cooldown-little").replace("%0%", Integer.valueOf(time).toString()));
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
				} else {
					if(time % 10 == 0 && time != 10 && time != 0 && time != game.getLobbyTime()) {
						game.sendMessage(MessageHandler.getMessage("game-waiting-cooldown-big").replace("%0%", Integer.valueOf(time).toString()));
					} else if(time <= 10 && time > 0){
						game.sendMessage(MessageHandler.getMessage("game-waiting-cooldown-little").replace("%0%", Integer.valueOf(time).toString()));
					} else if(time == 0) {
						Arena winner = getMostVotedArena();
						if(winner == null) {
							time = 80;
							game.sendMessage(MessageHandler.getMessage("prefix") + "§cAn internal error occured.");
							return;
						}
						
						winner.getSpawns().get(0).getWorld().setTime(0);
						
						task.cancel();
						running = false;
						time = game.getLobbyTime();
						game.sendMessage(MessageHandler.getMessage("game-waiting-end"));
						
						
						game.startCooldown(winner);
						voteArenas.clear();
						game.getVotedUsers().clear();
						return;
					}
				}
				
				
				time--;

			}
		}, 0L, 20L);
	}
	
	public Inventory getVotingInventory() {
		return voteInventory;
	}
	
	@SuppressWarnings("deprecation")
	public void equipPlayer(User user) {
		user.getPlayer().getInventory().setItem(1, voteItem);
		user.getPlayer().updateInventory();
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
			}
				
			return a;
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	private void sendVoteMessage() {
		game.sendMessage(MessageHandler.getMessage("game-vote"));
		
		int i = 1;
		for(Arena arena : voteArenas) {
			game.sendMessage("§3" + i + "§7. §6" + arena.getName() + " §7(§e" + arena.getVotes() + "§7)");
			i++;
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
