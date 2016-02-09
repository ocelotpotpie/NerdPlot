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
		sender.sendMessage(ChatColor.GREEN + "  World Name: " + fixNull(worldName));
		sender.sendMessage(ChatColor.GREEN + "  Plot Name: " + fixNull(plotName));
		sender.sendMessage(ChatColor.GREEN + "  Area Name: " + fixNull(areaName));
		sender.sendMessage(ChatColor.GREEN + "  Owner Name: " + fixNull(ownerName));
		sender.sendMessage(ChatColor.GREEN + "  Owner ID: " + fixNull(ownerID));
		sender.sendMessage(ChatColor.GREEN + "  Date Created: " + fixNull(dateCreated));
		sender.sendMessage(ChatColor.GREEN + "  Date Claimed: " + fixNull(dateClaimed));
	}
	
	
	/**
	 * Fix null strings
	 */
	private String fixNull(String s) {
		if (s == null) {
			return "<UNKNOWN>";
		} else {
			return s;
		}
	}
}
