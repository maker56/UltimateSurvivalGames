package me.maker56.survivalgames.chat;

import me.maker56.survivalgames.commands.messages.MessageHandler;

import org.bukkit.entity.Player;

public class Helper {

	public static void showLobbyHelpsite(Player p) {
		p.sendMessage(MessageHandler.getMessage("prefix") + "Lobby Management §7§m---§r §6Helpsite");
		new JSONMessage("§8/§6sg lobby create <LOBBYNAME> §7- §eCreates a game with the specify name!").suggest("/sg lobby create ").send(p);
		new JSONMessage("§8/§6sg lobby unload <LOBBYNAME> §7- §eUnload a lobby!").suggest("/sg lobby unload ").send(p);
		new JSONMessage("§8/§6sg lobby load <LOBBYNAME> §7- §eLoad a lobby!").suggest("/sg lobby load ").send(p);
		new JSONMessage("§8/§6sg lobby reload <LOBBYNAME> §7- §eUnload and load a lobby!").suggest("/sg lobby reload ").send(p);
		new JSONMessage("§8/§6sg lobby list <LOBBYNAME> §7- §eList of all loaded arenas in a lobby!").suggest("/sg lobby list ").send(p);
		new JSONMessage("§8/§6sg lobby delete <LOBBYNAME> §7- §eDeletes a lobby from file!").suggest("/sg lobby delete ").send(p);
		p.sendMessage("");
		new JSONMessage("§7Need more help? Click here!").tooltip("Click here to open the official bukkit site!").link("http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/").send(p);
	}
	
	public static void showArenaHelpsite(Player p) {
		p.sendMessage(MessageHandler.getMessage("prefix") + "Arena Management §7§m---§r §6Helpsite");
		new JSONMessage("§8/§6sg arena create <LOBBYNAME> <ARENA NAME> §7- §eCreates an arena in a specify game!").suggest("/sg arena create ").send(p);
		new JSONMessage("§8/§6sg arena select <LOBBYNAME> <ARENA NAME> §7- §eSelects the specify arena!").suggest("/sg arena select ").send(p);
		new JSONMessage("§8/§6sg arena tools §7- §eGives you the Arena-Selection Tools!").suggest("/sg arena tools").send(p);
		new JSONMessage("§8/§6sg arena check §7- §eShows whats even need to be done on the selected arena!").suggest("/sg arena check").send(p);
		new JSONMessage("§8/§6sg arena addspawn §7- §eAdd a Spawn on the selected arena!").suggest("/sg arena addspawn").send(p);
		new JSONMessage("§8/§6sg arena removespawn <SPAWNID> §7- §eRemoves a spawn from the selected arena!").suggest("/sg arena removespawn ").send(p);
		new JSONMessage("§8/§6sg arena deathmatch §7- §eDe/activate the Deathmatch on the spelected arena!").suggest("/sg arena deathmatch").send(p);
		new JSONMessage("§8/§6sg arena deathmatch add §7- §eAdd a Deathmatch-Spawn on the selected arena!").suggest("/sg arena deathmatch add").send(p);
		new JSONMessage("§8/§6sg arena deathmatch remove <SPAWNID> §7- §eRemove an Spawn on the selected arena!").suggest("/sg arena deathmatch remove ").send(p);
		new JSONMessage("§8/§6sg arena finish §7- §eFinished the create-setup on the selected arena!").suggest("/sg arena finish").send(p);
		new JSONMessage("§8/§6sg arena save §7- §eSaves the blocks of an arena to file for map reset!").suggest("/sg arena save").send(p);
		new JSONMessage("§8/§6sg arena delete §7- §eRemoves an arena in a lobby!").suggest("/sg arena delete").send(p);
		p.sendMessage("");
		new JSONMessage("§7Need more help? Click here!").tooltip("Click here to open the official bukkit site!").link("http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/").send(p);
	}

}
