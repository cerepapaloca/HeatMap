package hm.ceres.yaml;

import hm.ceres.ChunkData;
import hm.ceres.HotMap;
import org.jetbrains.annotations.Nullable;

public class ChunkDataFile extends FileYaml {
    public ChunkDataFile(String filename, String folderName) {
        super(filename, folderName, false);
    }

    @Override
    public void loadData() {
        loadConfig();
        ChunkData data = getChuckData();
        if (data == null) return;
        data.setActivityPlayer(fileYaml.getInt("activity-player"));
        data.setPlaceBlock(fileYaml.getInt("place-block"));
        data.setBreakBlock(fileYaml.getInt("break-block"));
    }

    @Override
    public void saveData() {
        ChunkData data = getChuckData();
        if (data == null) return;
        fileYaml.set("activity-player", data.getActivityPlayer());
        fileYaml.set("place-block", data.getPlaceBlock());
        fileYaml.set("break-block", data.getBreakBlock());
        saveConfig();
    }

    private @Nullable ChunkData getChuckData() {
        String[] split = fileName.split("\\.");
        int xR = Integer.parseInt(split[0]);
        int zR = Integer.parseInt(split[1].replace(".yml",""));
        return HotMap.getChunkData(xR, zR);
    }


}
