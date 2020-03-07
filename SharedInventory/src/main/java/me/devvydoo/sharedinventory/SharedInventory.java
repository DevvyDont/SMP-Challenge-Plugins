package me.devvydoo.sharedinventory;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import me.devvydoo.sharedinventory.commands.StartCommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public final class SharedInventory extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getCommand("sharedinv").setExecutor(new StartCommand(this));
    }

    public void syncInventories(Player masterInventory){

        new BukkitRunnable() {

            @Override
            public void run() {

                for (Player player : getServer().getOnlinePlayers()) {

                    ItemStack[] actualItems = masterInventory.getInventory().getContents();

                    if (player.getGameMode().equals(GameMode.SURVIVAL) && !player.equals(masterInventory)) {

                        for (int i = 0; i < masterInventory.getInventory().getContents().length; i++){
                            PlayerInventory playerInventory = player.getInventory();


                            ItemStack oldItem = playerInventory.getItem(i);
                            ItemStack newItem = actualItems[i];

                            if (newItem == null)
                                playerInventory.setItem(i, null);
                            else if (oldItem == null)
                                playerInventory.setItem(i, newItem);
                            else if (!newItem.isSimilar(oldItem))
                                playerInventory.setItem(i, newItem);
                            else if (newItem.getAmount() != oldItem.getAmount())
                                oldItem.setAmount(newItem.getAmount());
                        }

                    }
                }

            }
        }.runTaskLater(this, 0);

    }

    @EventHandler
    public void onPlayerInteractedWithInventory(InventoryClickEvent event){
        syncInventories((Player)event.getWhoClicked());
    }

    @EventHandler
    public void onPlayerInteractedWithInventory2(InventoryDragEvent event){
        syncInventories((Player)event.getWhoClicked());
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event){

        if (event.getEntity() instanceof Player){
            for (Player player : getServer().getOnlinePlayers()){
                if (!player.equals(event.getEntity())){
                    player.getInventory().addItem(event.getItem().getItemStack());
                }
            }
        }

    }

    @EventHandler
    public void onPlayerThrewItem(PlayerDropItemEvent event){

        for (Player player : getServer().getOnlinePlayers()){
            if (!player.equals(event.getPlayer())){
                player.getInventory().removeItem(event.getItemDrop().getItemStack());
            }
        }

    }

    @EventHandler
    public void onPlayerDurabilityUse(PlayerItemDamageEvent event){

        int slot = event.getPlayer().getInventory().first(event.getItem());

        // If we got -1, that means it is armor
        if (slot == -1){
            if (event.getPlayer().getInventory().getHelmet() != null && event.getPlayer().getInventory().getHelmet().isSimilar(event.getItem())){
                slot = 39;
            }
            else if (event.getPlayer().getInventory().getChestplate() != null && event.getPlayer().getInventory().getChestplate().isSimilar(event.getItem())){
                slot = 38;
            }
            else if (event.getPlayer().getInventory().getLeggings() != null && event.getPlayer().getInventory().getLeggings().isSimilar(event.getItem())){
                slot = 37;
            }
            else if (event.getPlayer().getInventory().getBoots() != null && event.getPlayer().getInventory().getBoots().isSimilar(event.getItem())){
                slot = 36;
            }
        }

        for (Player player : getServer().getOnlinePlayers()){
            if (!player.equals(event.getPlayer())){

                ItemStack itemHurt = player.getInventory().getItem(slot);

                if (itemHurt != null && itemHurt.getItemMeta() instanceof Damageable) {
                    Damageable meta = (Damageable) itemHurt.getItemMeta();
                    meta.setDamage(meta.getDamage() + event.getDamage());
                    itemHurt.setItemMeta((ItemMeta) meta);

                }
            }
        }

    }

    @EventHandler
    public void onItemBroke(PlayerItemBreakEvent event){

        int slot = event.getPlayer().getInventory().first(event.getBrokenItem());
        // If we got -1, that means it is armor
        if (slot == -1){
            if (event.getPlayer().getInventory().getHelmet() != null && event.getPlayer().getInventory().getHelmet().isSimilar(event.getBrokenItem())){
                slot = 39;
            }
            else if (event.getPlayer().getInventory().getChestplate() != null && event.getPlayer().getInventory().getChestplate().isSimilar(event.getBrokenItem())){
                slot = 38;
            }
            else if (event.getPlayer().getInventory().getLeggings() != null && event.getPlayer().getInventory().getLeggings().isSimilar(event.getBrokenItem())){
                slot = 37;
            }
            else if (event.getPlayer().getInventory().getBoots() != null && event.getPlayer().getInventory().getBoots().isSimilar(event.getBrokenItem())){
                slot = 36;
            }
        }

        for (Player player : getServer().getOnlinePlayers()){
            if (!player.equals(event.getPlayer())){
                player.getInventory().setItem(slot, null);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            }
        }
    }

    @EventHandler
    public void onPlayerSwitchOffhand(PlayerSwapHandItemsEvent event){


        int hotbarSlot = event.getPlayer().getInventory().getHeldItemSlot();

        for (Player player : getServer().getOnlinePlayers()){
            if (!player.equals(event.getPlayer())){
                player.getInventory().setItemInOffHand(event.getOffHandItem());
                player.getInventory().setItem(hotbarSlot, event.getMainHandItem());
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
            }
        }


    }

    @EventHandler
    public void onPlayerEatItem(PlayerItemConsumeEvent event){

        int slot = -1;
        for (int i = 0; i < event.getPlayer().getInventory().getContents().length; i++){
            if (event.getPlayer().getInventory().getContents()[i] != null && event.getPlayer().getInventory().getContents()[i].equals(event.getItem())){
                slot = i;
                break;
            }
        }

        if (slot == -1){
            getLogger().warning("Could not find food to consume for others");
            return;
        }

        for (Player player : getServer().getOnlinePlayers()){
            if (!player.equals(event.getPlayer())){
                player.getInventory().setItem(slot, event.getReplacement());
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
            }
        }

        syncInventories(event.getPlayer());

    }

    @EventHandler
    public void onPlayerShotBow(EntityShootBowEvent event){

        if (!(event.getEntity() instanceof Player))
            return;

        if (!event.getConsumeArrow())
            return;

        for (Player player : getServer().getOnlinePlayers()){
            if (!event.getEntity().equals(player)){
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
            }
        }

        syncInventories((Player)event.getEntity());

    }

    @EventHandler
    public void onPlayerInteractedWithItem(PlayerInteractEvent event){

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR) || event.getItem().getType().equals(Material.SHIELD) || event.getItem().getType().equals(Material.BOW))
            return;

        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        if (event.getItem().getType().isEdible()){
            return;
        }
        else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)){
            syncInventories(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerPickupExperience(PlayerPickupExperienceEvent event){

        for (Player player : getServer().getOnlinePlayers()){

            if (!player.equals(event.getPlayer())){
                player.giveExp(event.getExperienceOrb().getExperience());
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .5f, 1);
            }

        }

    }

    @EventHandler
    public void onPlayerLevelDecreased(PlayerLevelChangeEvent event){

        if (event.getNewLevel() < event.getOldLevel()){

            for (Player player : getServer().getOnlinePlayers()){
                if (!player.equals(event.getPlayer())){
                    player.setLevel(event.getNewLevel());
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, .5f, 1);
                }
            }

        }

    }

    @EventHandler
    public void onPlayerDied(PlayerDeathEvent event){

        int numPlayers = getServer().getOnlinePlayers().size();
        int numItems = 0;

        for (int i = 0; i < event.getEntity().getInventory().getContents().length - 1; i++){
            ItemStack item = event.getEntity().getInventory().getContents()[i];
            if (item != null)
                numItems += item.getAmount();
        }

        if (numItems == 0)
            return;

        event.setKeepInventory(true);
        event.getDrops().clear();

        int numItemsToDrop = numItems / numPlayers;

        // While we need more items and the inventory is not empty
        while ( numItemsToDrop > 0 && !Helpers.inventoryIsEmpty(event.getEntity())){

            // Find an item starting at a random index
            int startIndex = (int)(Math.random() * event.getEntity().getInventory().getContents().length - 1);
            // Backwards or forwards?
            int iter = Math.random() < .5 ? 1 : -1;
            // Iterate until we hit an item
            for (int i = startIndex; i >= 0 && i < event.getEntity().getInventory().getContents().length - 1; i += iter){

                // If we find an actual item
                if (event.getEntity().getInventory().getContents()[i] != null && !event.getEntity().getInventory().getContents()[i].getType().equals(Material.AIR)){

                    ItemStack itemStack = event.getEntity().getInventory().getContents()[i];  // Get item
                    int dropAmount = (int)(Math.random() * itemStack.getAmount());  // Get amount to drop randomly
                    dropAmount = Math.max(itemStack.getMaxStackSize() / 2 + 1, dropAmount);  // Don't drop less than half
                    dropAmount = Math.min(dropAmount, itemStack.getAmount());  // Don't drop more than we have

                    event.getDrops().add(itemStack.asQuantity(dropAmount));  // Add what we should take away to the drops upon death
                    event.getEntity().getInventory().remove(itemStack.asQuantity(dropAmount));
                    numItemsToDrop -= dropAmount;  // Adjust # items to drop later
                    break;
                }
            }
        }

        // Loop through all players except the player that died, sync their inventory
        for (Player player : getServer().getOnlinePlayers()){
            if (!player.equals(event.getEntity())){
                player.getInventory().setContents(event.getEntity().getInventory().getContents());
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, .5f, 1);
                player.damage(1, event.getEntity());
                player.sendTitle("", ChatColor.RED + event.getEntity().getDisplayName() + ChatColor.GRAY + " died and lost items!", 10, 60, 20);
            }
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        // We need to find an online player to sync inventories with, otherwise just don't do anything
        for (Player p : getServer().getOnlinePlayers()){
            if (!p.equals(event.getPlayer())){
                event.getPlayer().getInventory().setContents(p.getInventory().getContents());
                return;
            }
        }
    }
}
