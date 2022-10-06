package silverassist.fishplugin.system;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import silverassist.fishplugin.system.calc.Calc;

import java.util.ArrayList;
import java.util.List;

public class MainSystem implements Listener{
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        ItemStack item = null;
        Item FishItem = (Item) e.getCaught();
        if(FishItem==null)return;

        double power = new Calc().CalcMain(e);

        //釣りパワーが負の時はハズレアイテムにする
        if(power < 0){
            item = new ItemStack(Material.KELP);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§a§lコンブ");
            List<String> lore = new ArrayList<>();
            lore.set(0,"§f§l釣りパワーが足りないようだ");
            meta.setLore(lore);
            item.setItemMeta(meta);
            FishItem.setItemStack(item);
            return;
        }

    }

}
