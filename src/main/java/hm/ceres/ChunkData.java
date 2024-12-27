package hm.ceres;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
@RequiredArgsConstructor
public class ChunkData {

    private final int x;
    private final int z;

    private int placeBlock = 0;
    private int breakBlock = 0;
    private int activityPlayer = 0;

    public void addActivityPlayer() {
        activityPlayer++;
    }
    public void addBreakBlock() {
        breakBlock++;
    }
    public void addPlaceBlock() {
        placeBlock++;
    }
}
