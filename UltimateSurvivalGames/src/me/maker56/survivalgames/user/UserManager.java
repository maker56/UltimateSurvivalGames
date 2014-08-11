package me.maker56.survivalgames.user;

import java.util.Iterator;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.potion.PotionEffect;

public class UserManager {
	
	private GameManager gm = SurvivalGames.gameManager;
	
	// START SPECTATOR
	
	public void leaveGame(SpectatorUser su) {
		Game g = su.getGame();
		for(User u : g.getUsers()) {
			u.getPlayer().showPlayer(su.getPlayer());
		}
		g.leaveSpectator(su);
		if(g.getScoreboardPhase() != null) {
			su.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
		setState(su.getPlayer(), su);
	}
	
	public void joinGameAsSpectator(Player p, String gamename) {
		if(!SurvivalGames.instance.getConfig().getBoolean("Spectating.Enabled")) {
			p.sendMessage(MessageHandler.getMessage("spectator-disabled"));
			return;
		}
		
		if(!PermissionHandler.hasPermission(p, Permission.SPECTATE)) {
			p.sendMessage(MessageHandler.getMessage("no-permission"));
			return;
		}
		
		if(isSpectator(p.getName()) || isPlaying(p.getName())) {
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
		
		int max = SurvivalGames.instance.getConfig().getInt("Spectating.Max-Spectators-Per-Arena", 8);
		if(g.getSpecators().size() >= max) {
			p.sendMessage(MessageHandler.getMessage("spectator-full").replace("%0%", Integer.valueOf(max).toString()));
			return;
		}
		
		GameState state = g.getState();
		
		if(state == GameState.VOTING || state == GameState.WAITING || state == GameState.COOLDOWN || state == GameState.RESET) {
			p.sendMessage(MessageHandler.getMessage("spectator-game-running"));
			return;
		}
		
		g.joinSpectator(new SpectatorUser(p, g));
		return;
	}
	
	// END SPECTATOR
	
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
			if(SurvivalGames.instance.getConfig().getBoolean("Spectating.Enabled")) {
				joinGameAsSpectator(p, gamename);
			} else {
				p.sendMessage(MessageHandler.getMessage("join-game-running"));
			}
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
			SpectatorUser su = getSpectator(p.getName());
			if(su != null) {
				leaveGame(su);
				return;
			}
			p.sendMessage(MessageHandler.getMessage("leave-not-playing"));
			return;
		}
		
		if(p.getVehicle() != null)
			p.getVehicle().setPassenger(null);
		if(p.getPassenger() != null) {
			p.setPassenger(null);
		}
		
		final User user = getUser(p.getName());
		user.clear();
		Game game = user.getGame();
		
		for(SpectatorUser su : game.getSpecators()) {
			p.showPlayer(su.getPlayer());
		}
		
		
		if(game.getState() == GameState.WAITING || game.getState() == GameState.VOTING || game.getState() == GameState.COOLDOWN)
			game.sendMessage(MessageHandler.getMessage("game-leave").replace("%0%", p.getName()).replace("%1%", Integer.valueOf(game.getPlayingUsers() - 1).toString()).replace("%2%", Integer.valueOf(game.getMaximumPlayers()).toString()));
		game.leave(user);
		
		if(game.getScoreboardPhase() != null) {
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
		
		
		if(p.isDead()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.instance, new Runnable() {
				public void run() {
					setState(p, user);
				}
			}, 1);
		} else {
			setState(p, user);
		}
	}
	
	public void setState(Player p, UserState state) {
		p.teleport(state.getLocation());
		p.setFallDistance(state.getFallDistance());
		p.setGameMode(state.getGameMode());
		p.setAllowFlight(state.getAllowFlight());
		p.setFlying(state.isFlying());
		p.setLevel(state.getLevel());
		p.setExp(state.getExp());
		p.setFireTicks(state.getFireTicks());
		p.setMaxHealth(state.getMaxHealth());
		p.setHealth(state.getHealth());
		p.setFoodLevel(state.getFoodLevel());
		p.setWalkSpeed(state.getWalkSpeed());
		p.setFlySpeed(state.getFlySpeed());
		
		for (Iterator<PotionEffect> i = p.getActivePotionEffects().iterator(); i.hasNext();) {
			p.removePotionEffect(i.next().getType());
		}
		p.addPotionEffects(state.getActivePotionEffects());
		p.getInventory().setHeldItemSlot(state.getHeldItemSlot());
		
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
	
	public boolean isSpectator(String name) {
		return getSpectator(name) != null;
	}
	
	public SpectatorUser getSpectator(Player p) {
		return getSpectator(p.getName());
	}
	
	public SpectatorUser getSpectator(String name) {
		for(Game game : gm.getGames()) {
			for(SpectatorUser su : game.getSpecators()) {
				if(su.getName().equals(name))
					return su;
			}
		}
		return null;
	}
	
	public boolean isPlaying(String name) {
		return getUser(name) != null;
	}
	
	public User getUser(Player player) {
		return getUser(player.getName());
	}
	
	public User getUser(String name) {
		for(Game game : gm.getGames()) {
			User u = game.getUser(name);
			if(u != null)
				return u;
		}
		return null;
	}

}
