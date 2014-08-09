package me.maker56.survivalgames.listener;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.commands.permission.Permission;
import me.maker56.survivalgames.commands.permission.PermissionHandler;
import me.maker56.survivalgames.sign.SignManager;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
	
	private SignManager sm = SurvivalGames.signManager;
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player p = event.getPlayer();
		
		if(event.getLine(0).equalsIgnoreCase("[SurvivalGames]")) {
			
			if(!PermissionHandler.hasPermission(p, Permission.LOBBY)) {
				p.sendMessage(MessageHandler.getMessage("no-permission"));
				event.getBlock().breakNaturally();
				return;
			}
			
			if(event.getLine(1).equalsIgnoreCase("join") ) {
				SurvivalGames.signManager.addSign(p, event.getBlock().getLocation(), event.getLine(2));
			} else if(event.getLine(1).equalsIgnoreCase("quit") || event.getLine(1).equalsIgnoreCase("leave")) {
				for(int i = 0; i < sm.getLeaveSignDesign().length; i++) {
					event.setLine(i, sm.getLeaveSignDesign()[i]);
				}
			}
			
			
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR)
			return;
		Block b = event.getClickedBlock();
		if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
			String lobby = sm.getLobby(b.getLocation());
			
			if(lobby != null) {
				Player p = event.getPlayer();
				if(event.getAction() == Action.LEFT_CLICK_BLOCK && p.getGameMode() != GameMode.CREATIVE) {
					sm.sendInfo(p, lobby);
					event.setCancelled(true);
				} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
					SurvivalGames.userManger.joinGame(p, lobby);
					event.setCancelled(true);
				}
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Sign s = (Sign) b.getState();
				String[] design = sm.getLeaveSignDesign();
				for(int i = 0; i < design.length; i++) {
					if(!s.getLine(i).equals(design[i]))
						return;
				}
				SurvivalGames.userManger.leaveGame(event.getPlayer());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.isCancelled())
			return;
		Block b = event.getBlock();
		if(b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
			if(sm.isSign(b.getLocation())) {
				if(event.getPlayer().isSneaking()) {
					sm.removeSign(event.getPlayer(), b.getLocation());
				} else {
					event.getPlayer().sendMessage(MessageHandler.getMessage("prefix") + "§cYou have to sneak if you want to delete this survivalgames sign.");
					event.setCancelled(true);
				}
			}
			
		}
	}

}
