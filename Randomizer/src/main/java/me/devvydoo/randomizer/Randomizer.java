package me.devvydoo.randomizer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public final class Randomizer extends JavaPlugin implements Listener {


    private HashMap<Material, Material> materialMap = new HashMap<>();

    private Material getRandomMaterial(){
        int randomIndex = (int)(Math.random() * Material.values().length);
        Material ret = Material.values()[randomIndex];

        if (!ret.isItem())
            return getRandomMaterial();

        return ret;
    }

    private void doRecycleTaskLater(){

        new BukkitRunnable() {
            @Override
            public void run() {
                materialMap.clear();
                for (Material material : Material.values())
                    materialMap.put(material, getRandomMaterial());
                getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Items have been randomized!!!");
                doRecycleTaskLater();
            }
        }.runTaskLater(this, 300 * 20);

    }


    @Override
    public void onEnable() {

        // Assign a random material to map to a different material for every material in the game
        for (Material material : Material.values()){
            materialMap.put(material, getRandomMaterial());
        }

        getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Items have been randomized!!!");

        getServer().getPluginManager().registerEvents(this, this);
        doRecycleTaskLater();
    }

    /**
     * Everytime a block is broken by a player, replace the drops of the block with something random
     *
     * @param event
     */
    @EventHandler
    public void onBlockBroken(BlockBreakEvent event){

        ArrayList<ItemStack> itemsToDrop = new ArrayList<>();

        for (ItemStack drop : event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand())){
            drop.setType(materialMap.get(drop.getType()));
            itemsToDrop.add(drop);
        }

        event.setDropItems(false);
        for (ItemStack item : itemsToDrop) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
        }

    }

    /**
     * Every time an entity is defeated, randomize the drops
     *
     * @param event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        for (ItemStack drop : event.getDrops()){
            drop.setType(materialMap.get(drop.getType()));
        }
    }

    /**
     * Every time a fish is caught and it is a valid item stack, randomize it
     *
     * @param event
     */
    @EventHandler
    public void onFishCatch(PlayerFishEvent event){
        if (event.getCaught() instanceof Item){
            Item item = (Item) event.getCaught();
            item.getItemStack().setType(materialMap.get(item.getItemStack().getType()));
        }
    }

}
