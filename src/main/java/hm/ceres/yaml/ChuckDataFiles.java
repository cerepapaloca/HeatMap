package hm.ceres.yaml;

import hm.ceres.HeatMap;

public class ChuckDataFiles extends FilesYams {
    public ChuckDataFiles() {
        super("cacheMap", ChunkDataFile.class);
    }

    public void saveData(){
        for (int x = 0; x < HeatMap.matrixMap.length ; x++){
            for (int z = 0; z < HeatMap.matrixMap[x].length ; z++){
                if (HeatMap.matrixMap[x][z] != null){
                    int xR = x - HeatMap.width /2;
                    int zR = z - HeatMap.height /2;
                    ChunkDataFile fy = (ChunkDataFile) getConfigFile(xR/100 + "." + zR/100, true);
                    fy.saveData(xR%100, zR%100);
                }
            }
        }
    }

    public void loadData(){
        for (FileYaml file : getConfigFiles()){
            file.loadData();
        }
    }
}
