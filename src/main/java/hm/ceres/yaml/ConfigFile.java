package hm.ceres.yaml;

import hm.ceres.HotMap;

public class ConfigFile extends FileYaml {
    public ConfigFile() {
        super("config.yml", null, true);
    }

    @Override
    public void loadData() {
        loadConfig();
        HotMap.height = fileYaml.getInt("height");
        HotMap.width = fileYaml.getInt("width");
    }

    @Override
    public void saveData() {
        fileYaml.set("height", HotMap.height);
        fileYaml.set("width", HotMap.width);
        saveConfig();
    }
}
