package hm.ceres;

import hm.ceres.command.BaseTabCommand;
import hm.ceres.command.CommandHandler;
import hm.ceres.command.commands.HeatMapCommand;
import hm.ceres.listener.PlayerListener;
import hm.ceres.yaml.ChuckDataFiles;
import hm.ceres.yaml.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class HeatMap extends JavaPlugin {

    @Getter
    private static HeatMap instance;
    public static int width;
    public static int height;
    public static String path;
    public static ChunkData[][] matrixMap;
    private static CommandHandler commandHandler;
    @Getter
    public static ChuckDataFiles chuckDataFiles;
    public static boolean running = true;
    public static ConfigFile config;
    private Thread workerThread;
    private static final BlockingQueue<Runnable> TASK_QUEUE = new LinkedBlockingQueue<>();
    private static final HashSet<UUID> AKF_PLAYERS = new HashSet<>();



    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        config = new ConfigFile();
        matrixMap = new ChunkData[width][height];
        commandHandler = new CommandHandler();
        register(new HeatMapCommand());
        chuckDataFiles = new ChuckDataFiles();
        chuckDataFiles.loadData();
        register(new PlayerListener());
        workerThread = new Thread(this::processQueue);
        workerThread.setName("HotMap");
        workerThread.start();
        Bukkit.getAsyncScheduler().runAtFixedRate(this, task -> {
            if (!running) return;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PlayerListener.AFK_PLAYERS.contains(player.getUniqueId())) continue;
                ChunkData data = getChunkData(player.getLocation());
                if (data != null) {
                    data.addActivityPlayer();
                }
            }
        }, 0L, 2, TimeUnit.SECONDS);
        Bukkit.getAsyncScheduler().runAtFixedRate(this, task -> chuckDataFiles.saveData(), 60*5L, 60*5, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {
        workerThread.interrupt();
        chuckDataFiles.saveData();
    }

    @Override
    public void reloadConfig() {
        config.reloadConfig();
        config.loadData();
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

    public static void CreateImage(ModeMap mode, ModeColor color){
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();

            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            for (int x = 0; x < HeatMap.matrixMap.length ; x++){
                for (int z = 0; z < HeatMap.matrixMap[x].length ; z++){
                    ChunkData data = matrixMap[x][z];
                    if (data != null) {
                        double i;
                        switch (mode) {
                            case PLAYERS -> i = data.getActivityPlayer();
                            case BREAK_BLOCKS -> i = data.getBreakBlock();
                            case PLACE_BLOCKS -> i = data.getPlaceBlock();
                            default -> i = 0;
                        }
                        i /= 300;
                        i = Math.min(i, 1);
                        if (x < width && z < height) image.setRGB(x, z, interpolateColor(i, color).getRGB());
                    }
                }
            }
            g.dispose();
            ImageIO.write(image, "png", new File(path + File.separator +  "heatmap.png"));
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
        int x = xR + HeatMap.width /2;
        int z = zR + HeatMap.height /2;
        ChunkData data;
        if (x > 0 && z > 0 && x < HeatMap.width && z < HeatMap.height){
            data = HeatMap.matrixMap[x][z];
        }else {
            return null;
        }
        return Objects.requireNonNullElseGet(data, () -> {
            ChunkData chunkData = new ChunkData(xR, zR);
            HeatMap.matrixMap[x][z] = chunkData;
            return chunkData;
        });
    }

    // Función para interpolar colores
    private static Color interpolateColor(double value,@NotNull ModeColor modeColor) {
        switch (modeColor){
            case HOT_COLOR -> {
                Gradient gradient = new Gradient();
                gradient.addGradient(new Color(0, 0, 0), 2)
                        .addGradient(new Color(0, 0, 160), 2)
                        .addGradient(new Color(0, 160, 0), 2)
                        .addGradient(new Color(220, 220, 0), 2)
                        .addGradient(new Color(200, 0, 0), 2)
                        .addGradient(new Color(255, 255, 255), 1);
                return gradient.getColor(value);
            }
            case MONOCHROME -> {
                int i = (int) Math.round(value);
                return new Color(i, i, i);
            }
            default -> throw new IllegalStateException("Unexpected value: " + modeColor);
        }
    }

    public static void enqueueTaskAsynchronously(Runnable task) {
        if (!TASK_QUEUE.offer(task)){
            getInstance().getLogger().severe("Error al añadir la tarea");
        }
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Toma una tarea de la cola y la ejecuta
                Runnable task = TASK_QUEUE.take();
                task.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Si es interrumpido, detenemos el hilo
        }
    }
}
