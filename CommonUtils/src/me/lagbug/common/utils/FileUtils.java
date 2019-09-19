package me.lagbug.common.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.lagbug.common.CommonUtils;

public class FileUtils {

	private JavaPlugin plugin = CommonUtils.getPlugin();
	private String[] fileNames;

	private Map<String, YamlConfiguration> files;
	private Map<String, File> filesData;

	public void initiate(String... fileNames) {
		this.fileNames = fileNames;
		this.files = new HashMap<>();
		this.filesData = new HashMap<>();

		if (!files.isEmpty() || !filesData.isEmpty()) {
			files.clear();
			filesData.clear();
		}

		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}

		for (String fileName : fileNames) {
			File file = new File(plugin.getDataFolder(), fileName);
			if (!file.isDirectory()) {
				if (!file.exists()) {
					plugin.saveResource(fileName, false);
				}

				files.put(fileName, YamlConfiguration.loadConfiguration(file));
				filesData.put(fileName, file);
			}
		}
		
		for (File file : new File(plugin.getDataFolder() + File.separator + "lang").listFiles()) {
			if (!file.isDirectory()) {
				files.put("lang/" + file.getName(), YamlConfiguration.loadConfiguration(file));
				filesData.put("lang/" + file.getName(), file);
			}
		}

	}

	public void reloadFiles() {
		initiate(fileNames);
	}

	public YamlConfiguration getFile(String path) {
		return files.get(path);
	}

	public File getFileData(String path) {
		return filesData.get(path);
	}

	public void saveFile(String path) {
		try {
			getFile(path).save(getFileData(path));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}