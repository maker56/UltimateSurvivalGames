package me.maker56.survivalgames.chat;

import java.util.ArrayList;
import java.util.List;

import me.maker56.survivalgames.commands.messages.MessageHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import org.bukkit.entity.Player;

public class Helper {

	public static void showLobbyHelpsite(Player p) {
		p.sendMessage(MessageHandler.getMessage("prefix") + "Lobby Management §7§m---§r §6Helpsite");
		for(BaseComponent[] bc : lobby) {
			p.spigot().sendMessage(bc);
		}
	}
	
	public static void showConfigHelpsite(Player p) {
		p.sendMessage(MessageHandler.getMessage("prefix") + "Configuration §7§m---§r §6Helpsite");
		for(BaseComponent[] bc : config) {
			p.spigot().sendMessage(bc);
		}
	}
	
	public static void showArenaHelpsite(Player p) {
		p.sendMessage(MessageHandler.getMessage("prefix") + "Arena Management §7§m---§r §6Helpsite");
		for(BaseComponent[] bc : arena) {
			p.spigot().sendMessage(bc);
		}
	}
	
	private static List<BaseComponent[]> lobby, arena, config;
	
	static {		
		lobby = new ArrayList<>();
		lobby.add(new ComponentBuilder("§8/§6sg lobby create <LOBBYNAME> §7- §eCreates a game with the specify name!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg lobby create ")).create());
		lobby.add(new ComponentBuilder("§8/§6sg lobby unload <LOBBYNAME> §7- §eUnload a lobby!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg lobby unload ")).create());
		lobby.add(new ComponentBuilder("§8/§6sg lobby load <LOBBYNAME> §7- §eLoad a lobby!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg lobby load ")).create());
		lobby.add(new ComponentBuilder("§8/§6sg lobby reload <LOBBYNAME> §7- §eUnload and load a lobby!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg lobby reload ")).create());
		lobby.add(new ComponentBuilder("§8/§6sg lobby list <LOBBYNAME> §7- §eList of all loaded arenas in a lobby!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg lobby list ")).create());
		lobby.add(new ComponentBuilder("§8/§6sg lobby delete <LOBBYNAME> §7- §eDeletes a lobby from file!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg lobby delete  ")).create());
		
		arena = new ArrayList<>();
		arena.add(new ComponentBuilder("§8/§6sg arena create <LOBBYNAME> <ARENA NAME> §7- §eCreates an arena in a specify game!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena create ")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena select <LOBBYNAME> <ARENA NAME> §7- §eSelects the specify arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena select ")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena tools §7- §eGives you the Arena-Selection Tools!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena tools")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena check §7- §eShows whats even need to be done on the selected arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena check")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena addspawn §7- §eAdd a Spawn on the selected arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena addspawn")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena removespawn <SPAWNID> §7- §eRemoves a spawn from the selected arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena removespawn ")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena deathmatch §7- §eDe/activate the Deathmatch on the spelected arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena deathmatch")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena deathmatch add §7- §eAdd a Deathmatch-Spawn on the selected arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena deathmatch add")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena deathmatch remove <SPAWNID> §7- §eRemove an Spawn on the selected arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena deathmatch remove ")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena deathmatch domemiddle §7- §eSet the middle of the invisible dome of the deathmatch arena").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena deathmatch domemiddle")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena deathmatch domeradius [RADIUS] §7- §eSet/View the radius of the ivsibile deathmatch dome").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena deathmatch domeradius")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena finish §7- §eFinished the create-setup on the selected arena!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena finish")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena save §7- §eSaves the blocks of an arena to file for map reset!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena save")).create());
		arena.add(new ComponentBuilder("§8/§6sg arena delete §7- §eRemoves an arena in a lobby!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg arena delete")).create());
	
		config = new ArrayList<>();
		config.add(new ComponentBuilder("§8/§6sg config reload [MESSAGES/SIGNS/DATABASE/CONFIG/CHESTLOOT/SCOREBOARD] §7- §eReloads the specify config!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to prepare command...").create())).event(new ClickEvent(Action.SUGGEST_COMMAND, "/sg config reload ")).create());
	}
	
}
