package me.devvydoo.gravityflip;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GravityStartCommand implements CommandExecutor {

    private GravityFlip plugin;

    public GravityStartCommand(GravityFlip plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Args?
        if (args.length < 1){
            sender.sendMessage(ChatColor.RED + "Please provide an argument. " + ChatColor.DARK_RED + "< start | stop >");
            return true;
        }

        String arg = args[0];

        if (arg.equalsIgnoreCase("start")){

            if (plugin.isGravityFlipping()){
                sender.sendMessage(ChatColor.RED + "Gravity is already flipping!");
                return true;
            } else {
                sender.sendMessage(ChatColor.GREEN + "Gravity is now flipping!");
                plugin.getLogger().info(ChatColor.GOLD + "Zero Gravity has been turned on! First run is in 300 seconds.");
                plugin.setGravityFlipping(true);
                return true;
            }

        } else if (arg.equalsIgnoreCase("stop")){
            if (plugin.isGravityFlipping()){
                sender.sendMessage(ChatColor.RED + "Gravity is no longer flipping!");
                plugin.setGravityFlipping(false);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Gravity is not flipping!");
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown argument: " + ChatColor.DARK_RED + arg);
            return true;
        }

    }


}
