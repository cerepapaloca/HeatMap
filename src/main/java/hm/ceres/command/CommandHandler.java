package hm.ceres.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

@Getter
public final class CommandHandler implements TabExecutor {
    private final HashSet<BaseTabCommand> commands = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        for (BaseTabCommand command : commands) {// Pasa por todas las clases para saber que comando es
            if (!(cmd.getName().equals(command.getName()))) continue;
            try {
                command.execute(sender, args);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
            break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        for (BaseTabCommand command : commands) {
            if (!(cmd.getName().equals(command.getName()))) continue;
            return command.onTab(sender, args);
        }
        return null;
    }
}
