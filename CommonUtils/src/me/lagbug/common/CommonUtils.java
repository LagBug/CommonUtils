package me.lagbug.common;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;

public class CommonUtils {
	
	private static final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static String pluginName = "Plugin";
	private static JavaPlugin plugin = null;
	
	public static String randomString(int length) {
		String result = ""; Random r = new Random();

		for (int i = 0; i < length; i++) {
			result = result + alphabet.charAt(r.nextInt(alphabet.length()));
		}
		return result;
	}
	
	public static int randomInteger(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		return new Random().nextInt((max - min) + 1) + min;
	}
	
	public static String listToString(List<?> list) {
		return list.toString().replace("[", "").replace("]", "").replace(",", "");
	}
	
	public static Color colorFromString(String colorName) {
		Color color;

		try {
			color = (Color) Class.forName("java.awt.Color").getField(colorName).get(null);
		} catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			return Color.BLACK;
		}
		
		return color;
	}
	
	public static String materialToString(Material material) {
		String result = "";

		for (String s : material.name().split("_")) {
			result += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
		}
		return result.substring(0, result.length() - 1);
	}
	
	public static boolean isPluginEnabled(String plugin) {
		return Bukkit.getPluginManager().getPlugin(plugin) != null && Bukkit.getPluginManager().getPlugin(plugin).isEnabled();
	}

	public static boolean isBungee() {
		return SpigotConfig.bungee && (!(Bukkit.getServer().getOnlineMode()));
	}
	
	public static void log(String... text) {
		for (String current : text) {
			System.out.println("[" + pluginName + "] " + current + ".");
		}
	}
	
	public static void forceLog(String... text) {
		for (String current : text) {
			System.out.println("[" + pluginName + "] " + current + ".");
		}
	}
	
	public static void setPluginName(String name) {
		pluginName = name;
	}

	public static void setPlugin(JavaPlugin pluginN) {
		plugin = pluginN;
	}
	
	public static JavaPlugin getPlugin() {
		return plugin;
	}
	
	public static String getPluginName() {
		return pluginName;
	}
}