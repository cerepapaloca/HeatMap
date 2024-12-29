package hm.ceres.listener;

import hm.ceres.ChunkData;
import hm.ceres.HotMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void placeBLock(BlockPlaceEvent e) {
        if (!HotMap.running) return;
        ChunkData data = HotMap.getChunkData(e.getBlock().getLocation());
        if (data != null) {
            data.addPlaceBlock();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void breakBlock(BlockBreakEvent e) {
        if (!HotMap.running) return;
        ChunkData data = HotMap.getChunkData(e.getBlock().getLocation());
        if (data != null) {
            data.addBreakBlock();
        }
    }/*

    @EventHandler(priority = EventPriority.LOWEST)
    public void death(PlayerDeathEvent e) {
        if (!HotMap.running) return;
    }*/
}
