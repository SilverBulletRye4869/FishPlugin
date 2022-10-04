package silverassist.fishplugin.system;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import silverassist.fishplugin.system.Calc;

public class MainSystem implements Listener {
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        ItemStack item = new ItemStack(Material.LAVA_BUCKET);
        Item FishItem = (Item) e.getCaught();
        if(FishItem==null)return;

        double power = new Calc().CalcMain(e);


    }

}
