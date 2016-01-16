package org.challenger2.NerdPlot;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.DomainInputResolver;
import com.sk89q.worldguard.protection.util.DomainInputResolver.UserLocatorPolicy;
import com.sk89q.worldguard.util.profile.resolver.ProfileService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NerdPlotPlugin extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");
	private static final String cmdName = "nerdplot";

	private WorldGuardPlugin wg;
	private WorldEditPlugin we;
	private Map<String, NerdPlotCommand> plotCommands;
	private Map<String, Map<String, UUID>> worldPlots;

	public final ListeningExecutorService executor =
	        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

	/**
	 * Enable the plugin.
	 * 
	 * Grab WE, WG references
	 * Load all commands and config file
	 * 
	 */
    @Override
    public void onEnable() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			logSevere("Failed to load WorldGuard.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		wg = (WorldGuardPlugin)plugin;

		plugin = getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			logSevere("Failed to load WorldEdit.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		we = (WorldEditPlugin)plugin;

		loadConfig();

		// Add commands
		plotCommands = new LinkedHashMap<String, NerdPlotCommand>();
		addCommand(new CmdClaim(this));
		addCommand(new CmdList(this));
		addCommand(new CmdMax(this));
		addCommand(new CmdCreate(this));
		addCommand(new CmdRemove(this));
		addCommand(new CmdAddOwner(this));
		addCommand(new CmdRemoveOwner(this));
		addCommand(new CmdClean(this));
		addCommand(new CmdVersion(this));
    }
    
    private void addCommand(NerdPlotCommand cmd) {
    	plotCommands.put(cmd.getName(), cmd);
    }

    /**
     * Shutdown our plugin
     */
    @Override
    public void onDisable() {
    	saveMyConfig();
    	worldPlots = null;
    	plotCommands = null;
    	wg = null;
    	we = null;
    }

    /**
     * Process user commands
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {

    	//logInfo("Received command: " + name + " numArgs = " + args.length);

    	if (!name.equalsIgnoreCase(cmdName)) {
    		return false;
    	}

    	if (we == null) {
    		sender.sendMessage(ChatColor.RED + "WorldEdit cannot be found. Please contact an administrator");
    		return false;
    	}
    	
    	if (wg == null) {
    		sender.sendMessage(ChatColor.RED + "WorldGuard cannot be found. Please contact an administrator");
    		return false;
    	}
    	
    	if (args.length < 1) {
    		printUsage(sender);
    		return true;
    	}
    	
    	String subCommand = args[0].toLowerCase();

    	NerdPlotCommand cmd = plotCommands.get(subCommand);
    	if (cmd != null) {
    		cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    	} else {
    		//logInfo("Command Not Found");
    		printUsage(sender);
    	}

    	return true;
    }

    /**
     * Print usage of all commands
     * 
     * @param sender
     */
    public void printUsage(CommandSender sender){
    	for (NerdPlotCommand cmd : plotCommands.values()) {
    		cmd.printUsage(sender);
    	}
    	sender.sendMessage(ChatColor.GREEN + "/" + cmdName + " help");
    }

    /**
     * Load config from disk
     */
    public synchronized void loadConfig() {
    	this.saveDefaultConfig();

    	worldPlots = new HashMap<String, Map<String, UUID>>();
    	ConfigurationSection worlds = this.getConfig().getConfigurationSection("worlds");
    	if (worlds != null) {
	    	for (String world : worlds.getKeys(false)) {
	    		ConfigurationSection plotSection = worlds.getConfigurationSection(world);
	    		if (plotSection != null) {
		    		Map<String, UUID> plotMap = new HashMap<String, UUID>();
		    		worldPlots.put(world, plotMap);
		    		for (String plot : plotSection.getKeys(false)) {
		    			String owner = plotSection.getString(plot);
		    			try {
		    				plotMap.put(plot, UUID.fromString(owner));
		    			} catch (IllegalArgumentException e) {
		    				plotMap.put(plot, null);
		    			}
		    		}
	    		}
	    	}
    	}
    }

    /**
     * Save config to disk
     */
    public synchronized void saveMyConfig() {
    	
    	ConfigurationSection worlds = this.getConfig().createSection("worlds");
    	for (String world : worldPlots.keySet()) {
    		Map<String, UUID> plotMap = worldPlots.get(world);
    		ConfigurationSection plots = worlds.createSection(world);
    		for (String plot : plotMap.keySet()) {
    			UUID uuid = plotMap.get(plot);
    			if (uuid == null) {
    				plots.set(plot, "");
    			} else {
    				plots.set(plot, uuid.toString());
    			}
    		}
    	}
    	this.getConfig().set("Foo", "Bar");

    	this.saveConfig();
    }
    
    /**
     * Get the maximum number of plots a player can have
     * @return
     */
    public int getMaxPlots() {
    	int max = this.getConfig().getInt("maxPlots");
    	return max;
    }

    /**
     * Set the maximum number of plots a player can have
     * 
     * @param max
     */
    public void setMaxPlots(int max) {
    	this.getConfig().set("maxPlots", max);
    }
    
    /**
     * Add a new plot to the plot database
     * 
     * @param world
     * @param plot
     */
    public synchronized void addPlot(String world, String plot) {
    	Map<String, UUID> plotMap = worldPlots.get(world);
    	if (plotMap == null) {
    		plotMap = new HashMap<String, UUID>();
    		worldPlots.put(world, plotMap);
    	}
    	plotMap.put(plot, null);
    }

    /**
     * Remove a plot from the plot database
     * 
     * @param world
     * @param plot
     */
    public synchronized void removePlot(String world, String plot) {
    	Map<String, UUID> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		plotMap.remove(plot);
    		if (plotMap.isEmpty()) {
    				worldPlots.remove(world);
    		}
    	}
    }
    
    /**
     * Check to see if the requested name is in the plot database
     * 
     * @param world
     * @param plot
     * @return
     */
    public synchronized boolean isPlot(String world, String plot) {
    	Map<String, UUID> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		return plotMap.containsKey(plot);
    	}
    	return false;
    }
    
    /**
     * Set the owner of a plot in the database.
     * Set the owner to null to make the plot /claim able
     * 
     * @param world
     * @param plot
     * @param player
     */
    public synchronized void setPlotOwner(String world, String plot, UUID owner) {
    	Map<String, UUID> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		plotMap.put(plot, owner);
    	}
    }

    public synchronized UUID getPlotOwner(String world, String plot) {
    	Map<String, UUID> plotMap = worldPlots.get(world);
    	if (plotMap == null) {
    		return null;
    	} else {
    		return plotMap.get(plot);
    	}
    }

    /**
     * Return a list of all the plots a player has
     * 
     * This command has to check all the plots to generate the list
     * 
     * @param player
     * @return
     */
    public synchronized List<String> getAllPlayerPlots(UUID player) {
    	List<String> list = new LinkedList<String>();
    	if (player == null) {
    		return list;
    	}

    	for (Map<String, UUID> plotMap : worldPlots.values()) {
    		for (String plot : plotMap.keySet()) {
    			if (player.equals(plotMap.get(plot))) {
    				list.add(plot);
    			}
    		}
    	}
    	return list;
    }
 
    /**
     * Clean the worldPlots database.
     * 
     * Check for plots that do not have WG regions
     *   We fix by removing the plot.
     * 
     * Check for plots with owners that are not in WG
     *   We fix by removing the player from the plot.
     *   
     */
    public synchronized void cleanupDatabase(CommandSender sender) {
    	for (String worldName : worldPlots.keySet()) {
    		
    		// List of all plots and their owners for this world
    		Map<String, UUID> plotMap = worldPlots.get(worldName);
 
    		// Lookup the bukkit world with this name
    		List<World> worlds = this.getServer().getWorlds();
    		World world = null;
    		for (World w : worlds) {
    			if (w.getName().equalsIgnoreCase(worldName)) {
    				world = w;
    				break;
    			}
    		}
    		if (world == null) {
    			// World does not exist. Delete it
    			worldPlots.remove(worldName);
    			if (sender != null) {
    				sender.sendMessage(ChatColor.RED + "Removing world: " + worldName);
    			}
    		} else {
    			RegionManager manager = wg.getRegionManager(world);
    			
    			// At this point, we have a region manager and a world and a list of plots to check
    			Iterator<String> itr = plotMap.keySet().iterator();
    			while (itr.hasNext()) {
    				String plot = itr.next();
    				ProtectedRegion rg = manager.getRegion(plot);
    				if (rg == null) {
    					// The region does not exist in WG. Remove the plot
    					itr.remove();
    					if (sender != null) {
    						sender.sendMessage(ChatColor.RED + "Removed plot " + worldName + ":" + plot);
    					}
    				} else {
    					UUID uuid = plotMap.get(plot);
    					if (uuid == null) {
    						// Ignore the empty string owner
    						continue;
    					}
    					if (!rg.getOwners().contains(uuid)) {
    						// This region has a new owner. Remove the old one from the plotdb
    						plotMap.put(plot, null);
    						if(sender != null) {
    							Player p = this.getServer().getPlayer(uuid);
    							String ownerName;
    							if (p == null) {
    								ownerName = "<unknown>";
    							} else {
    								ownerName = p.getName();
    							}
    							sender.sendMessage(ChatColor.RED + "Removed owner \"" + ownerName + "\" from plot " + worldName + ":" + plot);
    						}
    					}
    				}
    			}
    			if (plotMap.isEmpty()) {
    				worldPlots.remove(worldName);
    			}
    		}
    	}
    }
	
	public void lookupPlayerUUID(String name, FutureCallback<DefaultDomain> callback) {
		// The below is copied from SK89q docs on getting owner names
		// We have to use it or UUIDs will be displayed in Minecraft instead of player names
		String[] input = new String[] { name };
		ProfileService profiles = wg.getProfileService();
		DomainInputResolver resolver = new DomainInputResolver(profiles, input);
		resolver.setLocatorPolicy(UserLocatorPolicy.UUID_ONLY);
		ListenableFuture<DefaultDomain> future = this.executor.submit(resolver);

		// Add a callback using Guava
		Futures.addCallback(future, callback);
	}

    public String getCmdName() {
    	return cmdName;
    }
    
    public Logger getLog() {
    	return log;
    }

    public WorldGuardPlugin getWG() {
    	return wg;
    }
    
    public WorldEditPlugin getWE() {
    	return we;
    }
    
    public void logInfo(String msg) {
    	log.info("[" + cmdName + "] " + msg);
    }
    
    public void logWarning(String msg) {
    	log.warning("[" + cmdName + "] " + msg);
    }
    
    public void logSevere(String msg) {
    	log.severe("[" + cmdName + "] " + msg);
    }

}
 
