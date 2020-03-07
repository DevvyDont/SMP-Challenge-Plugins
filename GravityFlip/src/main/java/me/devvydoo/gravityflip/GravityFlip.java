package me.devvydoo.gravityflip;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitRunnable;

public final class GravityFlip extends JavaPlugin implements Listener {

    private boolean gravityFlipping = false;
    private boolean wantDebugSeconds = false;

    public boolean isGravityFlipping() {
        return gravityFlipping;
    }

    public void setGravityFlipping(boolean gravityFlipping) {
        int delay = wantDebugSeconds ? 5 : 300;
        if (gravityFlipping) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    activateGravity();
                }
            }.runTaskLater(this, 20 * delay);
        }
        this.gravityFlipping = gravityFlipping;
    }

    private void activateGravity(){

        for (Player player : getServer().getOnlinePlayers()){
            for (int x = player.getLocation().getBlockX() - 30; x <= player.getLocation().getBlockX() + 30; x++){
                for (int y = player.getLocation().getBlockY() - 10; y <= player.getLocation().getBlockY() + 5; y++){
                    for (int z = player.getLocation().getBlockZ() - 30; z <= player.getLocation().getBlockZ() + 30; z++){

                        Block b = player.getWorld().getBlockAt(x, y, z);

                        if (!player.getWorld().getBlockAt(x, y + 1, z).getType().equals(Material.AIR))
                            continue;

                        if (b.getType().equals(Material.SAND) || b.getType().equals(Material.GRAVEL)){

                            if (Math.random() > .5)
                                continue;

                            FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
                            fb.setGravity(false);
                            b.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        // How many seconds should this go on?
        int secondsOfZeroGravity = (int)( Math.random() * 20 + 30);
        getLogger().info(ChatColor.BLUE + "Activating zero-gravity for " + ChatColor.AQUA + secondsOfZeroGravity + ChatColor.BLUE + " seconds");

        new BukkitRunnable() {

            int runs = 0;

            @Override
            public void run() {

                runs++;

                for (World world : getServer().getWorlds()){

                    if (world.getPlayerCount() == 0)
                        continue;

                    for (Entity entity : world.getEntities()) {
                        entity.setGravity(false);
                        entity.setVelocity(entity.getVelocity().setY(.08));
                    }
                }

                if (runs > secondsOfZeroGravity * 10 || !isGravityFlipping()) {
                    getLogger().info(ChatColor.RED + "Disabling zero-gravity... ");
                    for (World world : getServer().getWorlds()) {
                        for (Entity entity : world.getEntities())
                            entity.setGravity(true);
                    }
                    this.cancel();
                }

            }
        }.runTaskTimerAsynchronously(this, 1, 2);

        // Now that we started our zero gravity, lets set the next one to be anywhere from 2-5 minutes later
        int secondsAfter = wantDebugSeconds ? (int)(Math.random() * 10 + 5) : (int)(Math.random() * 120 + 60);
        int nextCycleSeconds = secondsOfZeroGravity + secondsAfter;
        getLogger().info(ChatColor.BLUE + "Next cycle starts " + ChatColor.AQUA + nextCycleSeconds + ChatColor.BLUE + " seconds from now");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isGravityFlipping())
                    activateGravity();
            }
        }.runTaskLater(this, nextCycleSeconds * 20);

    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("gravityflip").setExecutor(new GravityStartCommand(this));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        event.getPlayer().setGravity(true);
    }

}
