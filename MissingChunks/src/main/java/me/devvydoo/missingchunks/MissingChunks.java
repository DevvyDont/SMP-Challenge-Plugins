package me.devvydoo.missingchunks;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MissingChunks extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);

    }

    private boolean isEven(int i){
        return i % 2 == 0;
    }

    @EventHandler
    public void onChunkGenerate(ChunkLoadEvent event){

        if (!event.isNewChunk())
            return;

        int xCoord = event.getChunk().getX();
        int zCoord = event.getChunk().getZ();

        // For all the even rows, let the even columns be safely gen'ed
        if (isEven(xCoord)) {
            if (isEven(zCoord))
                return;
        }
        // For the odd rows, let the odd columns be safely gen'ed
        else {
            if (!isEven(zCoord))
                return;
        }

        Chunk chunk = event.getChunk();

        // This current implementation is EXTREMELY intensive on the server, going to need to either
        // a) pre-generate chunks before we allow people to play
        // b) figure out a more efficient way to set an entire chunk to air or load the chunk using a custom chunk gen
        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++){
                for (int y = 0; y < 160; y++){
                    Block block = chunk.getBlock(x, y, z);
                    if (block.getType() != Material.AIR)
                        block.setType(Material.AIR, false);
                }
            }
        }


    }

}
