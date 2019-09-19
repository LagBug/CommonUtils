package me.lagbug.common.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class SpigotCommand implements CommandExecutor {

	private String permission, noPermissions, usage, wrongUsage;
	private int reqArgs;
	private List<SubCommand> subCommands = new ArrayList<>();
	
	public SpigotCommand(String permission, String noPermissions, String wrongUsage, int reqArgs, @Nullable SubCommand... subCommands) {
		this.permission = permission;
		this.noPermissions = noPermissions;
		this.wrongUsage = wrongUsage;
		this.reqArgs = reqArgs - 1;
		
		for (SubCommand sc : subCommands) {
			this.subCommands.add(sc);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		if (!sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', noPermissions));
			return false;
		}
		
		if (reqArgs <= -1) {
			onCommand(sender, args);
			return false;
		}
		
		if (args.length <= reqArgs) {
			try {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', wrongUsage).replace("%usage%", "/" + usage + " <" + getSubCommandNames() + ">"));	
			} catch (NullPointerException ex) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', wrongUsage).replace("%usage%", usage));
			}
			return false;
		}
		
		
		boolean found = false;
		
		try {
			for (SubCommand subCommand : this.subCommands) {
				if (subCommand.getNames().contains(args[0].toLowerCase())) {
					found = true;
					
					if (!sender.hasPermission(subCommand.getPermission()) || !sender.hasPermission(permission)) {
						sender.sendMessage(noPermissions);
						return false;
					}

					subCommand.sender = sender;
					subCommand.onCommand(sender, args);
					return false;
				}
			}
			
			if (!found) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', wrongUsage).replace("%usage%", "/" + usage + " <" + getSubCommandNames() + ">"));
			}	
		} catch (NullPointerException ex) {
			onCommand(sender, args);
		}
		return false;
	}

	public abstract void onCommand(CommandSender sender, String[] args);
	
	protected String getSubCommandNames() {
		String result = "";
		for (SubCommand cmd : subCommands) {
			result += "/" + cmd.getNames().get(0);
		}
		
		return result.substring(1, result.length());
	}
	
	protected void setUsage(String usage) {
		this.usage = usage;
	}
}