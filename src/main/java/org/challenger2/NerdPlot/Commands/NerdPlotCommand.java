package org.challenger2.NerdPlot.Commands;

import org.bukkit.command.CommandSender;
import org.challenger2.NerdPlot.NerdPlotPlugin;

public abstract class NerdPlotCommand {

	protected final NerdPlotPlugin plugin;

	/**
	 * Make a new command
	 * @param plugin Bukkit plugin with all the main handles
	 * @param name Command name
	 */
	public NerdPlotCommand(NerdPlotPlugin plugin) {
		this.plugin = plugin;
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
	public abstract String getName();

	/**
	 * Get the usage String
	 * 
	 * @param sender What sent the command
	 * @return Usage String
	 */
	public abstract String getUsage();
	
	/**
	 * The the permission for this command
	 * 
	 * @return permission string
	 */
	public abstract String getPermission();
	


}

