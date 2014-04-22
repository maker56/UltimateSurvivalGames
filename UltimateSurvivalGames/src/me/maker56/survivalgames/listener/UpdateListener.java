package me.maker56.survivalgames.listener;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {
	
	private static String version = null;
	public static void update(String version) {
		UpdateListener.version = version;
		System.out.println("[SurvivalGames] A newer version of survivalgames is available. (" + version + ") You can download it here: http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ You're using " + SurvivalGames.version);
	
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.isOp()) {
				p.sendMessage(MessageHandler.getMessage("prefix") + "§eA newer version of SurvivalGames is available. §7(§b" + version + "§7) §eYou can download it here: §bhttp://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ §7You're using §o" + SurvivalGames.version);
			}
		}
	
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(version != null && event.getPlayer().isOp()) {
			event.getPlayer().sendMessage(MessageHandler.getMessage("prefix") + "§eA newer version of SurvivalGames is available. §7(§b" + version + "§7) §eYou can download it here: §bhttp://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/ §7You're using §o" + SurvivalGames.version);
		}
	}

}
