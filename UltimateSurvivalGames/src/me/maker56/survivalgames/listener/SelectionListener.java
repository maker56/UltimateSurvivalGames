package me.maker56.survivalgames.listener;

import java.util.HashMap;

import me.maker56.survivalgames.commands.messages.MessageHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SelectionListener implements Listener {
	
	public static HashMap<String, Location[]> selection = new HashMap<>();
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack is = p.getItemInHand();
		
		if(is == null) {
			return;
		}
		
		ItemMeta im = is.getItemMeta();
		
		if(im == null)
			return;
		
		if(im.getDisplayName() == null)
			return;
		
		if(im.getDisplayName().equals("SurvivalGames Selection Tool")) {
			event.setCancelled(true);
			
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				if(selection.containsKey(p.getName())) {
					Location[] loc = selection.get(p.getName());
					loc[0] = event.getClickedBlock().getLocation();
				} else {
					Location[] loc = { event.getClickedBlock().getLocation(), null };
					selection.put(p.getName(), loc);
				}
				
				p.sendMessage(MessageHandler.getMessage("prefix") + "First point set!");
				
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(selection.containsKey(p.getName())) {
					Location[] loc = selection.get(p.getName());
					loc[1] = event.getClickedBlock().getLocation();
				} else {
					Location[] loc = { null, event.getClickedBlock().getLocation() };
					selection.put(p.getName(), loc);
				}
				
				p.sendMessage(MessageHandler.getMessage("prefix") + "Second point set!");
			}
		}
		

	}

}
