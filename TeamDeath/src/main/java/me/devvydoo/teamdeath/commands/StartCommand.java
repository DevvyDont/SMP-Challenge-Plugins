package me.devvydoo.teamdeath.commands;

import me.devvydoo.teamdeath.TeamDeath;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCommand implements CommandExecutor {


    private TeamDeath plugin;

    public StartCommand(TeamDeath plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()){

            if (!plugin.isSettingEnabled()){

                sender.sendMessage(ChatColor.GREEN + "Enabling the plugin, Don't Die!!!");
                plugin.setSettingEnabled(true);
                plugin.updatePlayers();

            } else {

                sender.sendMessage(ChatColor.RED + "The plugin was already enabled, disabling...");
                plugin.setSettingEnabled(false);

            }

        } else {

            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");

        }

        return true;

    }
}
