package me.devvydoo.teamdeath;

import me.devvydoo.teamdeath.commands.StartCommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class TeamDeath extends JavaPlugin implements Listener {

    private boolean settingEnabled = false;
    private ArrayList<UUID> activePlayers = new ArrayList<>();

    public boolean isSettingEnabled() {
        return settingEnabled;
    }

    public void setSettingEnabled(boolean settingEnabled) {
        this.settingEnabled = settingEnabled;
    }

    public void updatePlayers(){
        activePlayers = new ArrayList<>();
        for (Player p : getServer().getOnlinePlayers()){
            if (p.getGameMode().equals(GameMode.SURVIVAL)) {
                activePlayers.add(p.getUniqueId());
                p.sendTitle(ChatColor.RED + "Don't Die!", ChatColor.GRAY + "Any deaths will kill everyone!", 10, 60, 30);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, .75f, 1);
            }
        }
    }

    @Override
    public void onEnable() {

        getServer().getPluginCommand("deathstart").setExecutor(new StartCommand(this));
        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onPlayerDied(PlayerDeathEvent event){

        if (!settingEnabled || !activePlayers.contains(event.getEntity().getUniqueId())) {
            event.setDeathMessage(ChatColor.DARK_RED + "☠ " + ChatColor.RED + event.getEntity().getDisplayName());
            return;
        }

        settingEnabled = false;

        event.setDeathMessage(ChatColor.DARK_RED + "☠ " + ChatColor.RED + ChatColor.BOLD + event.getEntity().getDisplayName());
        getServer().broadcastMessage(ChatColor.GRAY + "Everyone was killed by " + ChatColor.RED + ChatColor.BOLD + event.getEntity().getDisplayName() + ChatColor.GRAY + " since they died!");

        for (Player p : this.getServer().getOnlinePlayers()){

            if (p.equals(event.getEntity()))
                continue;

            p.damage(Integer.MAX_VALUE, event.getEntity());
            p.playSound(p.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, .75f, .5f);
        }

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){

        if (!settingEnabled || event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
            event.setFormat(ChatColor.GREEN + "%s: " + ChatColor.GRAY + "%s");
        } else {
            event.setFormat(ChatColor.GRAY + "[SPECTATING] " + ChatColor.GRAY + "%s: " + ChatColor.DARK_GRAY + "%s");
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        if (!settingEnabled || activePlayers.contains(event.getPlayer().getUniqueId())) {
            event.setJoinMessage(ChatColor.DARK_GREEN + event.getPlayer().getDisplayName() + ChatColor.GREEN + " has joined!");
            return;
        }

        event.setJoinMessage(ChatColor.GRAY + "Spectator " + ChatColor.GREEN + event.getPlayer().getDisplayName() + ChatColor.GRAY + " has joined");

        event.getPlayer().setGameMode(GameMode.SPECTATOR);
        event.getPlayer().sendActionBar(ChatColor.RED + "A challenge is in progress, but you can spectate!");

        for (Player p : getServer().getOnlinePlayers()) {
            if (!p.equals(event.getPlayer())){
                event.getPlayer().teleport(p.getLocation());
                break;
            }
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

        if (!settingEnabled || activePlayers.contains(event.getPlayer().getUniqueId())) {
            event.setQuitMessage(ChatColor.DARK_RED + event.getPlayer().getDisplayName() + ChatColor.RED + " has left!");
            return;
        }

        event.setQuitMessage(ChatColor.GRAY + "Spectator " + ChatColor.RED + event.getPlayer().getDisplayName() + ChatColor.GRAY +  " has left");

    }

}
