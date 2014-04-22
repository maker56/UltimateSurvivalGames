package me.maker56.survivalgames.game.phrase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.User;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class VotingPhrase {
	
	private Game game;
	private BukkitTask task;
	private boolean running = false;
	private int time;
	
	public ArrayList<Arena> voteArenas = new ArrayList<Arena>();
	
	
	public VotingPhrase(Game game) {
		this.game = game;
		time = game.getLobbyTime();
		game.setState(GameState.VOTING);
		start();
	}
	
	public void start() {
		running = true;
		chooseRandomArenas();
		
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
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


	public Arena vote(String player, int id) {
		try {
			game.getVotedUsers().add(player);
			return voteArenas.get(id - 1);
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
