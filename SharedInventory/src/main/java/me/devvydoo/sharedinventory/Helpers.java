package me.devvydoo.sharedinventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Helpers {

    public static boolean inventoryIsEmpty(Player player){

        for (int i = 0; i < player.getInventory().getContents().length - 1; i++){
            ItemStack item = player.getInventory().getContents()[i];
            if (item != null)
                return false;
        }
        return true;
    }

}
