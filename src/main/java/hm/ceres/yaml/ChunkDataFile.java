package hm.ceres.yaml;

import hm.ceres.ChunkData;
import hm.ceres.HeatMap;
import org.bukkit.configuration.ConfigurationSection;

public class ChunkDataFile extends FileYaml {
    public ChunkDataFile(String filename, String folderName) {
        super(filename, folderName, false);
    }

    @Override
    public void loadData() {
        loadConfig();
        String[] split = fileName.split("\\.");
        int xR = Integer.parseInt(split[0]);
        int zR = Integer.parseInt(split[1].replace(".yml",""));
        for (String x : fileYaml.getKeys(false)){
            ConfigurationSection cs = fileYaml.getConfigurationSection(x);
            if (cs == null) continue;
            for (String z : cs.getKeys(false)){
                String path = x + "." + z + ".";
                ChunkData data = HeatMap.getChunkData((xR)*100 + Integer.parseInt(x), (zR)*100 + Integer.parseInt(z));
                if (data == null) continue;
                data.setActivityPlayer(fileYaml.getInt( path + "activity-player"));
                data.setPlaceBlock(fileYaml.getInt(path + "place-block"));
                data.setBreakBlock(fileYaml.getInt(path + "break-block"));
            }
        }
    }

    @Override
    public void saveData() {

    }

    public void saveData(int x, int z) {
        String[] split = fileName.split("\\.");
        int xR = Integer.parseInt(split[0]);
        int zR = Integer.parseInt(split[1].replace(".yml",""));
        ChunkData data = HeatMap.getChunkData((xR)*100 + x, (zR)*100 + z);
        String path = x + "." + z + ".";
        if (data == null) return;
        fileYaml.set(path + "activity-player", data.getActivityPlayer());
        fileYaml.set(path + "place-block", data.getPlaceBlock());
        fileYaml.set(path + "break-block", data.getBreakBlock());
        saveConfig();
    }
}
