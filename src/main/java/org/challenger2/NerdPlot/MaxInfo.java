package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Immutable class to store maximum info
 */
public class MaxInfo {
	public final String worldName;
	public final String areaName;
	public final int maxPlots;
	
	public MaxInfo(String worldName, String areaName, int maxPlots) {
		this.worldName = fixNull(worldName);
		this.areaName = fixNull(areaName);
		this.maxPlots = maxPlots;
	}
	
	
	public void print(CommandSender sender) {
		sender.sendMessage(String.format("%sworld: %s, area: %s, max plots %d", ChatColor.GREEN, worldName, areaName, maxPlots));
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
