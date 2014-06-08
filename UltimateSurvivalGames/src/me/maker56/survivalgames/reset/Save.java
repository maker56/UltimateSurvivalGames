package me.maker56.survivalgames.reset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.events.SaveDoneEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class Save extends Thread {
	
	private String lobby, arena;
	
	private Selection sel;
	private long start;

	public Save(String lobby, String arena, Selection sel) {
		this(lobby, arena, sel, null);
	}
	
	public Save(String lobby, String arena, Selection sel, String pname) {
		this.lobby = lobby;
		this.sel = sel;
		this.arena = arena;
		this.pname = pname;
	}
	
	// PERCENT CALCULATION
	private double maxSteps, stepsDone = 0;
	private BukkitTask task;
	private String pname;
	
	public void startPercentInfoScheduler() {
		task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.instance, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				Player p = Bukkit.getPlayer(pname);
				if(p != null) {
					double percent = stepsDone / (maxSteps / 100);
					double rounded = Math.round( percent * 100. ) / 100.;
					if(rounded <= 100) {
						p.sendMessage(MessageHandler.getMessage("prefix") + "§eMap save of arena " + arena + " in lobby " + lobby + " " + Double.valueOf(rounded).toString().replace(".", ",") + "% completed!");
						Util.debug(stepsDone + "/" + maxSteps);
					}
				}
			}
		}, 70, 60);
	}
	
	
	@Override
	public void run() {
		start = System.currentTimeMillis();
		File file = new File("plugins/SurvivalGames/reset/" + lobby + arena + ".map");
		
		file.mkdirs();
		if(file.exists()) {
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		
		SchematicFormat sf = SchematicFormat.MCEDIT;
		Location min = sel.getMinimumLocation();
		Location max = sel.getMaximumLocation();
		
		setPriority(MIN_PRIORITY);
		WorldEdit we = SurvivalGames.getWorldEdit().getWorldEdit();
		try {
			for(LocalWorld lw : we.getServer().getWorlds()) {
				if(lw.getName().equals(min.getWorld().getName())) {
					es = we.getEditSessionFactory().getEditSession(lw, 0);
					break;
				}
			}
			
			cc = new CuboidClipboard(new Vector(sel.getLength(), sel.getHeight(), sel.getWidth()), new Vector(min.getBlockX(), min.getY(), min.getZ()), new Vector(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
			Vector size = cc.getSize();
			origin = cc.getOrigin();
			
			int maxSize = 16 * 16 * cc.getHeight();
			maxSteps = (cc.getHeight() * cc.getLength() * cc.getWidth()) / maxSize;
			long sleep = 125;
			
			Util.debug("SurvivalGames Map save - length:" + cc.getLength() + " width:" + cc.getWidth() + " height:" + cc.getHeight() + " perStep:" + maxSize + " maxSteps:" + maxSteps + " sleep:" + sleep);
			
			if(pname != null)
				startPercentInfoScheduler();
			
			for(int x = 0; x < size.getBlockX(); x++) {
				for(int z = 0; z < size.getBlockZ(); z++) {
					for(int y = 0; y < size.getBlockY(); y++) {
						if(cSave.size() == maxSize) {
							nextSave();
							while(save) { 
								try {
									sleep(sleep);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}

						cSave.add(new Vector(x, y, z));
					}
		        }     
			}
			nextSave();
			
			sf.save(cc, file);
		} catch (IOException | DataException e) {
			e.printStackTrace();
		}
		
		size = file.length();
		format = "Bytes";
		if(size >= 1000) {
			size = size / 1000;
			format = "KiloBytes";
			if(size >= 1000) {
				size = size / 1000;
				format = "MegaBytes";
			}
		}

		if(task != null)
			task.cancel();
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				// TEMPORARY
				Util.checkForOutdatedArenaSaveFiles();
				Bukkit.getPluginManager().callEvent(new SaveDoneEvent(lobby, arena,  (System.currentTimeMillis() - start), size, format));
				return null;
			}
		});
	}
	
	private long size;
	private String format;
	
	private boolean save = false;
	private CuboidClipboard cc;
	private Vector origin;
	private EditSession es;
	private List<Vector> cSave = new ArrayList<>();
	
	public void nextSave() {
		save = true;
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for(Vector v : cSave) {
					cc.setBlock(v, es.getBlock(v.add(origin)));
				}
				cSave.clear();
				stepsDone++;
				save = false;
				return null;
			}
		});
	}
	

}
