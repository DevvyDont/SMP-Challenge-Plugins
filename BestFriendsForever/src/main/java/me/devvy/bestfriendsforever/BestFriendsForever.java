package me.devvy.bestfriendsforever;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.devvy.bestfriendsforever.commands.StartChallengeCommand;
import me.devvy.bestfriendsforever.tasks.BoundingBoxTask;
import me.devvy.bestfriendsforever.commands.StopChallengeCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class BestFriendsForever extends JavaPlugin implements Listener {

    private BoundingBoxTask challengeTask;

    public BoundingBoxTask getChallengeTask() {
        return challengeTask;
    }

    public void setChallengeTask(BoundingBoxTask challengeTask) {
        this.challengeTask = challengeTask;
    }

    @Override
    public void onEnable() {

        getCommand("start").setExecutor(new StartChallengeCommand(this));
        getCommand("stop").setExecutor(new StopChallengeCommand(this));

    }

    @EventHandler
    public void onLeaderDied(PlayerDeathEvent event){

        if (challengeTask == null)
            return;

        if (event.getEntity().equals(challengeTask.getLeader())){
            for (Player player : getServer().getOnlinePlayers()){
                if (!player.equals(event.getEntity())){
                    player.damage(Integer.MAX_VALUE);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){

        if (challengeTask == null)
            return;

        if (challengeTask.getLeader().isDead()){
            challengeTask.setLeader(event.getPlayer());
            return;
        }

        event.setRespawnLocation(challengeTask.getLeader().getLocation());
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event){

        if (challengeTask == null || challengeTask.getLeader().isDead())
            return;

        event.getPlayer().setHealth(16);
        event.getPlayer().setFoodLevel(16);
        event.getPlayer().setSaturation(1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

        if (challengeTask == null)
            return;

        if (challengeTask.getLeader().equals(event.getPlayer()))
            challengeTask.newLeader();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){



    }
}
