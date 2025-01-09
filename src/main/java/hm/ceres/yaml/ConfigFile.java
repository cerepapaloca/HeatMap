package hm.ceres.yaml;

import hm.ceres.HeatMap;

public class ConfigFile extends FileYaml {
    public ConfigFile() {
        super("config.yml", null, true);
    }

    @Override
    public void loadData() {
        loadConfig();
        HeatMap.height = fileYaml.getInt("height");
        HeatMap.width = fileYaml.getInt("width");
    }

    @Override
    public void saveData() {
        fileYaml.set("height", HeatMap.height);
        fileYaml.set("width", HeatMap.width);
        saveConfig();
    }
}
