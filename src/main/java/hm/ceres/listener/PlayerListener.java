package hm.ceres.listener;

import hm.ceres.ChunkData;
import hm.ceres.HeatMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class PlayerListener implements Listener {

    private static final long AFK_TIME_THRESHOLD = 1000*30; // 5 minutos en milisegundos
    public static final HashSet<UUID> AFK_PLAYERS = new HashSet<>();
    private final HashMap<UUID, Long> lastActivity = new HashMap<>();


    public PlayerListener() {
        startAFKCheckTask();
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void placeBLock(BlockPlaceEvent e) {
        if (!HeatMap.running) return;
        ChunkData data = HeatMap.getChunkData(e.getBlock().getLocation());
        if (data != null) {
            data.addPlaceBlock();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void breakBlock(BlockBreakEvent e) {
        if (!HeatMap.running) return;
        ChunkData data = HeatMap.getChunkData(e.getBlock().getLocation());
        if (data != null) {
            data.addBreakBlock();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerMoveEvent e) {
        updateLastActivity(e.getPlayer());
    }

    private void updateLastActivity(Player player) {
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private void startAFKCheckTask() {
        Bukkit.getScheduler().runTaskTimer(HeatMap.getInstance(), () -> {
            long currentTime = System.currentTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                long lastActiveTime = lastActivity.getOrDefault(player.getUniqueId(), currentTime);
                if (currentTime - lastActiveTime >= AFK_TIME_THRESHOLD) {
                    // El jugador está AFK
                    AFK_PLAYERS.add(player.getUniqueId());
                }else {
                    AFK_PLAYERS.remove(player.getUniqueId());
                }
            }
        }, 0L, 20*2); // Comprobación cada minuto
    }
    /*

    @EventHandler(priority = EventPriority.LOWEST)
    public void death(PlayerDeathEvent e) {
        if (!HotMap.running) return;
    }*/
}
