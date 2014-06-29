package me.maker56.survivalgames.listener;

import java.util.HashMap;

import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.reset.Selection;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SelectionListener implements Listener {
	
	public static HashMap<String, Selection> selections = new HashMap<>();
	
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
			String message = MessageHandler.getMessage("prefix");
			if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				Location loc = event.getClickedBlock().getLocation();
				message += "First point set!";
				if(selections.containsKey(p.getName())) {
					Selection sel = selections.get(p.getName());
					sel.setMinimumLocation(loc);
					message += " (" + sel.getSize() + " blocks)";
				} else {
					Selection sel = new Selection(loc, null);
					selections.put(p.getName(), sel);
				}
				
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Location loc = event.getClickedBlock().getLocation();
				message += "Second point set!";
				if(selections.containsKey(p.getName())) {
					Selection sel = selections.get(p.getName());
					sel.setMaximumLocation(loc);
					message += " (" + sel.getSize() + " blocks)";
				} else {
					Selection sel = new Selection(null, loc);
					selections.put(p.getName(), sel);
				}
			}
			p.sendMessage(message);
		}
		

	}

}
