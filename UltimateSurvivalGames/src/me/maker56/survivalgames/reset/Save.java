package me.maker56.survivalgames.reset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.events.SaveDoneEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.TileEntityBlock;

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

		Location min = sel.getMinimumLocation();
		Location max = sel.getMaximumLocation();
		
		cc = new CuboidClipboard(new Vector(sel.getLength(), sel.getHeight(), sel.getWidth()), new Vector(min.getBlockX(), min.getY(), min.getZ()), new Vector(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
		

		if(pname != null)
			startPercentInfoScheduler();
		
	    width = cc.getWidth();
	    length = cc.getLength();
	    height = cc.getHeight();
	    origin = cc.getOrigin();

	    HashMap<String, Tag> schematic = new HashMap<>();
	    schematic.put("Width", new ShortTag("Width", (short)width));
	    schematic.put("Length", new ShortTag("Length", (short)length));
	    schematic.put("Height", new ShortTag("Height", (short)height));
	    schematic.put("Materials", new StringTag("Materials", "Alpha"));
	    schematic.put("WEOriginX", new IntTag("WEOriginX", cc.getOrigin().getBlockX()));
	    schematic.put("WEOriginY", new IntTag("WEOriginY", cc.getOrigin().getBlockY()));
	    schematic.put("WEOriginZ", new IntTag("WEOriginZ", cc.getOrigin().getBlockZ()));
	    schematic.put("WEOffsetX", new IntTag("WEOffsetX", cc.getOffset().getBlockX()));
	    schematic.put("WEOffsetY", new IntTag("WEOffsetY", cc.getOffset().getBlockY()));
	    schematic.put("WEOffsetZ", new IntTag("WEOffsetZ", cc.getOffset().getBlockZ()));

	    blocks = new byte[width * height * length];
	    addBlocks = null;
	    blockData = new byte[width * height * length];
	    tileEntities = new ArrayList<>();

		int maxSize = 6 * 16 * 16 * cc.getHeight();
		maxSteps = (cc.getHeight() * cc.getLength() * cc.getWidth()) / maxSize;
		long sleep = 625;
		
		Util.debug("SurvivalGames Map save - length:" + cc.getLength() + " width:" + cc.getWidth() + " height:" + cc.getHeight() + " perStep:" + maxSize + " maxSteps:" + maxSteps + " sleep:" + sleep);
		
		for(LocalWorld lw : WorldEdit.getInstance().getServer().getWorlds()) {
			if(lw.getName().equals(min.getWorld().getName())) {
				es = WorldEdit.getInstance().getEditSessionFactory().getEditSession(lw, 0);
				break;
			}
		}
	    
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
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
		while(save) {
			try {
				sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	    schematic.put("Blocks", new ByteArrayTag("Blocks", blocks));
	    schematic.put("Data", new ByteArrayTag("Data", blockData));
	    schematic.put("Entities", new ListTag("Entities", CompoundTag.class, new ArrayList<CompoundTag>()));
	    schematic.put("TileEntities", new ListTag("TileEntities", CompoundTag.class, tileEntities));
	    if (addBlocks != null) {
	      schematic.put("AddBlocks", new ByteArrayTag("AddBlocks", addBlocks));
	    }

	    CompoundTag schematicTag = new CompoundTag("Schematic", schematic);
	    NBTOutputStream stream;
		try {
			stream = new NBTOutputStream(new FileOutputStream(file));
		    stream.writeTag(schematicTag);
		    stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		sizeB = file.length();
		format = "Bytes";
		if(sizeB >= 1000) {
			sizeB = sizeB / 1000;
			format = "KiloBytes";
			if(sizeB >= 1000) {
				sizeB = sizeB / 1000;
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
				Bukkit.getPluginManager().callEvent(new SaveDoneEvent(lobby, arena,  (System.currentTimeMillis() - start), sizeB, format));
				return null;
			}
		});
	}
	
	private long sizeB;
	private String format;
	
	private boolean save = false;
	private CuboidClipboard cc;
	private EditSession es;
	private Vector origin;
	private List<Vector> cSave = new ArrayList<>();
	
	byte[] blocks;
	byte[] addBlocks;
	byte[] blockData;
	ArrayList<CompoundTag> tileEntities;
	int width;
    int height;
    int length;
	
	public void nextSave() {
		save = true;
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() {
				try {
					Util.debug("Start save: " + cSave.size() + " blocks");
					for(Vector v : cSave) {
						int x = v.getBlockX();
						int y = v.getBlockY();
						int z = v.getBlockZ();
						
						int index = y * width * length + z * width + x;
						
	                    BaseBlock block = es.getBlock(new BlockVector(x, y, z).add(origin));

	                    // Save 4096 IDs in an AddBlocks section
	                    if (block.getType() > 255) {
	                        if (addBlocks == null) { // Lazily create section
	                            addBlocks = new byte[(blocks.length >> 1) + 1];
	                        }

	                        addBlocks[index >> 1] = (byte) (((index & 1) == 0) ?
	                                addBlocks[index >> 1] & 0xF0 | (block.getType() >> 8) & 0xF
	                                : addBlocks[index >> 1] & 0xF | ((block.getType() >> 8) & 0xF) << 4);
	                    }

	                    blocks[index] = (byte) block.getType();
	                    blockData[index] = (byte) block.getData();

	                    // Store TileEntity data
	                    if (block instanceof TileEntityBlock) {
	                        TileEntityBlock tileEntityBlock = block;

	                        // Get the list of key/values from the block
	                        CompoundTag rawTag = tileEntityBlock.getNbtData();
	                        if (rawTag != null) {
	                            Map<String, Tag> values = new HashMap<String, Tag>();
	                            for (Entry<String, Tag> entry : rawTag.getValue().entrySet()) {
	                                values.put(entry.getKey(), entry.getValue());
	                            }
	                            
	                            values.put("id", new StringTag("id", tileEntityBlock.getNbtId()));
	                            values.put("x", new IntTag("x", x));
	                            values.put("y", new IntTag("y", y));
	                            values.put("z", new IntTag("z", z));
	                            
	                            CompoundTag tileEntityTag = new CompoundTag("TileEntity", values);
	                            tileEntities.add(tileEntityTag);
	                        }
	                    }
					}
					stepsDone++;
					cSave.clear();
				} catch(Exception e) {
					e.printStackTrace();
					
				}

				Util.debug("end save");
				save = false;
				return null;
			}
		});
	}
	

}
