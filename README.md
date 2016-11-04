NerdPlot
===========
Simple Minecraft Plot Management

Player Manual
-------------

#### /nerdplot claim
Claim the plot you are standing in. You can only have so many plots for each type of area.

#### /nerdplot list
Name all the plots you are in. You can use worldguard /region commands to find out more about your plots (such as where they are).

#### /nerdplot info
Shows information about the plot you are standing in

#### /nerdplot tp <plot name> [<world name>]
Teleport to a plot

#### /nerdplot max
Shows the maximum number of plots you are allowed to have

Mod Manual
----------
####/nerdplot create <prefix> [\<parent\>]
Create a new region from your current WE selection. Prefix will be used as the region prefix and a numerical suffix will be automatically created. The optional parent parameter will automatically set the new plots parent region.

Example:
/nerdplot create suburb spawncity
And just spam the above command 100 times to get all suburbs!


#### /nerdplot remove -f <plot name>
Remove a plot from nerdplot tracking. If -f is omitted, a friendly warning will be printed.

#### /nerdplot info <plot name>
Shows information about the named plot

#### /nerdplot setowner \<playername\>
Add a player to a plot you are standing in. This bypassess the max plot check. The plot will still show up under /nerdplot listplots correctly.


Example:
/nerdplot setowner Challenger2
Adds Challenger2 to the plot you are standing in.

#### /nerdplot removeowner \<playername\>
Remove a plot owner from the plot you are standing in.
You can optionally specify the plot name if you do not want to stand in the plot.
Please do not use /region removeowner for this. The plot system will still count the plot towards the player's total plot count and will show up under /nerdplot listplots.

#### /nerdplot max \<max\>
Set the new maximum number of plots each player may have

#### /nerdplot list \<player\>
List all the plots a player has.

#### /nerdplot createarea \<areaname\>
Create a new area. Players can only claim so many plots within a given area.

#### /nerdplot removearea \<areaname\>
Remove an area. If a region is part of a delete area, no one will be able to claim it.

#### /nerdplot cleanup [-f]
Look for any regions that have been deleted or any regions a plot owner is no longer a member of. If any such inconsistencies are found, the player will be removed from /nerdplot so they won’t have rogue plots that are not really there (The plots would otherwise count towards their max plot count).
This command will run automatically on plugin load each server reboot.

#### /nerdplot version
Print version info

#### /plot thicken
Spawn Slime blocks somehow ?

#### /plot murder
lol?


#### Permission Nodes
* nerdplot.claim
* nerdplot.list
* nerdplot.listany
* nerdplot.max
* nerdplot.setmax
* nerdplot.create
* nerdplot.remove
* nerdplot.addowner
* nerdplot.removeowner
* nerdplot.createarea
* nerdplot.removearea
* nerdplot.cleanup
* nerdplot.version

