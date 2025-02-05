package hm.ceres.yaml;

import hm.ceres.HeatMap;
import lombok.Getter;

import java.io.File;
import java.util.HashSet;

public abstract class FilesYams {

    protected String folderName;
    @Getter
    protected HashSet<FileYaml> configFiles;
    private final Class<? extends FileYaml> fileclass;

    public FilesYams(String folderName, Class<? extends FileYaml> fileclass){
        this.folderName = folderName;
        this.configFiles = new HashSet<>();
        this.fileclass = fileclass;
        configure();
    }

    public void configure() {
        createFolder();
        reloadConfigs();
    }

    public void reloadConfigs(){
        this.configFiles = new HashSet<>();
        registerConfigFiles();
        //loadConfigs();
    }

    public void createFolder(){
        File folder;
        try {
            folder = new File(HeatMap.getInstance().getDataFolder() + File.separator + folderName);
            if(!folder.exists()){
                folder.mkdirs();
            }
        } catch(SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveConfigFiles() {
        for (FileYaml configFile : configFiles) {
            configFile.saveConfig();
        }
    }

    public void registerConfigFiles(){
        String path = HeatMap.getInstance().getDataFolder() + File.separator + folderName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    registerConfigFile(file.getName());
                }
            }
        }
    }

    public FileYaml getConfigFile(String pathName, boolean createNew) {
        for (FileYaml configFile : configFiles) {
            if (configFile.fileName.equals(pathName + ".yml")) {
                return configFile;
            }
        }
        if (createNew){
            return registerConfigFile(pathName + ".yml");
        }else {
            return null;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteConfigFile(String nameFile) {
        nameFile = nameFile + ".yml";
        for (FileYaml configFile : configFiles) {
            if (folderName != null){
                nameFile = folderName + File.separator + nameFile;
            }
            if (configFile.getPath().equals(nameFile)) {
                configFiles.remove(configFile);
                configFile.getFile().delete();
                return;
            }
        }
    }

    public FileYaml registerConfigFile(String fileName) {
        try {
            FileYaml config = fileclass.getConstructor(String.class, String.class)
                    .newInstance(fileName, folderName);
            configFiles.add(config);
            config.reloadConfig();
            return config;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear instancia de " + fileclass.getName(), e);
        }
    }
}
