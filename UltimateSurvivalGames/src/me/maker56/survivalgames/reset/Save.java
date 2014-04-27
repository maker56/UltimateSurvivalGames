package me.maker56.survivalgames.reset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.commands.messages.MessageHandler;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Save extends Thread {
	
	private PrintWriter pw;
	private String lobby, arena;
	
	private Location min, max;
	private long start;
	
	private BukkitTask task;
	private double writeStepsDone, writeSteps;
	private String player;
	
	public Save(String lobby, String arena, Location min, Location max, String player) {
		this.lobby = lobby;
		this.arena = arena;
		this.min = min;
		this.max = max;
		this.player = player;
	}
	
	private void startPercentTask() {
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				float percent = Math.round((float) (writeStepsDone / (writeSteps / 100)));
				if(percent > 100)
					return;
				Player p = Bukkit.getPlayer(player); // TODO
				if(p != null)
					p.sendMessage(MessageHandler.getMessage("prefix") + "§eArena save lobby " + lobby + " arena " + arena + ": " + percent + "% done...");
			}
		}, 100, 200);
	}
	
	private List<Chunk> chunks = new ArrayList<>();
	private Chunk chunk;
	private World world;
	
	@Override
	public void run() {
		saves.add(lobby + arena);
		start = System.currentTimeMillis();
		while(Reset.isResetting(lobby, arena)) {
			try {
				sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		File file = new File("plugins/SurvivalGames/reset/" + lobby + arena + ".map");
		
		file.mkdirs();
		if(file.exists()) {
			file.delete();
		}
		
		
		
		try {
			 pw = new PrintWriter(new FileWriter("plugins/SurvivalGames/reset/" + lobby + arena + ".map", true), true);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			pw.close();
			return;
		}
		
		final int xMin = Math.min(min.getBlockX(), max.getBlockX());
		final int zMin = Math.min(min.getBlockZ(), max.getBlockZ());
		
		final int xMax = Math.max(min.getBlockX(), max.getBlockX());
		final int zMax = Math.max(min.getBlockZ(), max.getBlockZ());
		
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Location chunkloc = new Location(min.getWorld(), 0, 0, 0);
				for(int x = xMin; x <= xMax; x++) {
					chunkloc.setX(x);
					for(int z = zMin; z <= zMax; z++) {
						chunkloc.setZ(z);
						Chunk c = min.getWorld().getChunkAt(chunkloc);
						if(!chunks.contains(c))
							chunks.add(c);
					}
				}
				world = min.getWorld();
				return null;
			}
		});
		
		while(world == null) {
			try {
				sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		writeSteps = chunks.size();
		startPercentTask();
		
		for(Chunk c : chunks) {
			chunk = c;
			
			Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
				@SuppressWarnings("deprecation")
				@Override
				public Void call() throws Exception {
					pw.println(";" + chunk.getX() + "," + chunk.getZ());
					for(int x = 0; x < 16; x++) {
						for(int z = 0; z < 16; z++) {
							for(int y = 0; y < world.getMaxHeight(); y++) {
								Block b = chunk.getBlock(x, y, z);
								Location loc = b.getLocation();
								String save = loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
								int id = b.getTypeId();
								if(id != 0) {
									save += "," + id;
									short data = b.getData();
									if(data != 0) {
										save += "," + data;
									}
								}
								pw.println(save);
							}
						}
					}
					writeStepsDone++;
					chunk = null;
					return null;
				}
			});
			
			while(chunk != null) {
				try {
					sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		chunks.clear();
		task.cancel();
		pw.close();
		saves.remove(lobby + arena);
		size = (int) (file.length() / 1000);
		format = "KiloByte";
		if(size >= 1000) {
			size = size / 1000;
			format = "MegaByte";
		}

		
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				Bukkit.getPluginManager().callEvent(new SaveDoneEvent(lobby, arena, (System.currentTimeMillis() - start) / 1000, size, format));
				return null;
			}
		});
	}
	
	int size;
	String format;
	
	// STATIC
	private static List<String> saves = new ArrayList<>();
	public static boolean isSaveing(String lobby, String arena) {
		return saves.contains(lobby + arena);
	}
	
	

}
