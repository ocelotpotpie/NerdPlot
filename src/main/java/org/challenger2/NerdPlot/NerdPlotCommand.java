package org.challenger2.NerdPlot;

import org.bukkit.command.CommandSender;

public abstract class NerdPlotCommand {

	protected final NerdPlotPlugin plugin;
	protected final String name;

	/**
	 * Make a new command
	 * @param plugin Bukkit plugin with all the main handles
	 * @param name Command name
	 */
	public NerdPlotCommand(NerdPlotPlugin plugin, String name) {
		this.plugin = plugin;
		this.name = name;
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
	 * Print out usage string
	 * 
	 * @param sender
	 */
	public abstract void printUsage(CommandSender sender);

}
