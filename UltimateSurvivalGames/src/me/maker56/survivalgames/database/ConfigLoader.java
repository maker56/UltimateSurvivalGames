package me.maker56.survivalgames.database;

import java.util.ArrayList;
import java.util.List;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.game.GameState;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLoader {
	
	public void load() {
		reloadConfig();
		reloadMessages();
		reloadDatabase();
		reloadSigns();
		reloadReset();
		reloadChests();
		reloadScoreboard();
	}
	
	public static void reloadScoreboard() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "scoreboard.yml").getFileConfiguration();
		SurvivalGames.scoreboard = c;
		
		String path = "Phase.Waiting.";
		c.addDefault(path + "Enabled", true);
		c.addDefault(path + "Title", "&b&lWaiting for players");
		List<String> content = new ArrayList<>();
		content.add("&eRequired players to start&7://%requiredplayers%");
		content.add("&eCurrent player amount&7://%playing%");
		c.addDefault(path + "Scores", content);
		
		path = "Phase.Voting.";
		c.addDefault(path + "Enabled", true);
		c.addDefault(path + "Title", "&b&lArena Voting");
		content = new ArrayList<>();
		content.add("&e%arena%//%votecount%");
		content.add("&e%arena%//%votecount%");
		content.add("&e%arena%//%votecount%");
		c.addDefault(path + "Scores", content);
		
		path = "Phase.Cooldown.";
		c.addDefault(path + "Enabled", true);
		c.addDefault(path + "Title", "&b&lCooldown");
		content = new ArrayList<>();
		content.add("&eTime remaining&7://%time%");
		content.add("&eTributes&7://%playing%");
		c.addDefault(path + "Scores", content);
		
		path = "Phase.Ingame.";
		c.addDefault(path + "Enabled", true);
		c.addDefault(path + "Title", "&b&lIngame");
		content = new ArrayList<>();
		content.add("&e&lAlive&7://%playing%");
		content.add("&c&lDeath&7://%death%");
		c.addDefault(path + "Scores", content);
		
		path = "Phase.Deathmatch.";
		c.addDefault(path + "Enabled", true);
		c.addDefault(path + "Title", "&b&lDeathmatch");
		content = new ArrayList<>();
		content.add("&eTime remaining&7://%time%");
		c.addDefault(path + "Scores", content);
		
		c.options().header(
				"##### UltimateSurvivalGames Scoreboard Configuration #####\n" +
				"\n" +
				"How does this work?\n" +
				"For each game phase (WAITING,VOTING,COOLDOWN,INGAME and DEATHMATHCH) is a scoreboard design.\n" +
				"If you set \"Enabled\" for a phase to false, no scoreboard will shown!\n" +
				"The title can be maximal 32 charakters long and cannot contain variables.\n" +
				"\n" +
				"In the \"Scores\" part, you can modify the content of the scoreboard. \"//\" splits the line in name and score.\n" +
				"The left part is the name which can be maximal 48 charalters long.\n" +
				"The right part is the amount of a score. Here you have to write the variables.\n" +
				"\n" +
				"What are the variables?\n" +
				"You can use many variables. Here is a list:\n" +
				"\n" +
				"  %playing% - The current amount of players in a lobby!\n" +
				"  %requiredplayers% - The amount of required players to start a game automaticly!\n" +
				"  %death% - The amount of deaths in a round!\n" +
				"  %spectators% - The amount of spectators in a round!\n" +
				"  %time% - The remaining time of a game phase!\n" +
				"  %votecount% - The amount of votes of an arena (Only works in the voting phase)\n" +
	            "  %arena% - The name of the arena (Only works in the score name)\n" +
	            "\n" +
	            "More help on http://dev.bukkit.org/bukkit-plugins/ultimatesurvivalgames/\n");
		
		
		c.options().copyDefaults(true);
		SurvivalGames.saveScoreboard();
	}
	
	public static void reloadChests() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "chestloot.yml").getFileConfiguration();
		SurvivalGames.chestloot = c;
		
		List<String> lvl1 = new ArrayList<>();
		
		lvl1.add(Material.WOOD_AXE + "");
		lvl1.add(Material.LEATHER_BOOTS + "");
		lvl1.add(Material.GOLD_HELMET + "");
		lvl1.add(Material.APPLE + " 3");
		lvl1.add(Material.ARROW + " 5");
		c.addDefault("Chestloot.Level 1", lvl1);
		
		
		List<String> lvl2 = new ArrayList<>();
		
		lvl2.add(Material.COOKED_BEEF +"");
		lvl2.add(Material.RAW_CHICKEN + " 2");
		lvl2.add(Material.COOKED_CHICKEN + "");
		lvl2.add(Material.MUSHROOM_SOUP + "");
		lvl2.add(Material.WOOD_SWORD + "");
		lvl2.add(Material.GOLD_HELMET + "");
		lvl2.add(Material.GOLD_LEGGINGS + "");
		lvl2.add(Material.LEATHER_BOOTS + "");
		lvl2.add(Material.GRILLED_PORK + " 2");
		lvl2.add(Material.BOWL + "");
		lvl2.add(Material.MELON + " 2");
		lvl2.add(Material.RAW_CHICKEN + "");
		
		c.addDefault("Chestloot.Level 2", lvl2);
		
		
		List<String> lvl3 = new ArrayList<>();
		
		lvl3.add(Material.MELON_BLOCK + "");
		lvl3.add(Material.IRON_HELMET + "");
		lvl3.add(Material.MELON + " 4");
		lvl3.add(Material.GOLD_SWORD + "");
		lvl3.add(Material.WEB + " 3");
		lvl3.add(Material.CHAINMAIL_CHESTPLATE + "");
		lvl3.add(Material.CHAINMAIL_BOOTS + "");
		lvl3.add(Material.FISHING_ROD + "");
		lvl3.add(Material.LEATHER_LEGGINGS + "");
		lvl3.add(Material.ARROW + " 4");
		lvl3.add(Material.GOLD_INGOT + " 2");
		lvl3.add(Material.TNT + " name:&eInstant_ignition_bomb");
		lvl3.add(Material.DEAD_BUSH + "");
		
		c.addDefault("Chestloot.Level 3", lvl3);
		
		
		List<String> lvl4 = new ArrayList<>();
		
		lvl4.add(Material.GOLD_INGOT + " 5");
		lvl4.add(Material.IRON_CHESTPLATE + "");
		lvl4.add(Material.IRON_BOOTS + "");
		lvl4.add(Material.CHAINMAIL_HELMET + "");
		lvl4.add(Material.FLINT_AND_STEEL + "");
		lvl4.add(Material.GOLD_BOOTS + "");
		lvl4.add(Material.STONE_SWORD + "");
		lvl4.add(Material.WOOD_SWORD + "");
		lvl4.add(Material.STRING + " 2");
		
		c.addDefault("Chestloot.Level 4", lvl4);
		
		
		List<String> lvl5 = new ArrayList<>();
		lvl5.add(Material.DIAMOND + " 2");
		lvl5.add(Material.IRON_INGOT + "");
		lvl5.add(Material.STICK + " 2");
		lvl5.add(Material.CAKE + "");
		lvl5.add(Material.FERMENTED_SPIDER_EYE + "");
		lvl5.add(Material.BOW + ":168");
		lvl4.add(Material.STONE_SWORD + " name:&eSword_of_Herobrine enchant:KNOCKBACK,1 enchant:DAMAGE_ALL,1");
		lvl5.add(Material.POTION + " effect:regeneration,10,1 name:&cRegeneration");
		lvl5.add(Material.POTION + " effect:jump,18,1 effect:speed,18,2 name:&ePotion_of_a_rabbit lore:&7Give_you_the//&7abilities_of_a_rabbit!");
		c.addDefault("Chestloot.Level 5", lvl5);
		
		c.addDefault("Chest-Title", "Survival Chest");
		c.options().header(
				"##### UltimateSurvivalGames Chestloot Configuration #####\n" +
				"\n" +
				"## How does this work? ##\n" +
				"The chestloot is splitted into 5 lists. You can add unlimited items to each list.\n" +
				"In one chest can spawn up to 8 itemstacks. For each itemstack, the plugin choose\n" +
				"one list. The following lists under this text have different spawn changes:\n" +
				"\n" +
				"Level 1: 40 %\n" +
				"Level 2: 30 %\n" +
				"Level 3: 15 %\n" +
				"Level 4: 10 %\n" +
				"Level 5: 5 %\n" +
				"\n" +
				"If the plugin has choosed a list for an itemstack, it takes a item random from the list.\n" +
				"\n" +
				"## How can I modify the items? ##\n" +
				"You can add or remove items from all lists. But at least one item has to be on each list.\n" +
				"\n" +
				"## How do I format the items? ##\n" +
				"MATERIAL/ITEMID[:SUBID] [AMOUNT] [SPECIAL THINGS]\n" +
				"Here are some examples:\n" +
				"\n" +
				"# Normal Item:\n" +
				"\"BREAD\" - is the same like \"BREAD 1\", \"BREAD:0 1\" or \"297:0 1\"\n" +
				"\n" +
				"# If you want to set a predefined durability-level, just change the subid:\n" +
				"\"STONE_SWORD:10\" - This tool has already 10 uses lost.\n" +
				"\n" +
				"# You can also add enchantments to an item:\n" +
				"\"STONE_SWORD enchant:KNOCKBACK,2 enchant:DAMAGE_ALL,3\" - This item has knockback 2 and sharpness 3! Note: Only the vanilla level of an enchantment can be used!\n" +
				"\n" +
				"# You can also set a custom name and lore for an item:\n" +
				"\"EGG name:&eEaster_Egg lore:&7Throw//&7me!\" - This is an egg with a displayname \"Easter Egg\" and the lore \"Throw me\"! Note: Spaces are \"_\" and line breaks in lore the charakters \"//\"\n");
		
		c.options().copyDefaults(true);
		SurvivalGames.saveChests();
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
		c.addDefault("Sign.Leave.Line.2", "");
		c.addDefault("Sign.Leave.Line.3", "&oRightclick");
		c.addDefault("Sign.Leave.Line.4", "&oto leave!");
		
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
		if(c.contains("Chestloot"))
			c.set("Chestloot", null);
		

		
		ArrayList<String> allowedCmds = new ArrayList<>();
		allowedCmds.add("/sg");
		allowedCmds.add("/hg");
		allowedCmds.add("/hungergames");
		allowedCmds.add("/survivalgames");
		c.addDefault("Allowed-Commands", allowedCmds);
		
		
		c.addDefault("Voting.Item", Material.CHEST + " name:&eVote_for_an_arena lore:&7Rightclick_to_open//&7the_voting_menu!");
		c.addDefault("Voting.InventoryTitle", "Vote for an arena!");
		c.addDefault("Voting.ArenaItem", Material.EMPTY_MAP + " 0 lore:&7Click_to_vote//&7for_this_arena!");
		c.addDefault("Leave-Item", Material.MAGMA_CREAM + " name:&eLeave_the_lobby lore:&7Rightclick_to_leave//&7the_lobby!");
		
		c.addDefault("Spectating.Enabled", true);
		c.addDefault("Spectating.Max-Spectators-Per-Arena", 8);
		c.addDefault("Spectating.Player-Navigator.Item", Material.COMPASS + " name:&ePlayer Navigator lore:&7Rightclick_to_open//&7the_player_navigator!");
		c.addDefault("Spectating.Player-Navigator.Inventory-Title", "Click on a item to spectate!");
		
		c.addDefault("Separating.Chat.Enabled", true);
		
		List<String> joinfull = new ArrayList<>();
		joinfull.add("sg.donator.vip.iron");
		joinfull.add("sg.donator.vip.gold");
		joinfull.add("sg.donator.moderator");
		joinfull.add("sg.donator.admin");
		c.addDefault("Donator-Permissions.Join-Full-Arena", joinfull);
		
		List<String> votePower = new ArrayList<>();
		votePower.add("sg.donator.vip.iron//2");
		votePower.add("sg.donator.vip.gold//2");
		c.addDefault("Donator-Permissions.Extra-Vote-Power", votePower);
		c.addDefault("TNT-Extra-Damage", 7.0);
		
		c.addDefault("Enable-Arena-Reset", true);
		
		c.options().copyDefaults(true);
		SurvivalGames.instance.saveConfig();
		
		if(!c.getBoolean("Enable-Arena-Reset")) {
			System.out.println("[SurvivalGames] Warning: Arena map reset ist disabled.");
		}
	}
	
	public static void reloadDatabase() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "database.yml").getFileConfiguration();
		SurvivalGames.database = c;
	}
	
	public static void reloadMessages() {
		FileConfiguration c = new DatabaseLoader("plugins/SurvivalGames", "messages.yml").getFileConfiguration();
		SurvivalGames.messages = c;
		
		c.addDefault("prefix", "&7[&3SG&7] &6");
		c.addDefault("no-permission", "&cYou don't have permission to do this!");
		c.addDefault("cmd-error", "&cError: %0%");
		
		c.addDefault("join-unknown-game", "&cThe lobby %0% does not exist!");
		c.addDefault("join-game-running", "&cThis game is already running!");
		c.addDefault("join-vehicle", "&cYou can't join SurvivalGames in a vehicle!");
		c.addDefault("join-game-full", "&cSorry, this lobby is full!");
		c.addDefault("join-success", "%0% joined the lobby! &7(&e%1%&7/&e%2%&7)");
		c.addDefault("fulljoin-kick", "&cI'm sorry, you've been kicked to make a free slot for a donator or a team member!");
		c.addDefault("join-already-playing", "&cYou're already playing!");
		c.addDefault("leave-not-playing", "&cYou aren't playing!");
		c.addDefault("game-leave", "%0% left the lobby! &7(&e%1%&7/&e%2%&7)");
		c.addDefault("game-cooldown", "The game starts in &b%0%");
		
		c.addDefault("spectator-join", "%0% joined the game as spectator!");
		c.addDefault("spectator-full", "&cThe lobby is full. There can be up to %0% spectators in a lobby!");
		c.addDefault("spectator-game-running", "&cThis game isn't running!");
		c.addDefault("spectator-not-living", "&cPlayer %0% isn't alive anymore.");
		c.addDefault("spectator-new-player", "You're now specatating %0%!");
		c.addDefault("spectator-disabled", "&cSpectating is disabled!");
		
		c.addDefault("game-waiting-cooldown-big", "The voting ends in %0% seconds");
		c.addDefault("game-waiting-cooldown-little", "The voting ends in %0%");
		c.addDefault("game-waiting-end", "The waiting phase has been ended!");
		
		c.addDefault("game-deathmatch-cooldown", "The final deathmatch starts in &b%0%");
		c.addDefault("game-deathmatch-start", "Let's start the final deathmatch!");
		c.addDefault("game-deathmatch-timeout", "The deathmatch ends automaticly in %0% seconds!");
		c.addDefault("game-deathmatch-timeout-warning", "When the deathmatch ends automaticly, the winner will be choosed random!");
		
		c.addDefault("game-player-die-killer", "%0% was killed by %1%!");
		c.addDefault("game-player-die-damage", "%0% has died and gone from us!");
		c.addDefault("game-player-left", "%0% left the lobby!");
		c.addDefault("game-remainplayers", "&b%0%&6 tributes remain.");
		
		c.addDefault("game-grace-period", "&bYou have %0% seconds grace-period!");
		c.addDefault("game-grace-period-ended", "&bThe grace-period has been ended!");
		
		c.addDefault("game-voting-cooldown", "The voting ends in &b%0%");
		c.addDefault("game-voting-end", "The voting phrase has been ended!");
		c.addDefault("game-no-vote", "&cYou can only vote in the voting phase of the game!");
		c.addDefault("game-bad-vote", "&cThis isn't a valid vote ID!");
		c.addDefault("game-already-vote", "&cYou've already voted for an arena!");
		c.addDefault("game-no-voting-enabled", "&cSorry, voting isn't enabled! The arena will choosed random!");
		c.addDefault("game-success-vote", "You've voted successfully for arena &b%0%&6!");
		c.addDefault("game-extra-vote", "You've voted with &b%0% &6votes!");
		c.addDefault("game-start-canceled", "Not enough players are in this lobby. Cancel Timer...");
		c.addDefault("game-start", "The round begins, &b%0% &6players are playing! &bGood luck&6!");
		c.addDefault("game-chestrefill", "It's midnight! All chests are refilled!");
		c.addDefault("game-win", "%0% won the SurvivalGames in arena %1% in lobby %2%!");
		c.addDefault("game-win-winner-message", "&bCongratulations!&6 You've won the SurvivalGames in arena &b%0%&6!");
		
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
		c.addDefault("game-spawn-set", "You've set the spawn for lobby %0% successfully!");
		c.addDefault("game-set-spawn", "To set the spawn of this lobby, type /sg lobby setspawn %0%");
		c.addDefault("game-not-found", "&cThe lobby %0% does not exists!");
		c.addDefault("game-must-enter", "&cYou must enter a name: %0%");
		c.addDefault("game-vote", "Vote for an arena: &b/sg vote <ID>");
		c.addDefault("forbidden-command", "&cYou can't execute this command in SurvivalGames!");
		c.addDefault("forbidden-build", "&cYou aren't allowed to build in a SurvivalGames arena!");
		
		c.addDefault("arena-already-exists", "&cThe arena %0% already exists in lobby %1%!");
		c.addDefault("arena-must-select", "&cPlease select an arena with %0%!");
		c.addDefault("arena-created", "You've created arena %0% in lobby %1% successfully!");
		c.addDefault("arena-selected", "You've selected arena %0% in lobby %1%!");
		c.addDefault("arena-not-found", "&cThe arena %0% does not exists in lobby %1%!");
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
