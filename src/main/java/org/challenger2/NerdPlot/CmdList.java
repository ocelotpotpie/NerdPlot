package org.challenger2.NerdPlot;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;

public class CmdList extends NerdPlotCommand {
	
	private final String permission = "nerdplot.list";
	private final String listallPermission = "nerdplot.listall";

	public CmdList(NerdPlotPlugin plugin) {
		super(plugin, "list");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		final CommandSender senderF = sender;
		
		if (args.length == 0 && !sender.hasPermission(permission) ||
	        args.length == 1 && !sender.hasPermission(listallPermission) ||
	        args.length > 1){
			plugin.printUsage(sender);
			return;
		}
		
		final String listPlayerName;
		final boolean listany = args.length == 1;
		if (listany) {
			listPlayerName = args[0];
		} else {
			// Are we a player?
	    	if (!(sender instanceof Player)) {
	    		sender.sendMessage("Who are you? Your not a player.");
	    		return;
	    	}
	    	listPlayerName = sender.getName();			
		}

		plugin.lookupPlayerUUID(listPlayerName, new FutureCallback<DefaultDomain>() {

		    @Override
		    public void onSuccess(DefaultDomain result) {
		    	for (UUID uuid : result.getUniqueIds()) {
					List<String> plots = plugin.getAllPlayerPlots(uuid);
					for (String plot : plots) {
						senderF.sendMessage(ChatColor.GREEN + plot);
					}
					if (listany) {
						senderF.sendMessage(ChatColor.GREEN + listPlayerName + " has " + plots.size() + " out of " + plugin.getMaxPlots() + " maximum.");
					} else {
						senderF.sendMessage(ChatColor.GREEN + "You have " + plots.size() + " out of " + plugin.getMaxPlots() + " maximum.");
			    	}
		    	}
		    }

		    @Override
		    public void onFailure(Throwable throwable) {
		    	senderF.sendMessage(ChatColor.RED + "Unknown player: " + listPlayerName);
		    }
		});

	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
		if(sender.hasPermission(listallPermission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name + " <player>");
		}
	}

}
