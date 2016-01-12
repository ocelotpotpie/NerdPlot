package org.challenger2.NerdPlot.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.challenger2.NerdPlot.NerdPlotPlugin;

public abstract class NerdPlotCommand {

	protected final NerdPlotPlugin plugin;
	protected final String name;
	protected final String usage;
	protected final String permission;

	/**
	 * Make a new command
	 * @param plugin Bukkit plugin with all the main handles
	 * @param name Command name
	 */
	public NerdPlotCommand(NerdPlotPlugin plugin, String name, String usage, String permission) {
		this.plugin = plugin;
		this.name = name;
		this.usage = usage;
		this.permission = permission;
	}

	/**
	 * Execute the command
	 * 
	 * @param sender What sent the command
	 * @param args command arguments
	 */
	public abstract void execute(CommandSender sender, String[] args);

	/**
	 * Get the name of this command
	 * 
	 * @return Command Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the usage String
	 * 
	 * @param sender What sent the command
	 * @return Usage String
	 */
	public String getUsage() {
		return usage;
	}
	
	/**
	 * The the permission for this command
	 * 
	 * @return permission string
	 */
	public String getPermission() {
		return permission;
	}
	
	/**
	 * Print out usage string
	 * 
	 * @param sender
	 */
	public void printUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + usage);
	}
}

