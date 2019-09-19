package me.lagbug.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.lagbug.common.CommonUtils;

public class ActionBar {

	private String nmsver;
	private boolean useOldMethods = false;
	private Map<Player, Integer> toSend = new HashMap<>();

	public ActionBar() {
		
	}

	private void send(Player player, String message) {
		nmsver = Bukkit.getServer().getClass().getPackage().getName();
		nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);
		if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.startsWith("v1_7_")) {
			useOldMethods = true;
		}

		if (player == null || !player.isOnline()) {
			return;
		}

		try {
			Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsver + ".entity.CraftPlayer"),
					packetPlayOutChatClass = Class.forName("net.minecraft.server." + nmsver + ".PacketPlayOutChat"),
					packetClass = Class.forName("net.minecraft.server." + nmsver + ".Packet");

			Object craftPlayer = craftPlayerClass.cast(player), packet;

			if (useOldMethods) {
				Class<?> chatSerializerClass = Class.forName("net.minecraft.server." + nmsver + ".ChatSerializer"),
						iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");

				Object cbc = iChatBaseComponentClass.cast(chatSerializerClass.getDeclaredMethod("a", new Class[] { String.class }).invoke(chatSerializerClass, new Object[] { "{\"text\": \"" + message + "\"}" }));
				packet = packetPlayOutChatClass.getConstructor(new Class[] { iChatBaseComponentClass, Byte.TYPE })
						.newInstance(new Object[] { cbc, Byte.valueOf((byte) 2) });
			} else {
				Class<?> chatComponentTextClass = Class.forName("net.minecraft.server." + nmsver + ".ChatComponentText"),
							iChatBaseComponentClass = Class.forName("net.minecraft.server." + nmsver + ".IChatBaseComponent");
				try {
					Object chatMessageType = null;
					
					for (Object obj : Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType").getEnumConstants()) {
						if (obj.toString().equals("GAME_INFO")) {
							chatMessageType = obj;
						}
					}

					packet = packetPlayOutChatClass
							.getConstructor(new Class[] { iChatBaseComponentClass, Class.forName("net.minecraft.server." + nmsver + ".ChatMessageType") })
							.newInstance(new Object[] { chatComponentTextClass.getConstructor(new Class[] { String.class }).newInstance(new Object[] { message }), chatMessageType });
				} catch (ClassNotFoundException cnfex) {
					packet = packetPlayOutChatClass.getConstructor(new Class[] { iChatBaseComponentClass, Byte.TYPE })
							.newInstance(new Object[] { chatComponentTextClass.getConstructor(new Class[] { String.class })
									.newInstance(new Object[] { message }), Byte.valueOf((byte) 2) });
				}
			}
			
			Object craftPlayerHandle = craftPlayerClass.getDeclaredMethod("getHandle", new Class[0]).invoke(craftPlayer, new Object[0]);
			Field playerConnectionField = craftPlayerHandle.getClass().getDeclaredField("playerConnection");
			Object playerConnection = playerConnectionField.get(craftPlayerHandle);
			Method sendPacketMethod = playerConnection.getClass().getDeclaredMethod("sendPacket", new Class[] { packetClass });
			
			sendPacketMethod.invoke(playerConnection, new Object[] { packet });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void cancel(Player player) {
		if (toSend.containsKey(player)) {
			Bukkit.getScheduler().cancelTask(toSend.get(player));
			toSend.remove(player);
		}
	}

	public void send(Player player, boolean forever, String message) {
		if (!forever) {
			if (toSend.containsKey(player)) {
				Bukkit.getScheduler().cancelTask(toSend.get(player));
				toSend.remove(player);
			}
			send(player, message);
			return;
		}

		if (toSend.containsKey(player)) {
			cancel(player);
		}

		int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CommonUtils.getPlugin(), () -> {
			send(player, message);
		}, 0, 40);

		toSend.put(player, taskId);
		taskId = -1;
	}
}