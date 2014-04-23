package me.maker56.survivalgames.listener;

import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phrase.IngamePhrase;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
	
	private UserManager um = SurvivalGames.userManger;
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player p = event.getEntity();
		
		if(p.getKiller() instanceof Player) {
			Player pkiller = p.getKiller();
			
			if(um.isPlaying(p.getName())) {
				User user = um.getUser(p.getName());
				Game game = user.getGame();
				event.setDeathMessage(null);
				
				if(game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
					um.leaveGame(p);
					return;
				}
				
				IngamePhrase ip = game.getIngamePhrase();
				
				for(Entity entity : p.getWorld().getEntities()) {
					if(entity instanceof Projectile) {
						Projectile pr = (Projectile) entity;
						if(p.equals(pr.getShooter())) {
							pr.remove();
						}
					}
				}

				for(ItemStack is : event.getDrops()) {
					p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
				}
				event.getDrops().clear();
				
				if(um.isPlaying(pkiller.getName())) {
					ip.killUser(user, um.getUser(pkiller.getName()), false);
				} else {
					ip.killUser(user, null, false);
				}
			}
		} else {
			if(um.isPlaying(p.getName())) {
				User user = um.getUser(p.getName());
				Game game = user.getGame();
				event.setDeathMessage(null);
				
				if(game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
					um.leaveGame(p);
					return;
				}
				
				IngamePhrase ip = game.getIngamePhrase();
				
				for(ItemStack is : event.getDrops()) {
					p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
				}
				event.getDrops().clear();
				
				ip.killUser(user, null, false);
			}
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		for(Game game : SurvivalGames.gameManager.getGames()) {
			for(Arena arena : game.getArenas()) {
				if(event.getWorld().equals(arena.getMinimumLocation().getWorld()) && event.toWeatherState()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onHangingDestroy(HangingBreakEvent event) {
		for(Game game : SurvivalGames.gameManager.getGames()) {
			for(Arena arena : game.getArenas()) {
				if(arena.containsBlock(event.getEntity().getLocation()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerRightClick(PlayerInteractEntityEvent event) {
		for(Game game : SurvivalGames.gameManager.getGames()) {
			for(Arena arena : game.getArenas()) {
				if(arena.containsBlock(event.getRightClicked().getLocation()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerRightClick(EntityDamageEvent event) {
		if(event.getEntity() instanceof ItemFrame) {
			for(Game game : SurvivalGames.gameManager.getGames()) {
				for(Arena arena : game.getArenas()) {
					if(arena.containsBlock(event.getEntity().getLocation()))
						event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerWaterPlace(PlayerBucketEmptyEvent event) {
		for(Game game : SurvivalGames.gameManager.getGames()) {
			for(Arena arena : game.getArenas()) {
				if(arena.containsBlock(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerWaterPlace(PlayerBucketFillEvent event) {
		for(Game game : SurvivalGames.gameManager.getGames()) {
			for(Arena arena : game.getArenas()) {
				if(arena.containsBlock(event.getBlockClicked().getLocation()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.PHYSICAL) {
			Player p = event.getPlayer();
			
			if(um.isPlaying(p.getName())) {
				User user = um.getUser(p.getName());
				Game game = user.getGame();
				
				if(game.getState() == GameState.COOLDOWN) {
					event.setCancelled(true);
				} else if(game.getArenas().size() == 1 && game.getState() == GameState.WAITING) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			
			if(um.isPlaying(p.getName())) {
				Game game = um.getUser(p.getName()).getGame();
				
				if(game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
					event.setCancelled(true);
					return;
				} else {
					if(game.getIngamePhrase().grace) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		if(um.isPlaying(p.getName())) {
			User user = um.getUser(p.getName());
			Game game = user.getGame();
			
			if(game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
				um.leaveGame(p);
				return;
			} else {
				IngamePhrase ip = game.getIngamePhrase();
				ip.killUser(user, null, true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		
		if(um.isPlaying(p.getName())) {
			if(event.getCause() == TeleportCause.END_PORTAL || event.getCause() == TeleportCause.COMMAND || event.getCause() == TeleportCause.NETHER_PORTAL) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			
			if(um.isPlaying(p.getName())) {
				Game game = um.getUser(p.getName()).getGame();
				
				if(game.getState() == GameState.WAITING || game.getState() == GameState.VOTING || game.getState() == GameState.COOLDOWN) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
			if(um.isPlaying(p.getName())) {
				Game game = um.getUser(p.getName()).getGame();
				
				if(game.getState() == GameState.COOLDOWN) {
					p.teleport(from);
					event.setCancelled(true);
				} else if(game.getArenas().size() == 1 && game.getState() == GameState.WAITING) {
					p.teleport(from);
					event.setCancelled(true);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Location loc = event.getBlock().getLocation();
		
		if(!um.isPlaying(p.getName())) {
			for(Game game : SurvivalGames.gameManager.getGames()) {
				for(Arena arena : game.getArenas()) {
					if(arena.containsBlock(loc)) {
						event.setCancelled(true);
						p.sendMessage(MessageHandler.getMessage("forbidden-build"));
						return;
					}
				}
			}
		} else {
			User user = um.getUser(p.getName());
			Game game = user.getGame();
			
			if(game.getState() != GameState.INGAME && game.getState() != GameState.DEATHMATCH) {
				event.setCancelled(true);
			} else {
				if(game.getCurrentArena().getAllowedMaterials().contains(event.getBlock().getTypeId())) {
					return;
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if(um.isPlaying(p.getName())) {
			User user = um.getUser(p.getName());
			Game game = user.getGame();
			
			if(game.getArenas().size() == 1) {
				if(game.getState() == GameState.WAITING || game.getState() == GameState.COOLDOWN) {
					event.setCancelled(true);
				}
			} else {
				if(game.getState() == GameState.COOLDOWN) {
					event.setCancelled(true);
				}
			}
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Location loc = event.getBlock().getLocation();
		if(!um.isPlaying(p.getName())) {
			for(Game game : SurvivalGames.gameManager.getGames()) {
				for(Arena arena : game.getArenas()) {
					if(arena.containsBlock(loc)) {
						event.setCancelled(true);
						p.sendMessage(MessageHandler.getMessage("forbidden-build"));
						return;
					}
				}
			}
		} else {
			User user = um.getUser(p.getName());
			Arena arena = user.getGame().getCurrentArena();
			if(arena == null) {
				event.setCancelled(true);
			} else {
				if(arena.getAllowedMaterials().contains(event.getBlock().getTypeId())) {
					if(event.getBlock().getType() == Material.TNT) {
						event.getBlock().setType(Material.AIR);
						event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
					}
					return;
				} else {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public static List<String> allowedCmds = SurvivalGames.instance.getConfig().getStringList("Allowed-Commands");
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if(um.isPlaying(p.getName())) {
			String message = event.getMessage().toLowerCase();
			
			for(String cmd : allowedCmds) {
				if(message.startsWith(cmd))
					return;
			}
			if(message.startsWith("/list")) {
				Game g = um.getUser(p.getName()).getGame();
				p.sendMessage(MessageHandler.getMessage("game-player-list").replace("%0%", Integer.valueOf(g.getPlayingUsers()).toString()).replace("%1%", g.getAlivePlayers()));
				event.setCancelled(true);
			} else if(message.startsWith("/vote")) {
				p.chat("/sg " + message.replace("/", ""));
				event.setCancelled(true);
			} else {
				event.setCancelled(true);
				p.sendMessage(MessageHandler.getMessage("forbidden-command"));
			}
		}
	}

}
