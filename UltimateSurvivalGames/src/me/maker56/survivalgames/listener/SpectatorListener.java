package me.maker56.survivalgames.listener;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.user.UserManager;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class SpectatorListener implements Listener {
	
	private UserManager um = SurvivalGames.getUserManager();
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if(um.isSpectator(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(um.isSpectator(event.getPlayer().getName()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if(um.isSpectator(event.getPlayer().getName()))
			event.setCancelled(true);
	}


	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent event) {
		if(um.isSpectator(event.getPlayer().getName()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if(um.isSpectator(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(um.isSpectator(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getTarget() instanceof Player) {
			Player p = (Player) event.getTarget();
			if(um.isSpectator(p.getName()))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onExpChangeEvent(PlayerExpChangeEvent event) {
		if(um.isSpectator(event.getPlayer().getName()))
			event.setAmount(0);
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		Player damager = null;
		if(event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if(event.getDamager() instanceof Projectile) {
			Projectile pro = (Projectile) event.getDamager();
			if(pro.getShooter() instanceof Player) {
				damager = (Player) pro.getShooter();
			}
		}
		
		if(damager != null) {
			if(um.isSpectator(damager.getName()))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(um.isSpectator(p.getName()))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(um.isSpectator(p.getName()))
				event.setCancelled(true);
		}
	}


}
