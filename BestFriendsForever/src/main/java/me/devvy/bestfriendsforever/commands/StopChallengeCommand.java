package me.devvy.bestfriendsforever.commands;

import me.devvy.bestfriendsforever.BestFriendsForever;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class StopChallengeCommand implements CommandExecutor {

    private BestFriendsForever plugin;

    public StopChallengeCommand(BestFriendsForever plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (plugin.getChallengeTask() == null){
            sender.sendMessage(ChatColor.RED + "There is no challenge running! Use /start");
            return true;
        }

        plugin.getChallengeTask().cancel();
        plugin.setChallengeTask(null);

        HandlerList.unregisterAll((Listener) plugin);
        sender.sendMessage(ChatColor.GREEN + "Challenge is ended!");
        return true;
    }
}
