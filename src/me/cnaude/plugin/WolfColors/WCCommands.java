/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.WolfColors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class WCCommands implements CommandExecutor {
    
    private final WCMain plugin;

    public WCCommands (WCMain instance) {
        this.plugin = instance;        
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        } 
        if (args.length == 3) {
            // 0 == set, 1 == name, 2 == color
            if (args[0].equalsIgnoreCase("set")) {
                if (sender.hasPermission("wolfcolors.set.other") || !(sender instanceof Player)) {
                    Player player = Bukkit.getPlayer(args[1]);
                    DyeColor dc;
                    if (player == null) {  
                        sender.sendMessage(ChatColor.RED + "Invalid player specified.");
                        return true;
                    }
                    try {
                        dc = DyeColor.valueOf(args[2].toUpperCase());
                    }
                    catch (Exception ex){
                        sender.sendMessage(ChatColor.RED + "Invalid color specified: " + args[2]);
                        return true;
                    }
                    sender.sendMessage(ChatColor.YELLOW + "Setting player " 
                            + player.getName() + "'s default wolf collar to " + dc.name());
                    plugin.plColors.put(player.getName(), dc.name());
                } else {
                    sender.sendMessage(ChatColor.RED + "You have no permission to run that command.");
                }
            } 
        } else if (args.length == 2) {  
            // 0 = set, 1 = color
            if (args[0].equalsIgnoreCase("set")) {
                if (sender.hasPermission("wolfcolors.set.self") && (sender instanceof Player)) {
                    Player player = (Player)sender;
                    DyeColor dc;
                    try {
                        dc = DyeColor.valueOf(args[1].toUpperCase());
                    }
                    catch (Exception ex){
                        sender.sendMessage(ChatColor.RED + "Invalid color specified: " + args[1]);
                        return true;
                    }
                    sender.sendMessage(ChatColor.YELLOW + "Setting default wolf collar to " + dc.name());
                    plugin.plColors.put(player.getName(), dc.name());
                } else {
                    sender.sendMessage(ChatColor.RED + "You have no permission to run that command.");
                }
            } else {
                sender.sendMessage("/wc set ([player]) [color]");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("wolfcolors.list") || !(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.GREEN + "=== [ " + ChatColor.WHITE + "Wolf Collar Colors" 
                            +ChatColor.GREEN + "] ===");
                    for (String s : plugin.plColors.keySet()) {
                        sender.sendMessage(ChatColor.YELLOW + s + ChatColor.BLUE + " => " 
                                + ChatColor.YELLOW + plugin.plColors.get(s));             
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You have no permission to run that command.");
                } 
            }
        } else {
            sender.sendMessage("/wc set ([player]) [color]");
        }
        return true;
    }
}
