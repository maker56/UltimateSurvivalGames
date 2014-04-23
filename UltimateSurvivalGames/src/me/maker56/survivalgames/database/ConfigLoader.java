package me.maker56.survivalgames.database;

import java.util.ArrayList;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.game.GameState;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLoader {
	
	public void load() {
		reloadConfig();
		reloadMessages();
		reloadDatabase();
		reloadSigns();
		reloadReset();
	}
	
	public static void reloadSigns() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "signs.yml").getFileConfiguration();
		SurvivalGames.signs = c;
		
		c.addDefault("Sign.LeftClick.Show current arena", true);
		c.addDefault("Sign.LeftClick.Show players remain", true);
		
		c.addDefault("Sign.Line.1", "&bSurvivalGames");
		c.addDefault("Sign.Line.2", "&8[&e%name%&8]");
		c.addDefault("Sign.Line.3", "&o%state%");
		c.addDefault("Sign.Line.4", "%currentplayers%/&7%requiredplayers%&r/%maxplayers%");
		
		c.addDefault("Sign.LeavePrefix", "&bSurvivalGames");
		
		for(GameState state : GameState.values()) {
			c.addDefault("Translations." + state.toString(), state.toString());
		}

		
		c.options().copyDefaults(true);
		SurvivalGames.saveSigns();
	}
	
	public static void reloadReset() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "reset.yml").getFileConfiguration();
		SurvivalGames.reset = c;
		
		c.options().header("This is the file for the startup reset.\n" +
				"If the server shutdown, reload or crash in a running game, the server reset the arena after enabling survivalgames.");
		c.options().copyDefaults(true);
		SurvivalGames.saveReset();
	}
	
	public static void reloadConfig() {
		SurvivalGames.instance.reloadConfig();
		FileConfiguration c = SurvivalGames.instance.getConfig();
		
		c.addDefault("enable-update-check", true);
		c.addDefault("use-permissions", true);
		c.addDefault("broadcast-win", true);
		
		c.addDefault("Lightning.on-death", true);
		c.addDefault("Lightning.on-few-players", true);
		c.addDefault("Lightning.few-players", 3);
		c.addDefault("Lightning.few-players-time", 45);
		
		c.addDefault("Default.Enable-Voting", true);
		c.addDefault("Default.Lobby-Time", 120);
		c.addDefault("Default.Max-Voting-Arenas", 3);
		c.addDefault("Default.Required-Players-to-start", 3);
		
		c.addDefault("Default.Arena.Chests.TypeID", 54);
		c.addDefault("Default.Arena.Chests.Data", -1);
		c.addDefault("Default.Arena.Grace-Period", 30);
		
		c.addDefault("Default.Arena.Automaticly-Deathmatch-Time", 1800);
		c.addDefault("Default.Arena.Player-Deathmatch-Start", 3);
		
		c.addDefault("Default.Money-on-Kill", 2.5);
		c.addDefault("Default.Money-on-Win", 20.0);
		c.addDefault("Default.Midnight-chest-refill", true);
		
		ArrayList<Integer> allowedBlocks = new ArrayList<>();
		
		allowedBlocks.add(18);
		allowedBlocks.add(31);
		allowedBlocks.add(92);
		allowedBlocks.add(103);
		allowedBlocks.add(39);
		allowedBlocks.add(40);
		allowedBlocks.add(86);
		allowedBlocks.add(46);
		allowedBlocks.add(51);
		allowedBlocks.add(30);
		
		c.addDefault("Default.Arena.Allowed-Blocks", allowedBlocks);
		
		if(c.contains("Chest"))
			c.set("Chest", null);
		
		List<String> lvl1 = new ArrayList<>();
		
		lvl1.add("271,1");
		lvl1.add("301,1");
		lvl1.add("314,1");
		lvl1.add("32,2");
		lvl1.add("287,2");
		lvl1.add("376,2");
		lvl1.add("260,3");
		lvl1.add("262,5");
		
		c.addDefault("Chestloot.C1", lvl1);
		
		
		List<String> lvl2 = new ArrayList<>();
		
		lvl2.add("364,1");
		lvl2.add("365,2");
		lvl2.add("366,1");
		lvl2.add("282,1");
		lvl2.add("268,1");
		lvl2.add("314,1");
		lvl2.add("316,1");
		lvl2.add("301,1");
		lvl2.add("320,2");
		lvl2.add("281,1");
		lvl2.add("360,2");
		lvl2.add("363,1");
		lvl2.add("262,3");
		
		c.addDefault("Chestloot.C2", lvl2);
		
		
		List<String> lvl3 = new ArrayList<>();
		
		lvl3.add("103,1");
		lvl3.add("306,1");
		lvl3.add("360,4");
		lvl3.add("283,1");
		lvl3.add("30,3");
		lvl3.add("303,1");
		lvl3.add("305,1");
		lvl3.add("398,1");
		lvl3.add("300,1");
		lvl3.add("262,4");
		lvl3.add("266,2");
		lvl3.add("46,1");
		
		c.addDefault("Chestloot.C3", lvl3);
		
		
		List<String> lvl4 = new ArrayList<>();
		
		lvl4.add("266,5");
		lvl4.add("307,1");
		lvl4.add("309,1");
		lvl4.add("302,1");
		lvl4.add("259,1");
		lvl4.add("215,1");
		lvl4.add("317,1");
		lvl4.add("272,1");
		lvl4.add("268,1");
		lvl4.add("249:3,1");
		
		c.addDefault("Chestloot.C4", lvl4);
		
		
		List<String> lvl5 = new ArrayList<>();
		lvl5.add("264,2");
		lvl5.add("265,2");
		lvl5.add("280,2");
		lvl5.add("354,1");
		lvl5.add("261:168,1");
		lvl5.add("373:8261,1");
		
		c.addDefault("Chestloot.C5", lvl5);
		c.addDefault("Chest-Title", "Survival Chest");
		
		ArrayList<String> allowedCmds = new ArrayList<>();
		allowedCmds.add("/sg");
		allowedCmds.add("/hg");
		allowedCmds.add("/hungergames");
		allowedCmds.add("/survivalgames");
		c.addDefault("Allowed-Commands", allowedCmds);
		
		c.options().copyDefaults(true);
		SurvivalGames.instance.saveConfig();
	}
	
	public static void reloadDatabase() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "database.yml").getFileConfiguration();
		SurvivalGames.database = c;
	}
	
	public static void reloadMessages() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "messages.yml").getFileConfiguration();
		SurvivalGames.messages = c;
		
		c.addDefault("prefix", "&3[SurvivalGames] &6");
		c.addDefault("no-permission", "&cYou don't have permission to do this!");
		c.addDefault("cmd-error", "&cError: %0%");
		
		c.addDefault("join-unknown-game", "&cThe lobby %0% does not exist!");
		c.addDefault("join-game-running", "&cThis game is already running!");
		c.addDefault("join-vehicle", "&cYou can't join SurvivalGames in a vehicle!");
		c.addDefault("join-game-full", "&cSorry, this lobby is full!");
		c.addDefault("join-success", "%0% joined the lobby! &7(&e%1%&7/&e%2%&7)");
		c.addDefault("join-already-playing", "&cYou're already playing!");
		c.addDefault("leave-not-playing", "&cYou aren't playing!");
		c.addDefault("game-cooldown-big", "The game starts in %0% seconds");
		c.addDefault("game-cooldown-little", "The game starts in %0%");
		
		c.addDefault("game-waiting-cooldown-big", "The voting ends in %0% seconds");
		c.addDefault("game-waiting-cooldown-little", "The voting ends in %0%");
		c.addDefault("game-waiting-end", "The waiting phase has been ended!");
		
		c.addDefault("game-deathmatch-cooldown-big-minutes", "&7The final deathmatch starts in %0% minutes!");
		c.addDefault("game-deathmatch-cooldown-big-seconds", "The final deathmatch starts in %0% seconds");
		c.addDefault("game-deathmatch-cooldown-little", "The final deathmatch starts in %0%");
		c.addDefault("game-deathmatch-start", "Let's start the final deathmatch!");
		c.addDefault("game-deathmatch-timeout", "The deathmatch ends automaticly in %0% seconds!");
		c.addDefault("game-deathmatch-timeout-warning", "When the deathmatch ends automaticly, the winner will be choosed random!");
		
		c.addDefault("game-player-die-killer", "%0% was killed by %1%!");
		c.addDefault("game-player-die-damage", "%0% has died and gone from us!");
		c.addDefault("game-player-left", "%0% left the game");
		c.addDefault("game-remainplayers", "%0% tributes remain.");
		
		c.addDefault("game-grace-period", "&bYou have %0% seconds Grace-Period!");
		c.addDefault("game-grace-period-ended", "&bThe Grace-Period has been ended!");
		
		c.addDefault("game-voting-cooldown-big", "The voting ends in %0% seconds");
		c.addDefault("game-voting-cooldown-little", "The voting ends in %0%");
		c.addDefault("game-voting-end", "The voting phrase has been ended!");
		c.addDefault("game-no-vote", "&cYou can only vote in the voting phase of the game!");
		c.addDefault("game-bad-vote", "&cThis isn't a valid vote ID!");
		c.addDefault("game-already-vote", "&cYou've already voted for a arena!");
		c.addDefault("game-no-voting-enabled", "&cSorry, voting isn't enabled! The arena will choosed random!");
		c.addDefault("game-success-vote", "You've voted successfully for arena &b%0%&6!");
		c.addDefault("game-start-canceled", "Not enough players are in this lobby. Cancel Timer...");
		c.addDefault("game-start", "The round begins, &b%0% &6players are playing! &bGood luck&6!");
		c.addDefault("game-chestrefill", "It's midnight! All chests are refilled!");
		c.addDefault("game-win", "%0% won SurvivalGames in Arena %1% in game %2%!");
		c.addDefault("game-win-winner-message", "Congratulations! You've won SurvivalGames in Arena &b%0%&6!");
		
		c.addDefault("game-sign-info", "&7&m-----&r &6Lobby info: &e%0% &7&m-----");
		c.addDefault("game-sign-arena", "Arena&7: &e%0%");
		c.addDefault("game-sign-playersleft", "%0% players remain&7: %1%");
		c.addDefault("game-sign-noinfo", "There aren't any informations now!");
		
		c.addDefault("game-player-list", "There are %0% players&7: %1%");
		c.addDefault("game-not-loaded", "&cLobby %0% isn't loaded!");
		c.addDefault("game-already-loaded", "&cLobby %0% is already loaded!");
		c.addDefault("game-success-loaded", "Lobby %0% loaded successfully!");
		c.addDefault("game-success-unloaded", "Lobby %0% unloaded successfully!");
		c.addDefault("game-load-error", "&cCan't load lobby %0%! %1%");
		
		c.addDefault("game-already-exists", "&cThe lobby %0% already exist!");
		c.addDefault("game-created", "You've created the lobby %0% successfully!");
		c.addDefault("game-spawn-set", "You've set the spawn for game %0% successfully!");
		c.addDefault("game-set-spawn", "To set the lobby of this game, type /sg game setspawn %0%");
		c.addDefault("game-not-found", "&cThe Game %0% does not exists!");
		c.addDefault("game-must-enter", "&cYou must enter a name: %0%");
		c.addDefault("game-vote", "Vote for an arena: &b/sg vote <ID>");
		c.addDefault("forbidden-command", "&cYou can't execute this command in SurvivalGames!");
		c.addDefault("forbidden-build", "&cYou aren't allowed to build in a SurvivalGames arena!");
		
		c.addDefault("arena-already-exists", "&cThe arena %0% already exists in lobby %1%!");
		c.addDefault("arena-must-select", "&cPlease select an arena with %0%!");
		c.addDefault("arena-created", "You've created arena %0% in lobby %1% successfully!");
		c.addDefault("arena-selected", "You've selected arena %0% in lobby %1%!");
		c.addDefault("arena-not-found", "The arena %0% does not exists in lobby %1%!");
		c.addDefault("arena-no-selection", "&cPlease select two points with the selection item: %0%");
		c.addDefault("arena-check", "Type %0% to see what you have to do to complete the arena setup!");
		c.addDefault("arena-spawn-added", "You've added Spawn %0% successfully!");
		c.addDefault("arena-spawn-removed", "You removed Spawn %0% successfully!");
		c.addDefault("arena-spawn-notfound", "&cSpawn %0% does not exist!");
		c.addDefault("arena-deathmatch-changed", "You've changed the deathmatch: %0%!");
		
		c.addDefault("arena-money-win", "&eYou've received &a%0% &emoney for winning survival games!");
		c.addDefault("arena-money-kill", "&eYou've received &a%0% &emoney for killing %1%!");
		
		c.addDefault("arena-tools", "Here is the selection tool. Left/Rightclick to set two positions!");
		c.addDefault("arena-tools-worldedit", "Please use the WorldEdit Wand Tool to set two positions!");
		
		c.addDefault("config-error-name", "&cPlease enter a valid configuration name: %0%");
		
		c.options().copyDefaults(true);
		SurvivalGames.saveMessages();
		
	}

}
