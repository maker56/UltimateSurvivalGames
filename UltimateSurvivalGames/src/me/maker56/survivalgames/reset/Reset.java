package me.maker56.survivalgames.reset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.events.ResetDoneEvent;

import org.bukkit.Bukkit;
import org.bukkit.World;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class Reset extends Thread {
	
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
	
	private HashMap<Vector, BaseBlock> cReset = new HashMap<>();
	private boolean build = false;
	
	private CuboidClipboard cc;
	
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
		resets.add(lobby + arena);
		start = System.currentTimeMillis();
		
		File file = new File("plugins/SurvivalGames/reset/" + lobby + arena + ".map");

		try {
			cc = SchematicFormat.MCEDIT.load(file);
		} catch (IOException | DataException e) {
			e.printStackTrace();
		}

		try {
			
			Vector size = cc.getSize();
			
			for(LocalWorld lws : we.getServer().getWorlds()) {
				if(lws.getName().equals(world.getName())) {
					lw = lws;
					break;
				}
			}
			int next = 16 * 16 * world.getMaxHeight();
			es = we.getEditSessionFactory().getEditSession(lw, -1);

			Vector pos = cc.getOrigin();
			
			for(int x = 0; x < size.getBlockX(); x++) {
				for(int z = 0; z < size.getBlockZ(); z++) {
					for(int y = 0; y < size.getBlockY(); y++) {
						if(cReset.size() >= next) {
							resetNext();
							while(build) {
								sleep(100);
							}
						}
						
						Vector vw = new Vector(x, y, z);
						Vector v = vw.add(pos);

						int cx = (int) Math.floor(v.getBlockX() / 16.0D);
						int cz = (int) Math.floor(v.getBlockZ() / 16.0D);
						
						String chunk = cx + "," + cz;

						if(chunks.contains(chunk)) {
							cReset.put(v, cc.getBlock(vw));
						}
					}
		        }     
			}
			resetNext();
			while(build) {
				sleep(10);
			}
			
		} catch (InterruptedException e) {
			System.out.println("[SurvivalGames] Can't reset arena " + arena + " in lobby " + lobby + "! Try to resave your file! /sg arena save");
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
	
	WorldEdit we = SurvivalGames.getWorldEdit().getWorldEdit();
	LocalWorld lw = null;
	EditSession es = null;

	public void resetNext() {
		build = true;
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() {
				for(Entry<Vector, BaseBlock> map : cReset.entrySet()) {
					try {
						es.setBlock(map.getKey(), map.getValue());
					} catch (MaxChangedBlocksException e) {
						System.err.println(e.getBlockLimit());
						build = false;
						return null;
					}
				}
				cReset.clear();
				build = false;
				return null;
			}
		});
	}


}
