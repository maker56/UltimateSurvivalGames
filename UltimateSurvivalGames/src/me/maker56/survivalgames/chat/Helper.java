package me.maker56.survivalgames.chat;

import me.maker56.survivalgames.commands.messages.MessageHandler;

import org.bukkit.entity.Player;

public class Helper {

	public static void showLobbyHelpsite(Player p) {
		p.sendMessage(MessageHandler.getMessage("prefix") + "Lobby §7§m---§r §6Helpsite");
		p.sendMessage("§8/§6sg lobby create <LOBBYNAME> §7- §eCreates a game with the specify name!");
		p.sendMessage("§8/§6sg lobby setspawn <LOBBYNAME> §7- §eSet the Lobby-Spawnlocation at the specify game!");
		p.sendMessage("§8/§6sg lobby unload <LOBBYNAME> §7- §eUnload a lobby!");
		p.sendMessage("§8/§6sg lobby load <LOBBYNAME> §7- §eLoad a lobby!");
		p.sendMessage("§8/§6sg lobby reload <LOBBYNAME> §7- §eUnload and load a lobby!");
		p.sendMessage("§8/§6sg lobby list <LOBBYNAME> §7- §eList of all loaded arenas in a lobby!");
		p.sendMessage("§8/§6sg lobby delete <LOBBYNAME> §7- §eDeletes a lobby from file!");
		p.sendMessage("");
		new JSONMessage("§7§oNeed more help? Click here!").tooltip("Click here to open the official bukkit site!").link("http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/").send(p);
	}
	
	public static void showArenaHelpsite(Player p) {
		p.sendMessage(MessageHandler.getMessage("prefix") + "Arena-Setup §7§m---§r §6Helpsite");
		p.sendMessage("§8/§6sg arena create <LOBBYNAME> <ARENA NAME> §7- §eCreates an arena in a specify game!");
		p.sendMessage("§8/§6sg arena select <LOBBYNAME> <ARENA NAME> §7- §eSelects the specify arena!");
		p.sendMessage("§8/§6sg arena tools §7- §eGives you the Arena-Selection Tools!");
		p.sendMessage("§8/§6sg arena check §7- §eShows whats even need to be done on the selected arena!");
		p.sendMessage("§8/§6sg arena addspawn §7- §eAdd a Spawn on the selected arena!");
		p.sendMessage("§8/§6sg arena removespawn <SPAWNID> §7- §eRemoves a spawn from the selected arena!");
		p.sendMessage("§8/§6sg arena deathmatch §7- §eDe/activate the Deathmatch on the spelected arena!");
		p.sendMessage("§8/§6sg arena deathmatch add §7- §eAdd a Deathmatch-Spawn on the selected arena!");
		p.sendMessage("§8/§6sg arena deathmatch remove <SPAWNID> §7- §eRemove an Spawn on the selected arena!");
		p.sendMessage("§8/§6sg arena finish §7- §eFinished the create-setup on the selected arena!");
		p.sendMessage("§8/§6sg arena save §7- §eSaves the blocks of an arena to file for map reset!");
		p.sendMessage("§8/§6sg arena delete §7- §eRemoves an arena in a lobby!");
		p.sendMessage("");
		new JSONMessage("§7§oNeed more help? Click here!").tooltip("Click here to open the official bukkit site!").link("http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/").send(p);
	}

}
