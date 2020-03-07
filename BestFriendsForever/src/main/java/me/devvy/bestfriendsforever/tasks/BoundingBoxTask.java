package me.devvy.bestfriendsforever.tasks;

import me.devvy.bestfriendsforever.BestFriendsForever;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BoundingBoxTask extends BukkitRunnable {

    private BestFriendsForever plugin;
    private Player leader;
    private int cycle;

    public BoundingBoxTask(BestFriendsForever plugin, Player leader) {
        this.plugin = plugin;
        setLeader(leader);
        this.cycle = 0;
    }

    public Player getLeader() {
        return leader;
    }

    private Player getRandomPlayer(){

        boolean validPlayer = false;
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()){
            if (!onlinePlayer.isDead()) {
                validPlayer = true;
                break;
            }
        }

        if (!validPlayer)
            this.cancel();

        Player randomPlayer = (Player) plugin.getServer().getOnlinePlayers().toArray()[(int)(Math.random() * plugin.getServer().getOnlinePlayers().size())];
        if (randomPlayer.equals(leader) && plugin.getServer().getOnlinePlayers().size() > 1)
            return getRandomPlayer();
        return randomPlayer;
    }

    public void setLeader(Player player){

        this.leader = player;

        for (Player oPlayer : plugin.getServer().getOnlinePlayers())
            oPlayer.sendTitle("", ChatColor.GREEN + leader.getDisplayName() + ChatColor.GRAY + " is the new leader!", 10, 40, 10);

    }

    public void newLeader(){

        cycle = 0;
        setLeader(getRandomPlayer());
    }

    @Override
    public void run() {

        cycle++;

        if (cycle > 600)
            newLeader();

        if (leader.isDead())
            return;

        for (Player player : plugin.getServer().getOnlinePlayers()){
            if (player.equals(leader)){
                player.setPlayerListName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "LEADER " + ChatColor.GOLD + player.getDisplayName());
            } else {
                player.setPlayerListName(ChatColor.GREEN + player.getDisplayName());
            }
        }

        int extraBlocks = (int)(plugin.getServer().getOnlinePlayers().size() * 1.5 + 5);

        double xMin = leader.getEyeLocation().getX() - extraBlocks;
        double yMin = leader.getEyeLocation().getY() - extraBlocks;
        double zMin = leader.getEyeLocation().getZ() - extraBlocks;

        double xMax = leader.getEyeLocation().getX() + extraBlocks;
        double yMax = leader.getEyeLocation().getY() + extraBlocks;
        double zMax = leader.getEyeLocation().getZ() + extraBlocks;

        // Draw the bounding box, cubic loop, but either x, y, or z has to be a max
        for (double x = xMin; x <= xMax; x++){
            for (double y = yMin; y <= yMax; y++){
                for (double z = zMin; z <= zMax; z++){

                    // We only care if either x, y, or z is a max or min value
                    if (x == xMin || x == xMax ||
                            y == yMin || y == yMax ||
                            z == zMin || z == zMax)
                        leader.getWorld().spawnParticle(Particle.REDSTONE, x, y, z, 1, new Particle.DustOptions(Color.PURPLE, 1));
                }
            }
        }

        if (cycle < 4)
            return;

        ArrayList<Player> outsidePlayers = new ArrayList<>();

        // Loop through every player on the server except the leader
        for (Player player : plugin.getServer().getOnlinePlayers()){

            if (player.equals(leader))
                continue;

            if (player.isDead())
                continue;

            // If they are more than 10 blocks away we need to do something
            Location location = player.getEyeLocation();

            if (location.getX() > xMax || location.getX() < xMin || location.getY() > yMax || location.getY() < yMin || location.getZ() > zMax || location.getZ() < zMin){
                player.sendTitle(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "You are too far away!!!", ChatColor.GRAY + "Get closer to the group!", 0, 20, 20);
                outsidePlayers.add(player);
            }
        }

        // If we have some players outside
        if (outsidePlayers.size() > 0){

            StringBuilder string = new StringBuilder();

            if (outsidePlayers.size() == 1){
                string.append(ChatColor.RED).append(outsidePlayers.get(0).getDisplayName()).append(" is ");
            } else {
                string.append(ChatColor.RED.toString());
                for (Player player : outsidePlayers){
                    string.append(player.getDisplayName());

                    if (outsidePlayers.indexOf(player) != outsidePlayers.size() - 1)
                        string.append(", ");
                }
                string.append(" are ");
            }

            string.append("too far away!");

            // Loop through every player and damage them
            for (Player player : plugin.getServer().getOnlinePlayers()){
                int mult = !outsidePlayers.contains(player) ? 1 : 3;
                player.damage(outsidePlayers.size() * mult);
                player.sendActionBar(string.toString());
            }

        }

    }


}
