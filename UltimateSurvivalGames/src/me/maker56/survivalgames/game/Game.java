package me.maker56.survivalgames.game;

import java.util.ArrayList;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.arena.chest.Chest;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.phrase.CooldownPhrase;
import me.maker56.survivalgames.game.phrase.DeathmatchPhrase;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.game.phrase.ResetPhrase;
import me.maker56.survivalgames.game.phrase.VotingPhrase;
import me.maker56.survivalgames.user.User;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Game {
	
	private String name;
	private Location lobby;
	private boolean voting;
	private int maxVotingArenas;
	private List<Arena> arenas;
	private int reqplayers, maxplayers;
	private GameState state;
	private int lobbytime, cooldown = 30;
	
	private VotingPhrase votingPhrase;
	private CooldownPhrase cooldownPhrase;
	private IngamePhrase ingamePhrase;
	private DeathmatchPhrase deathmatchPhrase;
	
	private Arena arena;
	private List<User> users = new ArrayList<>();
	private List<Chest> chests = new ArrayList<>();
	private List<String> rChunks = new ArrayList<>();
	public ArrayList<String> voted = new ArrayList<>();
	
	public Game(String name, Location lobby, boolean voting, int lobbytime, int maxVotingArenas, int reqplayers, List<Arena> arenas) {
		this.name = name;
		this.lobby = lobby;
		this.voting = voting;
		this.lobbytime = lobbytime;
		this.maxVotingArenas = maxVotingArenas;
		this.arenas = arenas;
		
		if(reqplayers < 2) {
			reqplayers = 2;
		}
		
		this.reqplayers = reqplayers;
		this.maxplayers = getFewestArena().getSpawns().size();
		setState(GameState.WAITING);
	}
	
	public List<String> getVotedUsers() {
		return voted;
	}
	
	public void join(User user) {
		users.add(user);
		Player p = user.getPlayer();
		
		if(arenas.size() == 1) {
			Arena arena = arenas.get(0);
			for(int i = 0; i < arena.getSpawns().size(); i++) {
				if(!hasUserIndex(i)) {
					p.teleport(arena.getSpawns().get(i));
					user.setSpawnIndex(i);
				}
			}
		} else if(getState() == GameState.COOLDOWN) {
			for(int i = 0; i < getCurrentArena().getSpawns().size(); i++) {
				if(!hasUserIndex(i)) {
					p.teleport(getCurrentArena().getSpawns().get(i));
					user.setSpawnIndex(i);
				}
			}
		} else {
			p.teleport(lobby);
		}
		user.clear();
		
		sendMessage(MessageHandler.getMessage("join-success").replace("%0%", p.getName()).replace("%1%", Integer.valueOf(users.size()).toString()).replace("%2%", Integer.valueOf(maxplayers).toString()));
		SurvivalGames.signManager.updateSigns();
		checkForStart();
	}
	
	public void leave(User user) {
		users.remove(user);
		checkForCancelStart();
		SurvivalGames.signManager.updateSigns();
	}
	
	public void kickall() {
		if(users.size() != 0) {
			int size = users.size();
			for(int i = 0; i < size; i++) {
				try {
					SurvivalGames.userManger.leaveGame(users.get(0).getPlayer());
				} catch(IndexOutOfBoundsException e) {
					break;
				}
			}
		}
	}
	
	public void end() {
		new ResetPhrase(this);
	}
	
	public DeathmatchPhrase getDeathmatch() {
		return deathmatchPhrase;
	}
	
	public void startDeathmatch() {
		deathmatchPhrase = new DeathmatchPhrase(this);
	}
	
	public void startIngame() {
		ingamePhrase = new IngamePhrase(this);
	}
	
	public void startCooldown(Arena arena) {
		cooldownPhrase = new CooldownPhrase(this, arena);
	}
	
	public void checkForStart() {
		if(users.size() == reqplayers) {
			if(getArenas().size() == 1) {
				cooldownPhrase = new CooldownPhrase(this, getArenas().get(0));
			} else {
				if(cooldownPhrase != null) {
					cooldownPhrase = new CooldownPhrase(this, getArenas().get(0));
				} else {
					votingPhrase = new VotingPhrase(this);
				}
			}
		}
	}
	
	public void checkForCancelStart() {
		if(state != GameState.VOTING && state != GameState.COOLDOWN)
			return;
		
		if(users.size() < reqplayers) {
			if(cooldownPhrase != null && !cooldownPhrase.isRunning()) {
				cooldownPhrase.cancelTask();
				sendMessage(MessageHandler.getMessage("game-start-canceled"));
			} else if(votingPhrase != null && !votingPhrase.isRunning()){
				votingPhrase.cancelTask();
				sendMessage(MessageHandler.getMessage("game-start-canceled"));
			}
			
		}
	}
	
	public IngamePhrase getIngamePhrase() {
		return ingamePhrase;
	}
	
	public VotingPhrase getVotingPhrase() {
		return votingPhrase;
	}
	
	public CooldownPhrase getCooldownPhrase() {
		return cooldownPhrase;
	}
	
	public Arena getFewestArena() {
		int slot = Integer.MAX_VALUE;
		Arena arena = null;
		
		for(Arena a : arenas) {
			if(a.getSpawns().size() < slot) {
				slot = a.getSpawns().size();
				arena = a;
			}
		}
		
		return arena;
	}
	
	public Arena getArena(String name) {
		for(Arena arena : arenas) {
			if(arena.getName().equals(name)) {
				return arena;
			}
		}
		return null;
	}
	
	public void setCurrentArena(Arena arena) {
		this.arena = arena;
	}
	
	public Arena getCurrentArena() {
		return arena;
	}
	
	public void setState(GameState state) {
		this.state = state;
		if(SurvivalGames.signManager != null)
			SurvivalGames.signManager.updateSigns();
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public int getPlayingUsers() {
		return users.size();
	}
	
	public String getName() {
		return name;
	}
	
	public Location getLobby() {
		return lobby;
	}
	
	public int getLobbyTime() {
		return lobbytime;
	}
	
	public int getRequiredPlayers() {
		return reqplayers;
	}
	
	public int getMaximumPlayers() {
		return maxplayers;
	}
	
	public boolean isVotingEnabled() {
		return voting;
	}
	
	public int getMaxVotingArenas() {
		return maxVotingArenas;
	}
	
	public List<Arena> getArenas() {
		return arenas;
	}
	
	public GameState getState() {
		return state;
	}
	
	public int getCooldownTime() {
		return cooldown;
	}
	
	public void sendMessage(String message) {
		for(User user : users) {
			user.sendMessage(message);
		}
	}
	
	public boolean hasUserIndex(int index) {
		for(User user : users) {
			if(user.getSpawnIndex() == index) {
				return true;
			}
		}
		return false;
	}
	
	public void registerChest(Chest chest) {
		chests.add(chest);
	}
	
	public List<Chest> getRegisteredChests() {
		return chests;
	}
	
	public Chest getChest(Location loc) {
		for(Chest chest : chests) {
			if(chest.getLocation().equals(loc))
				return chest;
		}
		return null;
	}
	
	public boolean isChestRegistered(Location loc) {
		for(Chest chest : chests) {
			if(chest.getLocation().equals(loc))
				return true;
		}
		return false;
	}
	
	public List<String> getChunksToReset() {
		return rChunks;
	}
	
	public String getAlivePlayers() {
		String s = new String();
		List<User> users = getUsers();
		for(int i = 0; i < users.size(); i++) {
			s += "§e" + users.get(i).getName();
			if(i != users.size() - 1)
				s += "§7, ";
		}
		return s;
	}

}
