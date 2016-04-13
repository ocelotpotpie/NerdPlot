package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Immutable class to store plot data
 */
public class PlotInfo {
	public final String worldName;
	public final String plotName;
	public final String areaName;
	public final String ownerName;
	public final String ownerID;
	public final String dateCreated;
	public final String dateClaimed;
	
	public PlotInfo(String worldName, String plotName, String areaName, String ownerName, String ownerID, String dateCreated, String dateClaimed) {
		this.worldName = worldName;
		this.plotName = plotName;
		this.areaName = areaName;
		this.ownerName = ownerName;
		this.ownerID = ownerID;
		this.dateCreated = dateCreated;
		this.dateClaimed = dateClaimed;
	}
	
	
	/**
	 * Print data to the sender
	 * @return 
	 */
	public void print(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "  World Name:   " + ChatColor.AQUA + fixNull(worldName));
		sender.sendMessage(ChatColor.GREEN + "  Plot Name:    " + ChatColor.AQUA + fixNull(plotName));
		sender.sendMessage(ChatColor.GREEN + "  Area Name:    " + ChatColor.AQUA + fixNull(areaName));
		sender.sendMessage(ChatColor.GREEN + "  Owner Name:   " + ChatColor.AQUA + fixNull(ownerName));
		sender.sendMessage(ChatColor.GREEN + "  Owner ID:     " + ChatColor.AQUA + fixNull(ownerID));
		sender.sendMessage(ChatColor.GREEN + "  Date Created: " + ChatColor.AQUA + fixNull(dateCreated));
		sender.sendMessage(ChatColor.GREEN + "  Date Claimed: " + ChatColor.AQUA + fixNull(dateClaimed));
	}
	
	
	/**
	 * Fix null strings
	 */
	private String fixNull(String s) {
		if (s == null) {
			return "";
		} else {
			return s;
		}
	}
}
