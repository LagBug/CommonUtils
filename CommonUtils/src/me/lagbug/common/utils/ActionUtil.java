package me.lagbug.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

public class ActionUtil {
	
	private static JavaPlugin plugin;
	
	public static void setPlugin(JavaPlugin plugin) {
		ActionUtil.plugin = plugin;
	}
	
	public static void execute(Player p, String actions) {
		String[] rewards = actions.replace("[", "").replace("]", "").split(", ");
		
		for (String r : rewards) {
			r = r.replace("%player%", p.getName());
			
			if (r.startsWith("consolecmd kick %player% ") || r.startsWith("kickplayer ")) {
				p.kickPlayer(r.replace("consolecmd kick %player% ", "").replace("kickplayer ", ""));
			} else if (r.startsWith("consolecmd")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), r.replace("consolecmd ", ""));
			}else if (r.startsWith("playercmd ")) {
				p.getPlayer().performCommand(r.replace("playercmd ", ""));
				
			} else if (r.startsWith("sendtobungee")) {				
		        Messenger messenger = Bukkit.getMessenger();
		          
		        if (!messenger.isOutgoingChannelRegistered(plugin, "BungeeCord")) {
		        	messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
		        }
		          
		        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		        DataOutputStream out = new DataOutputStream(byteArray);
		        
		        try {
					out.writeUTF("Connect");
					out.writeUTF(r.replace("sendtobungee ", ""));
				} catch (IOException e) {
					e.printStackTrace();
				}
		        
		        p.sendPluginMessage(plugin, "BungeeCord", byteArray.toByteArray());	
			}
		}	
	}
	
}
