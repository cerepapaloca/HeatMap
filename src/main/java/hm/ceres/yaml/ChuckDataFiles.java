package hm.ceres.yaml;

import hm.ceres.HotMap;

public class ChuckDataFiles extends FilesYams {
    public ChuckDataFiles() {
        super("cacheMap", ChunkDataFile.class);
    }

    public void saveData(){
        for (int x = 0 ; x < HotMap.matrixMap.length ; x++){
            for (int z = 0 ; z < HotMap.matrixMap[x].length ; z++){
                if (HotMap.matrixMap[x][z] != null){
                    int xR = x - HotMap.width /2;
                    int zR = z - HotMap.height /2;
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
