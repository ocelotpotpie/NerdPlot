package org.challenger2.NerdPlot;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Util {

	/**
	 * Extract the area name from a plot name
	 * 
	 */
	public static String getPlotArea(String plotName) {
		return plotName.substring(0, plotName.length() - 5);
	}
	
	public static String generatePlotName(String areaName, Map<String,ProtectedRegion> regions) {
		for (int i = 0; i < 10000; i++) {
			String name = String.format("%s_%04d", areaName, i);
			if (!regions.containsKey(name)) {
				return name;
			}
		}
		return null;
	}
	
	
	/**
	 * Get or create a configuration section
	 */
	public static ConfigurationSection getOrCreateSection(ConfigurationSection section, String subSection) {
		ConfigurationSection newSection = section.getConfigurationSection(subSection);
		if (newSection == null) {
			return section.createSection(subSection);
		} else {
			return newSection;
		}
	}
}
