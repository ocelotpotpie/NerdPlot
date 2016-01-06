package org.challenger2.NerdPlot;

import org.bukkit.plugin.java.JavaPlugin;
import org.challenger2.NerdPlot.Commands.CommandCreate;
import org.challenger2.NerdPlot.Commands.NerdPlotCommand;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class NerdPlotPlugin extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");
	private static final String cmdName = "nerdplot";

	private WorldGuardPlugin wg;
	private WorldEditPlugin we;
	private Map<String, NerdPlotCommand> plotCommands;

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

		plotCommands = new HashMap<String, NerdPlotCommand>();

		CommandCreate commandCreate = new CommandCreate(this);
		plotCommands.put(commandCreate.getName(), commandCreate);
		
		loadConfig();

    }
    
    @Override
    public void onDisable() {
    	saveMyConfig();
    	plotCommands = null;
    	wg = null;
    	we = null;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {

    	logInfo("Received command: " + name + " numArgs = " + args.length);

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
    		PrintUsage(sender);
    		return true;
    	}
    	
    	String subCommand = args[0].toLowerCase();

    	if (plotCommands.containsKey(subCommand)) {
    		logInfo("Command Found");
    		NerdPlotCommand cmd = plotCommands.get(subCommand);
    		if (sender.hasPermission(cmd.getPermission())) {
    			cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    		} else {
    			logInfo("Permission denyed");
    			PrintUsage(sender);
    		}
    	} else {
    		logInfo("Command Not Found");
    		PrintUsage(sender);
    	}

    	return true;
    }

    public void PrintUsage(CommandSender sender){
    	for (NerdPlotCommand cmd : plotCommands.values()) {
    		if (sender.hasPermission(cmd.getPermission())) {
    			sender.sendMessage(ChatColor.GREEN + "/" + cmdName + " " + cmd.getUsage());
    		}
    	}
    	sender.sendMessage(ChatColor.GREEN + "/" + cmdName + " help");
    }
    
    public void loadConfig() {
    	saveDefaultConfig();
    }

    public void saveMyConfig() {
    	saveConfig();
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
 
