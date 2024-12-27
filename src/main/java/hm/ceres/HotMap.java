package hm.ceres;

import hm.ceres.command.BaseTabCommand;
import hm.ceres.command.CommandHandler;
import hm.ceres.command.ModeMap;
import hm.ceres.command.commands.CreateImageCommand;
import hm.ceres.listener.PlayerListener;
import hm.ceres.yaml.ChuckDataFiles;
import hm.ceres.yaml.ChunkDataFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class HotMap extends JavaPlugin {
    @Getter
    private static HotMap instance;
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;
    public static final ChunkData[][] MAP = new ChunkData[WIDTH][HEIGHT];
    private static CommandHandler commandHandler;
    private static ChuckDataFiles chuckDataFiles;
    public static boolean running = true;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        commandHandler = new CommandHandler();
        register(new CreateImageCommand());
        chuckDataFiles = new ChuckDataFiles();
        chuckDataFiles.loadData();
        register(new PlayerListener());

        Bukkit.getLogger().info("Hot Map is running");
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!running) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ChunkData data = getChunkData(player.getLocation());
                    if (data != null) {
                        data.addActivityPlayer();
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 40L);
    }

    private void register(@NotNull BaseTabCommand command) {
        commandHandler.getCommands().add(command);
        PluginCommand pluginCommand = this.getCommand(command.getName());
        if (pluginCommand == null) {
            throw new CommandException(command.getName() + " El comando no existe. tiene que añadirlo en plugin.yml");
        }
        pluginCommand.setPermission(this.getName().toLowerCase() + ".command." + command.getName());
        pluginCommand.setDescription(command.getDescription());
        pluginCommand.setUsage(command.getUsage());
        pluginCommand.setExecutor(commandHandler);
        pluginCommand.setTabCompleter(commandHandler);
    }

    private void register(@NotNull Listener listener) {
        getServer().getPluginManager().registerEvents(listener , this);
    }

    public static void CreateImage(ModeMap mode){
        try {
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            for (int x = 0 ; x < HotMap.MAP.length ; x++){
                for (int z = 0 ; z < HotMap.MAP[x].length ; z++){
                    ChunkData data = MAP[x][z];
                    if (data != null){
                        int i;
                        switch (mode){
                            case PLAYERS -> i = data.getActivityPlayer();
                            case BREAK_BLOCKS -> i = data.getBreakBlock();
                            case PLACE_BLOCKS -> i = data.getPlaceBlock();
                            default -> i = 0;
                        }
                        i = Math.min(i, 255);
                        Color c = new Color(i, i, i);
                        image.setRGB(x,z, c.getRGB());
                    }
                }
            }
            g.dispose();
            ImageIO.write(image, "png", new File("C:/Users/Cagut/Desktop/hotmap.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable ChunkData getChunkData(@NotNull Location location){
        Chunk chunk = location.getChunk();
        int xR = chunk.getX();
        int zR = chunk.getZ();
        return getChunkData(xR, zR);
    }

    @Nullable
    public static ChunkData getChunkData(int xR, int zR) {
        int x = xR + HotMap.WIDTH/2;
        int z = zR + HotMap.HEIGHT/2;
        ChunkData data;
        if (x > 0 && z > 0 && x < HotMap.WIDTH && z < HotMap.HEIGHT){
            data = HotMap.MAP[x][z];
        }else {
            return null;
        }
        return Objects.requireNonNullElseGet(data, () -> {
            ChunkData chunkData = new ChunkData(xR, zR);
            HotMap.MAP[x][z] = chunkData;
            return chunkData;
        });
    }




    @Override
    public void onDisable() {
        chuckDataFiles.saveData();
    }
}
