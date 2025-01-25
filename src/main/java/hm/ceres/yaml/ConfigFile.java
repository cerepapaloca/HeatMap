package hm.ceres.yaml;

import hm.ceres.HeatMap;

public class ConfigFile extends FileYaml {
    public ConfigFile() {
        super("config.yml", null, true);
    }

    @Override
    public void loadData() {
        HeatMap.height = fileYaml.getInt("height");
        HeatMap.width = fileYaml.getInt("width");
        HeatMap.path = fileYaml.getString("path");
    }

    @Override
    public void saveData() {
        fileYaml.set("height", HeatMap.height);
        fileYaml.set("width", HeatMap.width);
        fileYaml.set("path", HeatMap.path);
        saveConfig();
    }
}
