package me.devvy.bestfriendsforever.commands;

import me.devvy.bestfriendsforever.BestFriendsForever;
import me.devvy.bestfriendsforever.tasks.BoundingBoxTask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartChallengeCommand implements CommandExecutor {

    private BestFriendsForever plugin;

    public StartChallengeCommand(BestFriendsForever plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        } else if (plugin.getChallengeTask() != null){
            sender.sendMessage(ChatColor.RED + "The challenge is already running! Use /stop");
            return true;
        }

        Player randomPlayer = (Player) plugin.getServer().getOnlinePlayers().toArray()[(int)(Math.random() * plugin.getServer().getOnlinePlayers().size())];

        for (Player player : plugin.getServer().getOnlinePlayers()){
            player.sendTitle(ChatColor.RED + "Stick Together!", ChatColor.GRAY + "Everyone has to stay close to " + ChatColor.GREEN + randomPlayer.getDisplayName(), 10, 60, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, .3f, .8f);
            if (!player.equals(randomPlayer)){
                player.teleport(randomPlayer);
            }
        }


        plugin.setChallengeTask(new BoundingBoxTask(plugin, randomPlayer));
        plugin.getChallengeTask().runTaskTimer(plugin, 20, 10);

        plugin.getServer().getPluginManager().registerEvents(plugin, plugin);
        sender.sendMessage(ChatColor.GREEN + "Started the challenge!");
        return true;

    }
}
