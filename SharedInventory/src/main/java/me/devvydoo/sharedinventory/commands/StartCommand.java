package me.devvydoo.sharedinventory.commands;

import me.devvydoo.sharedinventory.SharedInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class StartCommand implements CommandExecutor {

    private SharedInventory plugin;

    public StartCommand(SharedInventory plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 1)
            return false;

        if (!(sender instanceof Player))
            return false;

        if (!sender.isOp())
            return false;

        if (args[0].equalsIgnoreCase("start")) {
            sender.sendMessage(ChatColor.GREEN + "Inventories are now shared!");
            plugin.getServer().getPluginManager().registerEvents(plugin, plugin);
            plugin.syncInventories((Player)sender);
            return true;
        } else if (args[0].equalsIgnoreCase("stop")) {
            sender.sendMessage(ChatColor.RED + "Turned off shared inventories!");
            HandlerList.unregisterAll((Listener) plugin);
            return true;
        }

        return false;

    }



}
