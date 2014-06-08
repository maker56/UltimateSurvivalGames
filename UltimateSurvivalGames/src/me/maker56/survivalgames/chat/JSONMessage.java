package me.maker56.survivalgames.chat;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.maker56.survivalgames.user.SpectatorUser;
import me.maker56.survivalgames.user.User;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JSONMessage {
	
	/* The MIT License (MIT)
	 * 
	 * Copyright (c) 2013-2014 Max Kreminski
	 * 
	 * Permission is hereby granted, free of charge, to any person obtaining a
	 * copy of this software and associated documentation files (the
	 * "Software"), to deal in the Software without restriction, including
	 * without limitation the rights to use, copy, modify, merge, publish,
	 * distribute, sublicense, and/or sell copies of the Software, and to permit
	 * persons to whom the Software is furnished to do so, subject to the
	 * following conditions:
	 * 
	 * The above copyright notice and this permission notice shall be included
	 * in all copies or substantial portions of the Software.
	 * 
	 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
	 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
	 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
	 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
	 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
	 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
	 * USE OR OTHER DEALINGS IN THE SOFTWARE. */
	
	private final List<MessagePart> messageParts;
	private String jsonString;
	private boolean dirty;
	
	private Class<?> nmsChatSerializer = ReflectionUtil.getNMSClass("ChatSerializer");
	private Class<?> nmsTagCompound = ReflectionUtil.getNMSClass("NBTTagCompound");
	private Class<?> nmsPacketPlayOutChat = ReflectionUtil.getNMSClass("PacketPlayOutChat");
	private Class<?> nmsAchievement = ReflectionUtil.getNMSClass("Achievement");
	private Class<?> nmsStatistic = ReflectionUtil.getNMSClass("Statistic");
	private Class<?> nmsItemStack = ReflectionUtil.getNMSClass("ItemStack");

	private Class<?> obcStatistic = ReflectionUtil.getOBCClass("CraftStatistic");
	private Class<?> obcItemStack = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
	
	public JSONMessage(final String firstPartText) {
		messageParts = new ArrayList<MessagePart>();
		messageParts.add(new MessagePart(firstPartText));
		jsonString = null;
		dirty = false;
	}
	
	public JSONMessage() {
		messageParts = new ArrayList<MessagePart>();
		messageParts.add(new MessagePart());
		jsonString = null;
		dirty = false;
	}
	
	public JSONMessage text(String text) {
		MessagePart latest = latest();
		if (latest.hasText()) {
			throw new IllegalStateException("text for this message part is already set");
		}
		latest.text = text;
		dirty = true;
		return this;
	}
	
	public JSONMessage color(final ChatColor color) {
		if (!color.isColor()) {
			throw new IllegalArgumentException(color.name() + " is not a color");
		}
		latest().color = color;
		dirty = true;
		return this;
	}
	
	public JSONMessage style(ChatColor... styles) {
		for (final ChatColor style : styles) {
			if (!style.isFormat()) {
				throw new IllegalArgumentException(style.name() + " is not a style");
			}
		}
		latest().styles.addAll(Arrays.asList(styles));
		dirty = true;
		return this;
	}
	
	public JSONMessage file(final String path) {
		onClick("open_file", path);
		return this;
	}
	
	public JSONMessage link(final String url) {
		onClick("open_url", url);
		return this;
	}
	
	public JSONMessage suggest(final String command) {
		onClick("suggest_command", command);
		return this;
	}
	
	public JSONMessage command(final String command) {
		onClick("run_command", command);
		return this;
	}
	
	public JSONMessage achievementTooltip(final String name) {
		onHover("show_achievement", "achievement." + name);
		return this;
	}
	
	public JSONMessage achievementTooltip(final Achievement which) {
		try {
			Object achievement = ReflectionUtil.getMethod(obcStatistic, "getNMSAchievement").invoke(null, which);
			return achievementTooltip((String) ReflectionUtil.getField(nmsAchievement, "name").get(achievement));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public JSONMessage statisticTooltip(final Statistic which) {
		Type type = which.getType();
		if (type != Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic requires an additional " + type + " parameter!");
		}
		try {
			Object statistic = ReflectionUtil.getMethod(obcStatistic, "getNMSStatistic").invoke(null, which);
			return achievementTooltip((String) ReflectionUtil.getField(nmsStatistic, "name").get(statistic));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public JSONMessage statisticTooltip(final Statistic which, Material item) {
		Type type = which.getType();
		if (type == Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic needs no additional parameter!");
		}
		if ((type == Type.BLOCK && item.isBlock()) || type == Type.ENTITY) {
			throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		}
		try {
			Object statistic = ReflectionUtil.getMethod(obcStatistic, "getMaterialStatistic").invoke(null, which, item);
			return achievementTooltip((String) ReflectionUtil.getField(nmsStatistic, "name").get(statistic));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public JSONMessage statisticTooltip(final Statistic which, EntityType entity) {
		Type type = which.getType();
		if (type == Type.UNTYPED) {
			throw new IllegalArgumentException("That statistic needs no additional parameter!");
		}
		if (type != Type.ENTITY) {
			throw new IllegalArgumentException("Wrong parameter type for that statistic - needs " + type + "!");
		}
		try {
			Object statistic = ReflectionUtil.getMethod(obcStatistic, "getEntityStatistic").invoke(null, which, entity);
			return achievementTooltip((String) ReflectionUtil.getField(nmsStatistic, "name").get(statistic));
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public JSONMessage itemTooltip(final String itemJSON) {
		onHover("show_item", itemJSON);
		return this;
	}
	
	public JSONMessage itemTooltip(final ItemStack itemStack) {
		try {
			Object nmsItem = ReflectionUtil.getMethod(obcItemStack, "asNMSCopy", ItemStack.class).invoke(null, itemStack);
			return itemTooltip(ReflectionUtil.getMethod(nmsItemStack, "save").invoke(nmsItem, nmsTagCompound.newInstance()).toString());
		} catch (Exception e) {
			e.printStackTrace();
			return this;
		}
	}
	
	public JSONMessage tooltip(final String text) {
		return tooltip(text.split("\\n"));
	}
	
	public JSONMessage tooltip(final List<String> lines) {
		return tooltip((String[])lines.toArray());
	}
	
	public JSONMessage tooltip(final String... lines) {
		if (lines.length == 1) {
			onHover("show_text", lines[0]);
		} else {
			itemTooltip(makeMultilineTooltip(lines));
		}
		return this;
	}
	
	public JSONMessage then(final Object obj) {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		messageParts.add(new MessagePart(obj.toString()));
		dirty = true;
		return this;
	}
	
	public JSONMessage then() {
		if (!latest().hasText()) {
			throw new IllegalStateException("previous message part has no text");
		}
		messageParts.add(new MessagePart());
		dirty = true;
		return this;
	}
	
	public String toJSONString() {
		if (!dirty && jsonString != null) {
			return jsonString;
		}
		StringWriter string = new StringWriter();
		JsonWriter json = new JsonWriter(string);
		try {
			if (messageParts.size() == 1) {
				latest().writeJson(json);
			} else {
				json.beginObject().name("text").value("").name("extra").beginArray();
				for (final MessagePart part : messageParts) {
					part.writeJson(json);
				}
				json.endArray().endObject();
				json.close();
			}
		} catch (Exception e) {
			throw new RuntimeException("invalid message");
		}
		jsonString = string.toString();
		dirty = false;
		return jsonString;
	}
	
	public void send(Player player){
		try {
			Object handle = ReflectionUtil.getHandle(player);
			Object connection = ReflectionUtil.getField(handle.getClass(), "playerConnection").get(handle);
			Object serialized = ReflectionUtil.getMethod(nmsChatSerializer, "a", String.class).invoke(null, toJSONString());
			Object packet = nmsPacketPlayOutChat.getConstructor(ReflectionUtil.getNMSClass("IChatBaseComponent")).newInstance(serialized);
			ReflectionUtil.getMethod(connection.getClass(), "sendPacket").invoke(connection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(CommandSender sender) {
		if (sender instanceof Player) {
			send((Player) sender);
		} else {
			sender.sendMessage(toOldMessageFormat());
		}
	}

	public void send(final Iterable<? extends CommandSender> senders) {
		for (final CommandSender sender : senders) {
			send(sender);
		}
	}
	
	public void sendToSpectators(List<SpectatorUser> users) {
		for (final SpectatorUser su : users) {
			send(su.getPlayer());
		}
	}
	
	public void send(List<User> users) {
		for (final User u : users) {
			send(u.getPlayer());
		}
	}
	
	public String toOldMessageFormat() {
		StringBuilder result = new StringBuilder();
		for (MessagePart part : messageParts) {
			result.append(part.color).append(part.text);
		}
		return result.toString();
	}
	
	private MessagePart latest() {
		return messageParts.get(messageParts.size() - 1);
	}
	
	private String makeMultilineTooltip(final String[] lines) {
		StringWriter string = new StringWriter();
		JsonWriter json = new JsonWriter(string);
		try {
			json.beginObject().name("id").value(1);
			json.name("tag").beginObject().name("display").beginObject();
			json.name("Name").value("\\u00A7f" + lines[0].replace("\"", "\\\""));
			json.name("Lore").beginArray();
			for (int i = 1; i < lines.length; i++) {
				final String line = lines[i];
				json.value(line.isEmpty() ? " " : line.replace("\"", "\\\""));
			}
			json.endArray().endObject().endObject().endObject();
			json.close();
		} catch (Exception e) {
			throw new RuntimeException("invalid tooltip");
		}
		return string.toString();
	}
	
	private void onClick(final String name, final String data) {
		final MessagePart latest = latest();
		latest.clickActionName = name;
		latest.clickActionData = data;
		dirty = true;
	}
	
	private void onHover(final String name, final String data) {
		final MessagePart latest = latest();
		latest.hoverActionName = name;
		latest.hoverActionData = data;
		dirty = true;
	}
	
}

class MessagePart {

	ChatColor color = ChatColor.WHITE;
	ArrayList<ChatColor> styles = new ArrayList<ChatColor>();
	String clickActionName = null, clickActionData = null,
		   hoverActionName = null, hoverActionData = null;
	String text = null;

	MessagePart(final String text) {
		this.text = text;
	}

	MessagePart() {}

	boolean hasText() {
		return text != null;
	}

	JsonWriter writeJson(JsonWriter json) {
		try {
			json.beginObject().name("text").value(text);
			json.name("color").value(color.name().toLowerCase());
			for (final ChatColor style : styles) {
				String styleName;
				switch (style) {
				case MAGIC:
					styleName = "obfuscated"; break;
				case UNDERLINE:
					styleName = "underlined"; break;
				default:
					styleName = style.name().toLowerCase(); break;
				}
				json.name(styleName).value(true);
			}
			if (clickActionName != null && clickActionData != null) {
				json.name("clickEvent")
					.beginObject()
					.name("action").value(clickActionName)
					.name("value").value(clickActionData)
					.endObject();
			}
			if (hoverActionName != null && hoverActionData != null) {
				json.name("hoverEvent")
					.beginObject()
					.name("action").value(hoverActionName)
					.name("value").value(hoverActionData)
					.endObject();
			}
			return json.endObject();
		} catch(Exception e){
			e.printStackTrace();
			return json;
		}
	}

}