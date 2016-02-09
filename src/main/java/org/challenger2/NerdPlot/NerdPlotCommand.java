package org.challenger2.NerdPlot;

import org.bukkit.command.CommandSender;

public interface NerdPlotCommand {

	/**
	 * Execute the command
	 * 
	 * @param sender What sent the command
	 * @param args command arguments
	 */
	public void execute(CommandSender sender, String[] args);

	/**
	 * Get the name of this command
	 * 
	 * @return Command Name
	 */
	public String getName();

	/**
	 * Print out usage string
	 * 
	 * @param sender
	 */
	public void printUsage(CommandSender sender);

}
