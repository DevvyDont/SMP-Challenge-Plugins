package me.devvydoo.randomizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTables;
import org.bukkit.loot.Lootable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class Randomizer extends JavaPlugin implements Listener {


    private final HashMap<Material, Material> materialMap = new HashMap<>();
    private final HashMap<EntityType, LootTables> entityMap = new HashMap<>();

    private Material getRandomMaterial(){
        int randomIndex = (int)(Math.random() * Material.values().length);
        Material ret = Material.values()[randomIndex];

        if (!ret.isItem())
            return getRandomMaterial();

        return ret;

    }

    private LootTables getRandomLootTable(){
        int randomIndex = (int)(Math.random() * LootTables.values().length);
        return LootTables.values()[randomIndex];
    }

    private ArrayList<ItemStack> getRandomizedItemDrops(Collection<ItemStack> originalDrops) {
        ArrayList<ItemStack> drops = new ArrayList<>();

        for (ItemStack drop : originalDrops) {
            drop.setType(materialMap.get(drop.getType()));
            drops.add(drop);
        }

        return drops;
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

        ArrayList<Material> shuffledMats = new ArrayList<>(Arrays.asList(Material.values()));

        // Remove things that break
        shuffledMats.removeIf(m -> (!m.isItem()));

        // Shuffle
        Collections.shuffle(shuffledMats);

        // Assign as much as we can
        for (int i = 0; i < shuffledMats.size(); i++)
            materialMap.put(Material.values()[i], shuffledMats.get(i));

        // Assign the rest random materials
        for (int i = shuffledMats.size(); i < Material.values().length; i++)
            materialMap.put(Material.values()[i], getRandomMaterial());

        getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Items have been randomized!!!");
        getServer().getPluginManager().registerEvents(this, this);



        for (EntityType entityType : EntityType.values())
            entityMap.put(entityType, getRandomLootTable());
//        doRecycleTaskLater();
    }

    /**
     * Everytime a block is broken by a player, replace the drops of the block with something random
     *
     * @param event
     */
    @EventHandler
    public void onBlockBroken(BlockBreakEvent event){

        ArrayList<ItemStack> itemsToDrop = getRandomizedItemDrops(event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand()));

        event.setDropItems(false);
        for (ItemStack item : itemsToDrop)
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);

    }

    /**
     * Every time an entity is defeated, randomize the drops
     *
     * @param event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){

        if (event.getEntity() instanceof Player)
            return;

        ArrayList<ItemStack> itemsToDrop = getRandomizedItemDrops(event.getDrops());
        event.getDrops().clear();
        event.getDrops().addAll(itemsToDrop);
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
            ArrayList<ItemStack> itemsToDrop = getRandomizedItemDrops(Collections.singletonList(item.getItemStack()));

            if (itemsToDrop.isEmpty())
                return;

            for (int i = 0; i < itemsToDrop.size(); i++) {

                if (i == 0)
                    item.getItemStack().setType(itemsToDrop.get(i).getType());
                else
                    event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), itemsToDrop.get(i));


            }

        }
    }

}
