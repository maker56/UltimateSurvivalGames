package me.maker56.survivalgames.listener;

import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.events.ResetDoneEvent;
import me.maker56.survivalgames.events.SaveDoneEvent;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameManager;
import me.maker56.survivalgames.game.GameState;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ResetListener implements Listener {
	
	private GameManager gm = SurvivalGames.gameManager;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			logChunk(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled()) {
			logChunk(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onFromToEvent(BlockFromToEvent event)  {
	    if(!event.isCancelled()) {
	    	logChunk(event.getToBlock().getLocation());
		    logChunk(event.getBlock().getLocation());
	    }
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if(!event.isCancelled()) {
			logChunk(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockGrow(BlockGrowEvent event) {
		if(!event.isCancelled()) {
			logChunk(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeafDecay(LeavesDecayEvent event) {
		if(!event.isCancelled()) {
			logChunk(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBurn(BlockBurnEvent event) {
		if(!event.isCancelled()) {
			logChunk(event.getBlock().getLocation());
		}
	}
	
	@EventHandler
	public void onBlockFade(BlockFadeEvent event) {
		if(!event.isCancelled()) {
			logChunk(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockExplode(EntityExplodeEvent event) {
		List<Block> blocks = event.blockList();
		if(blocks.size() > 0) {
			Location loc = blocks.get(0).getLocation();
			for(Game game : gm.getGames()) {
				for(Arena a : game.getArenas()) {
					if(a.containsBlock(loc)) {
						blocks.clear();
						break;
					}
				}
			}
		}
	}
	 
	private void logChunk(Location loc) {
		for(Game game : gm.getGames()) {
			if(game.getState() == GameState.INGAME || game.getState() == GameState.DEATHMATCH) {
				for(Arena a : game.getArenas()) {
					if(a.containsBlock(loc)) {
						String chunkKey = loc.getChunk().getX() + "," + loc.getChunk().getZ();
						if(!game.getChunksToReset().contains(chunkKey)) {
							game.getChunksToReset().add(chunkKey);
							List<String> reset = SurvivalGames.reset.getStringList("Startup-Reset." + game.getName() + "." + a.getName());
							reset.add(chunkKey);
							SurvivalGames.reset.set("Startup-Reset." + game.getName() + "." + a.getName(), reset);
							SurvivalGames.saveReset();
						}
						return;
					}
				}
			}
		}
	}
	
	
	// AFTER SAVE / RESET
	
	@EventHandler
	public void onSaveComplete(SaveDoneEvent event) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(PermissionHandler.hasPermission(p, Permission.ARENA)) {
				p.sendMessage(MessageHandler.getMessage("prefix") + "Done saveing arena " + event.getArena() + " in lobby " + event.getLobby() + "! It tooks " + event.getTime() + "! The file is " + event.getFileSize() + " " + event.getFileSizeFormat() + " big.");
			}
		}
	}
	
	@EventHandler
	public void onResetComplete(ResetDoneEvent event) {
		Game game = gm.getGame(event.getLobby());
		if(game != null) {
			gm.unload(game);
		}
		gm.load(event.getLobby());
		SurvivalGames.signManager.updateSigns();
	}

}
