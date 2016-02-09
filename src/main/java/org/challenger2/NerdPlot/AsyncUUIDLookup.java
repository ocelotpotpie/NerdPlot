package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.util.profile.Profile;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;


/**
 * 
 * Convert a player name to a UUID using sk89q's name resolver. We have to do this
 * to ensure the UUID cache in WG has the right names
 *
 */
public class AsyncUUIDLookup extends BukkitRunnable {
	
	private final NerdPlotPlugin plugin;
	private final String playerName;
	private UUID playerID;
	
	
	public AsyncUUIDLookup(NerdPlotPlugin plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
		this.playerID = null;
	}


	@Override
	public void run() {
		ProfileService profiles = plugin.getWG().getProfileService();
		try {
			Profile profile = profiles.findByName(playerName);
			playerID = profile.getUniqueId();
		} catch (Exception e) {
			// ignore/don't care
		}
	}
	
	
	public String getPlayerName() {
		return playerName;
	}
	
	
	public UUID getPlayerID() {
		return playerID;
	}

}
