package me.maker56.survivalgames.reset;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.events.ResetDoneEvent;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;

import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.ListTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.TileEntityBlock;
import com.sk89q.worldedit.data.DataException;

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
	
	public Reset(World w, String lobby, String arena, List<String> chunks) {
		this.world = w;
		this.lobby = lobby;
		this.arena = arena;
		this.chunks = chunks;
		
		System.out.println("[SurvivalGames] Remove old Entitys from " + w.getName());
		int ents = 0;
		for(Entity e : w.getEntities()) {
			if(e instanceof Item || e instanceof LivingEntity || e instanceof Arrow) {
				e.remove();
				ents++;
			}
		}
		System.out.println("[SurvivalGames] Removed " + ents + " from " + w.getName());
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
			NBTInputStream nbtStream = new NBTInputStream( new GZIPInputStream(new FileInputStream(file)));

	        Vector origin = new Vector();
	        Vector offset = new Vector();

	        // Schematic tag
	        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
	        nbtStream.close();
	        if (!schematicTag.getName().equals("Schematic")) {
	            throw new DataException("Tag \"Schematic\" does not exist or is not first");
	        }

	        // Check
	        Map<String, Tag> schematic = schematicTag.getValue();
	        if (!schematic.containsKey("Blocks")) {
	            throw new DataException("Schematic file is missing a \"Blocks\" tag");
	        }

	        // Get information
	        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
	        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
	        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

	        try {
	            int originX = getChildTag(schematic, "WEOriginX", IntTag.class).getValue();
	            int originY = getChildTag(schematic, "WEOriginY", IntTag.class).getValue();
	            int originZ = getChildTag(schematic, "WEOriginZ", IntTag.class).getValue();
	            origin = new Vector(originX, originY, originZ);
	        } catch (DataException e) {
	            // No origin data
	        }

	        try {
	            int offsetX = getChildTag(schematic, "WEOffsetX", IntTag.class).getValue();
	            int offsetY = getChildTag(schematic, "WEOffsetY", IntTag.class).getValue();
	            int offsetZ = getChildTag(schematic, "WEOffsetZ", IntTag.class).getValue();
	            offset = new Vector(offsetX, offsetY, offsetZ);
	        } catch (DataException e) {
	            // No offset data
	        }

	        // Check type of Schematic
	        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
	        if (!materials.equals("Alpha")) {
	            throw new DataException("Schematic file is not an Alpha schematic");
	        }

	        // Get blocks
	        byte[] blockId = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
	        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
	        byte[] addId = new byte[0];
	        short[] blocks = new short[blockId.length]; // Have to later combine IDs

	        // We support 4096 block IDs using the same method as vanilla Minecraft, where
	        // the highest 4 bits are stored in a separate byte array.
	        if (schematic.containsKey("AddBlocks")) {
	            addId = getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
	        }

	        // Combine the AddBlocks data with the first 8-bit block ID
	        for (int index = 0; index < blockId.length; index++) {
	            if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
	                blocks[index] = (short) (blockId[index] & 0xFF);
	            } else {
	                if ((index & 1) == 0) {
	                    blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
	                } else {
	                    blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
	                }
	            }
	        }

	        // Need to pull out tile entities
	        List<Tag> tileEntities = getChildTag(schematic, "TileEntities", ListTag.class)
	                .getValue();
	        Map<BlockVector, Map<String, Tag>> tileEntitiesMap =
	                new HashMap<BlockVector, Map<String, Tag>>();

	        for (Tag tag : tileEntities) {
	            if (!(tag instanceof CompoundTag)) continue;
	            CompoundTag t = (CompoundTag) tag;

	            int x = 0;
	            int y = 0;
	            int z = 0;

	            Map<String, Tag> values = new HashMap<String, Tag>();

	            for (Map.Entry<String, Tag> entry : t.getValue().entrySet()) {
	                if (entry.getKey().equals("x")) {
	                    if (entry.getValue() instanceof IntTag) {
	                        x = ((IntTag) entry.getValue()).getValue();
	                    }
	                } else if (entry.getKey().equals("y")) {
	                    if (entry.getValue() instanceof IntTag) {
	                        y = ((IntTag) entry.getValue()).getValue();
	                    }
	                } else if (entry.getKey().equals("z")) {
	                    if (entry.getValue() instanceof IntTag) {
	                        z = ((IntTag) entry.getValue()).getValue();
	                    }
	                }

	                values.put(entry.getKey(), entry.getValue());
	            }

	            BlockVector vec = new BlockVector(x, y, z);
	            tileEntitiesMap.put(vec, values);
	        }

	        Vector size = new Vector(width, height, length);
	        CuboidClipboard clipboard = new CuboidClipboard(size);
	        clipboard.setOrigin(origin);
	        clipboard.setOffset(offset);
	        
	        for(LocalWorld lws : we.getServer().getWorlds()) {
				if(lws.getName().equals(world.getName())) {
					lw = lws;
					break;
				}
			}
			int next = 16 * 16 * world.getMaxHeight();
			es = we.getEditSessionFactory().getEditSession(lw, -1);
			long sleep = 100;
	        
	        for (int x = 0; x < width; ++x) {
	            for (int y = 0; y < height; ++y) {
	                for (int z = 0; z < length; ++z) {
	                	if(cReset.size() >= next) {
							resetNext();
							while(build) {
								sleep(sleep);
							}
						}
	                	
	                    BlockVector pt = new BlockVector(x, y, z);
	                    Vector pos = pt.add(origin);
	                    
	                    int cx = (int) Math.floor(pos.getBlockX() / 16.0D);
						int cz = (int) Math.floor(pos.getBlockZ() / 16.0D);
						
	                    
	                    int index = y * width * length + z * width + x;
	                    BaseBlock block = new BaseBlock(blocks[index], blockData[index]);

	                    if (block instanceof TileEntityBlock && tileEntitiesMap.containsKey(pt)) {
	                        try {
	                            ((TileEntityBlock) block).setNbtData(new CompoundTag("", tileEntitiesMap.get(pt)));
	                        } catch (DataException e) {
	                            throw new DataException(e.getMessage());
	                        }
	                    }
	                    
	                    if(chunks.contains(cx + "," + cz)) {
							cReset.put(pos, block);
						}
	                    
	                }
	            }
	        }
	        resetNext();
			while(build) {
				sleep(sleep);
			}
	        
		} catch (IOException | DataException | InterruptedException e) {
			e.printStackTrace();
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
		Util.debug("reset next!");
		Bukkit.getScheduler().callSyncMethod(SurvivalGames.instance, new Callable<Void>() {
			@Override
			public Void call() {
				for(Entry<Vector, BaseBlock> map : cReset.entrySet()) {
					try {
						es.setBlock(map.getKey(), map.getValue());
					} catch (MaxChangedBlocksException e) {
						e.printStackTrace();
					}
				}
				cReset.clear();
				build = false;
				return null;
			}
		});
	}
	
	private static <T extends Tag> T getChildTag(Map<String, Tag> items,
			String key, Class<T> expected) throws DataException {

		if (!items.containsKey(key)) {
			throw new DataException("Schematic file is missing a \"" + key
					+ "\" tag");
		}
		Tag tag = items.get(key);
		if (!expected.isInstance(tag)) {
			throw new DataException(key + " tag is not of tag type "
					+ expected.getName());
		}
		return expected.cast(tag);
	}


}
