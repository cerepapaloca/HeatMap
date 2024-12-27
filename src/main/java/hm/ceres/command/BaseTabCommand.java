package hm.ceres.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
@RequiredArgsConstructor
public abstract class BaseTabCommand {

    private final String name;
    private final String usage;
    private final String permissions;
    private final String description;

    public abstract void execute(CommandSender sender, String[] args);

    public abstract List<String> onTab(CommandSender sender, String[] args);

    protected List<String> listTab(String arg,  List<String> args) {
        return args.stream()
                .filter(name -> name.toLowerCase().contains(arg.toLowerCase()))
                .collect(toList());
    }

    @SuppressWarnings("rawtypes")
    protected List<String> enumsToStrings(Enum[] raw, boolean b){
        String[] strings = new String[raw.length];
        int i = 0 ;
        for (Enum e : raw){
            strings[i] = b ? e.name().toLowerCase() : e.name();
            i++;
        }
        return Arrays.stream(strings).toList();
    }
}
