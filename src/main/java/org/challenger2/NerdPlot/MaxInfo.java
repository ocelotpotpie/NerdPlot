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
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN);
		sb.append("World: ");
		sb.append(ChatColor.AQUA);
		sb.append(worldName);
		sb.append(ChatColor.GREEN);
		sb.append(", Area: ");
		sb.append(ChatColor.AQUA);
		sb.append(areaName);
		sb.append(ChatColor.GREEN);
		sb.append(", Max Plots: ");
		sb.append(ChatColor.AQUA);
		sb.append(maxPlots);
		sender.sendMessage(sb.toString());
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
