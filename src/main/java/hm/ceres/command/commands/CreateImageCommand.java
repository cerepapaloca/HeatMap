package hm.ceres.command.commands;

import hm.ceres.HeatMap;
import hm.ceres.ModeColor;
import hm.ceres.command.BaseTabCommand;
import hm.ceres.ModeMap;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CreateImageCommand extends BaseTabCommand {
    public CreateImageCommand() {
        super("hotmap",
                "/createImage",
                "hotmap.command.createimage",
                "Creas la imagen con el mapa de calor");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0].toLowerCase()){
            case "create" -> new BukkitRunnable() {
                @Override
                public void run() {
                    ModeMap modeMap;
                    ModeColor modeColor;
                    try {
                        modeMap = ModeMap.valueOf(args[1].toUpperCase());
                    }catch (Exception e){
                        sender.sendMessage("modo del mapa invalido");
                        return;
                    }
                    try {
                        modeColor = ModeColor.valueOf(args[2].toUpperCase());
                    }catch (Exception e){
                        sender.sendMessage("modo del color invalido");
                        return;
                    }

                    try {
                        HeatMap.CreateImage(modeMap, modeColor);
                        sender.sendMessage("Fue creado con exitosamente");
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
            }.runTaskAsynchronously(HeatMap.getInstance());
            case "stop" -> {
                if (!HeatMap.running){
                    sender.sendMessage("Ya estaba detenido");
                }else {
                    sender.sendMessage("Se detuvo");
                    HeatMap.running = false;
                }
            }
            case "start" -> {
                if (HeatMap.running){
                    sender.sendMessage("Ya estaba corriendo");
                }else {
                    sender.sendMessage("Comenzó a correr");
                    HeatMap.running = true;
                }
            }
            case "reload" -> {
                HeatMap.config.reloadConfig();
                HeatMap.config.loadData();
                sender.sendMessage("Configuración recargada");
            }
            case "save" -> {
                HeatMap.getChuckDataFiles().saveData();
                sender.sendMessage("Datos guardados");
            }
            default -> sender.sendMessage("Error con los argumentos");
        }

    }

    @Override
    public List<String> onTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                return listTab(args[0], List.of("create", "stop", "start", "reload", "save"));
            }
            case 2 -> {
                if (args[0].toLowerCase().equals("create")) {
                    return listTab(args[1], enumsToStrings(ModeMap.values(), true));
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("create")) {
                    return listTab(args[2], enumsToStrings(ModeColor.values(), true));
                }
            }
        }
        return null;
    }
}
