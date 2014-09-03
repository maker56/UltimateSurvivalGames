package me.maker56.survivalgames.listener;

import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.game.phases.IngamePhase;
import me.maker56.survivalgames.game.phases.VotingPhase;
import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;
import me.maker56.survivalgames.user.UserManager;
import me.maker56.survivalgames.user.UserState;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener {
	
	private UserManager um = SurvivalGames.userManger;
	
	public PlayerListener() {
		reinitializeDatabase();
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		
		int slot = event.getRawSlot();
		ItemStack is;
		try {
			is = event.getInventory().getItem(slot);
			if(is == null || is.getType() == Material.AIR)
				return;
		} catch(ArrayIndexOutOfBoundsException e) {
			return;
		}
		
		ItemMeta im = is.getItemMeta();
		String name = im.getDisplayName();
		if(name == null)
			return;
		
		
		User u = um.getUser(p.getName());
		if(u != null) {
			Game g = u.getGame();
			if(g.getState() == GameState.VOTING || g.getState() == GameState.WAITING || g.getState() == GameState.COOLDOWN) {
				event.setCancelled(true);
				String[] split = name.split(". ");
				if(split.length >= 2) {
					p.closeInventory();
					Arena a = null;
					try {
						a = g.getVotingPhrase().vote(p, Integer.parseInt(ChatColor.stripColor(split[0])));
					} catch(NumberFormatException e) { }
					
					if(a != null) {
						p.playSound(p.getLocation(), Sound.ORB_PICKUP, 4.0F, 2.0F);
					} else {
						p.sendMessage(MessageHandler.getMessage("prefix") + "§cAn interal error occured!");
					}
				}
			}
		} else {
			SpectatorUser su = um.getSpectator(p.getName());
			if(su != null) {
				if(is.getType() == Material.SKULL_ITEM && name.startsWith("§e")) {
					String pname = name.substring(2, name.length());
					event.setCancelled(true);
					Game g = su.getGame();
					User user = g.getUser(pname);
					if(user == null) {
						p.sendMessage(MessageHandler.getMessage("spectator-not-living").replace("%0%", pname));
						return;
					}
					p.closeInventory();
					p.teleport(user.getPlayer().getLocation());
					p.sendMessage(MessageHandler.getMessage("spectator-new-player").replace("%0%", pname));
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = event.getPlayer();
			ItemStack hand = p.getItemInHand();
			if(hand == null || hand.getType() == Material.AIR)
				return;
			
			if(!um.isPlaying(p.getName()) && !um.isSpectator(p.getName()))
				return;
			UserState us = um.getUser(p.getName());
			if(us == null)
				us = um.getSpectator(p.getName());
			
			if(hand.equals(Game.getLeaveItem())) {
				if(((System.currentTimeMillis() - us.getJoinTime()) / 1000) <= 2)
					return;
				um.leaveGame(p);
				event.setCancelled(true);
			}
			

			User u = um.getUser(p.getName());
			
			if(u != null) {
				Game g = u.getGame();

				
				
				if(hand.equals(VotingPhase.getVotingOpenItemStack())) {
					if(g.getState() != GameState.VOTING) {
						p.sendMessage(MessageHandler.getMessage("prefix") + "§cVoting isn't active right now!");
						return;
					}
					if(!g.getVotingPhrase().canVote(p.getName())) {
						p.sendMessage(MessageHandler.getMessage("game-already-vote"));
						return;
					}
					event.setCancelled(true);
					p.openInventory(g.getVotingPhrase().getVotingInventory());
				}
			} else  {
				SpectatorUser su = um.getSpectator(p.getName());
				if(su != null) {
					if(hand.equals(Game.getPlayerNavigatorItem())) {
						event.setCancelled(true);
						su.getPlayer().openInventory(su.getGame().getPlayerNavigatorInventory());
					}
				}
			}
		}
	}
	
	private static double tntdamage;
	
	public static void reinitializeDatabase() {
		tntdamage = SurvivalGames.instance.getConfig().getDouble("TNT-Extra-Damage", 7.0);
		allowedCmds = SurvivalGames.instance.getConfig().getStringList("Allowed-Commands");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if(!event.isCancelled()) {
			if(event.getDamager() instanceof TNTPrimed && event.getEntity() instanceof Player) {
				Player p = (Player) event.getEntity();
				
				if(um.isPlaying(p.getName())) {
					event.setDamage(event.getDamage() + tntdamage);
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getInventory().getType() == InventoryType.CHEST) {
			Player p = (Player) event.getPlayer();
			User user = um.getUser(p.getName());
			if(user != null && user.getCurrentChest() != null) {
				Location loc = user.getCurrentChest().getLocation();
				loc.getWorld().playSound(loc, Sound.CHEST_CLOSE, 1.0F, 1.0F);
			}
		}
	}
	
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
				
				IngamePhase ip = game.getIngamePhrase();
				
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
					ip.killUser(user, um.getUser(pkiller.getName()), false, true);
				} else {
					ip.killUser(user, null, false, true);
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
				
				IngamePhase ip = game.getIngamePhrase();
				
				for(ItemStack is : event.getDrops()) {
					p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
				}
				event.getDrops().clear();
				
				ip.killUser(user, null, false, true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemDrop(PlayerDropItemEvent event) {
		User u = um.getUser(event.getPlayer().getName());
		if(u != null) {
			GameState gs = u.getGame().getState();
			if(gs == GameState.WAITING || gs == GameState.VOTING || gs == GameState.COOLDOWN || u.getGame().isFinishing()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemPickup(PlayerPickupItemEvent event) {
		User u = um.getUser(event.getPlayer().getName());
		if(u != null) {
			GameState gs = u.getGame().getState();
			if(gs == GameState.WAITING || gs == GameState.VOTING || gs == GameState.COOLDOWN || u.getGame().isFinishing())
				event.setCancelled(true);
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
		User user = um.getUser(event.getPlayer().getName());
		if(user != null) {
			GameState state = user.getGame().getState();
			if(event.getRightClicked() instanceof ItemFrame || state == GameState.WAITING || state == GameState.VOTING || state == GameState.COOLDOWN) {
				event.setCancelled(true);
			}
		} else {
			for(Game game : SurvivalGames.gameManager.getGames()) {
				for(Arena arena : game.getArenas()) {
					if(arena.containsBlock(event.getRightClicked().getLocation()))
						event.setCancelled(true);
				}
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
					if(game.getIngamePhrase().grace || game.isFinishing()) {
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
				IngamePhase ip = game.getIngamePhrase();
				ip.killUser(user, null, true, false);
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
				
				if(game.getState() == GameState.WAITING || game.getState() == GameState.VOTING || game.getState() == GameState.COOLDOWN || game.isFinishing()) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		
		if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
			Player p = event.getPlayer();
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
				if(game.getCurrentArena().getAllowedMaterials().contains(event.getBlock().getTypeId()) && !game.isFinishing()) {
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
	
	public static List<String> allowedCmds; 
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		if(um.isPlaying(p.getName()) || um.isSpectator(p.getName())) {
			String message = event.getMessage().toLowerCase();
			
			for(String cmd : allowedCmds) {
				if(message.startsWith(cmd))
					return;
			}
			if(message.startsWith("/list")) {
				UserState u = um.getUser(p.getName());
				if(u == null)
					u = um.getSpectator(p.getName());
				Game g = u.getGame();
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
