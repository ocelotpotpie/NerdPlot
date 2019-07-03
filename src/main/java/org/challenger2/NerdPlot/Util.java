package org.challenger2.NerdPlot;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Util {
    static RegionManager getWorldGuardRegionManager(org.bukkit.World world) {
        World wrappedWorld = BukkitAdapter.adapt(world);
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(wrappedWorld);
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
