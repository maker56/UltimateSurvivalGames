package me.maker56.survivalgames.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;

public class UserManager {
	
	private GameManager gm = SurvivalGames.gameManager;
	
	public void joinGame(Player p, String gamename) {
		if(!PermissionHandler.hasPermission(p, Permission.JOIN)) {
			p.sendMessage(MessageHandler.getMessage("no-permission"));
			return;
		}
		
		if(isPlaying(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("join-already-playing"));
			return;
		}
		
		if(p.getVehicle() != null) {
			p.sendMessage(MessageHandler.getMessage("join-vehicle"));
			return;
		}
		
		Game g = gm.getGame(gamename);
		
		if(g == null) {
			p.sendMessage(MessageHandler.getMessage("join-unknown-game").replace("%0%", gamename));
			return;
		}
		
		GameState state = g.getState();
		
		if(state != GameState.VOTING && state != GameState.WAITING && state != GameState.COOLDOWN) {
			p.sendMessage(MessageHandler.getMessage("join-game-running"));
			return;
		}
		
		if(g.getUsers().size() >= g.getMaximumPlayers()) {
			User kick = PermissionHandler.canJoin(p, g);
			if(kick != null) {
				kick.sendMessage(MessageHandler.getMessage("fulljoin-kick"));
				leaveGame(kick.getPlayer());
			} else {
				p.sendMessage(MessageHandler.getMessage("join-game-full"));
				return;
			}
		}
		
		User user = new User(p, g);
		g.join(user);
		return;
	}
	

	public void leaveGame(final Player p) {
		if(!isPlaying(p.getName())) {
			p.sendMessage(MessageHandler.getMessage("leave-not-playing"));
			return;
		}
		
		final User user = getUser(p.getName());
		user.clear();
		Game game = user.getGame();
		
		game.leave(user);
		if(p.isDead()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.instance, new Runnable() {
				public void run() {
					setState(p, user.getState());
				}
			}, 1);
		} else {
			setState(p, user.getState());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setState(Player p, UserState state) {
		p.teleport(state.getLocation());
		p.setFallDistance(state.getFallDistance());
		p.setGameMode(state.getGameMode());
		p.setAllowFlight(state.getAllowFlight());
		p.setFlying(state.isFlying());
		p.setLevel(state.getLevel());
		p.setExp(state.getExp());
		p.setHealth(state.getHealth());
		p.setFoodLevel(state.getFoodLevel());
		p.addPotionEffects(state.getActivePotionEffects());
		
		final String name = p.getName();
		final ItemStack[] contents = state.getContents();
		final ItemStack[] armorcontents = state.getArmorContents();
		
		try {
			Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.instance, new Runnable() {
				public void run() {
					Player fp = Bukkit.getPlayer(name);
					
					if(fp != null) {
						fp.getInventory().setContents(contents);
						fp.getInventory().setArmorContents(armorcontents);
						fp.updateInventory();
					}
				}
			}, 2L);
		} catch(IllegalPluginAccessException e) {
			p.getInventory().setContents(contents);
			p.getInventory().setArmorContents(armorcontents);
			p.updateInventory();
		}
	}
	
	public boolean isPlaying(String name) {
		for(Game game : gm.getGames()) {
			for(User user : game.getUsers()) {
				if(user.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public User getUser(String name) {
		for(Game game : gm.getGames()) {
			for(User user : game.getUsers()) {
				if(user.getName().equals(name)) {
					return user;
				}
			}
		}
		return null;
	}

}
