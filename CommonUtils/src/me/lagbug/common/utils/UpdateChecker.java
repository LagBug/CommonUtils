package me.lagbug.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.scheduler.BukkitRunnable;

import me.lagbug.common.CommonUtils;

public class UpdateChecker {

	private int projectID;
	private String newVersion, currentVersion;
	private URL url;
	
	public UpdateChecker(int projectID) {
		this.projectID = projectID;
		this.currentVersion = CommonUtils.getPlugin().getDescription().getVersion();
		
		try {
			url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + projectID);
		} catch (MalformedURLException ex) { 
			return;
		}
	}
	
	/* 
	 * Asynchronously schedules an updater. 
	 * If an update is found, all online
	 * players with permissions will be notified.
	 * 
	 * Delay time unit is minutes.
	*/	
	public void schedule(int delay) {
		new BukkitRunnable() {
			@Override
			public void run() {
				String message = "";
				switch (getResult()) {
				case ERROR:
					message = " --> Failed to check for updates.";
					break;
				case FOUND:
					message = " --> Found a new update! Download it using " + "https://www.spigotmc.org/resources/" + projectID + "/";
					break;
				case NOT_FOUND:
					message = " --> No updates were found, you are using the latest version.";
					break;
				case DEVELOPMENT:
					message = " --> You are running a development build, this might not be stable.";
					break;
				}
				CommonUtils.forceLog(
						"--------------------------------------------------",
						" --> " + CommonUtils.getPlugin().getDescription().getName() + " update result:",
						message,
						"--------------------------------------------------");
			}
		}.runTaskTimerAsynchronously(CommonUtils.getPlugin(), 0, delay * 20 * 60);
	}
	
    private UpdateResult getResult() {
    	try {
         	URLConnection con = url.openConnection();
            this.newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            
        	int currentV = Integer.parseInt(currentVersion.replace(".", ""));
        	int newV = Integer.parseInt(newVersion.replace(".", "").replace("-", ""));
            
            if (newV > currentV) {
            	return UpdateResult.FOUND;
            } else if (newV < currentV) {
            	return UpdateResult.DEVELOPMENT;
            }
            return UpdateResult.NOT_FOUND;
            
    	} catch (IOException ex) {
    		return UpdateResult.ERROR;
    	}
    }
    
    public int getProjectID() {
    	return projectID;
    }
    
    public String getCurrentVersion() {
    	return currentVersion;
    }
    
    public String getNewVersion() {
    	return newVersion;
    }
}

enum UpdateResult {
	ERROR, FOUND, NOT_FOUND, DEVELOPMENT

}