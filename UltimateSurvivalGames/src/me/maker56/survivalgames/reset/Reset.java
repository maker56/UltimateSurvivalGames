package me.maker56.survivalgames.reset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.events.ResetDoneEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Reset extends Thread {
	
	private static int sleep = 10;
	private static List<String> resets = new ArrayList<>();
	
	public static boolean isResetting(String lobby, String arena) {
		return resets.contains(lobby + arena);
	}
	
	public static boolean isResseting(String lobby) {
		for(String key : resets) {
			if(key.startsWith(lobby))
				return true;
		}
		return false;
	}
	
	private String lobby, arena;
	private World world;
	private long start;
	private List<String> chunks = new ArrayList<>();
	
	private List<String> cReset = new ArrayList<>();
	private boolean build = false;
	
	public Reset(World w, String lobby, String arena, List<String> chunks) {
		this.world = w;
		this.lobby = lobby;
		this.arena = arena;
		this.chunks = chunks;
	}
	
	@Override
	public void run() {
		if(isResetting(lobby, arena))
			return;
		System.out.println("[SurvivalGames] Start arena reset... (arena " + arena + ", lobby " + lobby + ")");
		setPriority(MIN_PRIORITY);
		resets.add(lobby + arena);
		start = System.currentTimeMillis();
		
		File file = new File("plugins/SurvivalGames/reset/" + lobby + arena + ".map");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			String line;
			boolean add = false;
			while((line = br.readLine()) != null) {
				if(line.startsWith(";")) {
					if(add) {
						reset();
						while(build)
							sleep(sleep);
						cReset.clear();
						add = false;
					}
					String chunkKey = line.substring(1);
					if(chunks.contains(chunkKey)) {
						add = true;
					}
				} else if(add) {
					cReset.add(line);
				}
			}
			br.close();
		} catch (Exception | OutOfMemoryError e) {
			e.printStackTrace();
			resets.remove(lobby + arena);
			return;
		}

		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() {
				resets.remove(lobby + arena);
				
				SurvivalGames.reset.set("Startup-Reset." + lobby + "." + arena, null);
				SurvivalGames.saveReset();
			
				int time = (int) ((System.currentTimeMillis() - start) / 1000);
				System.out.println("[SurvivalGames] Finished arena reset! (arena " + arena + ", lobby " + lobby + ") Time: " + time + " seconds!");
				Bukkit.getPluginManager().callEvent(new ResetDoneEvent(lobby, arena, time));
				return null;
			}
		});
	}
	
	private void reset() {
		build = true;
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@SuppressWarnings("deprecation")
			@Override
			public Void call() {
				for(String restoreKey : cReset) {
					if(restoreKey.startsWith(";"))
						continue;
					String[] key = restoreKey.split(",");
					Location loc = new Location(world, Integer.parseInt(key[0]), Integer.parseInt(key[1]), Integer.parseInt(key[2]));
					
					Block b = loc.getBlock();
					if(key.length == 5) {
						int i = Integer.parseInt(key[3]);
						byte by = Byte.parseByte(key[4]);
						if(b.getTypeId() != i || b.getData() != by) {
							b.setTypeIdAndData(i, by, false);
						}
					} else if(key.length == 4) {
						int i = Integer.parseInt(key[3]);
						if(b.getTypeId() != i) {
							b.setTypeId(i, false);
						}
					} else {
						if(b.getTypeId() != 0) {
							b.setTypeId(0, false);
						}
					}
				}

				build = false;
				return null;
			}
			
		});
	}

}
