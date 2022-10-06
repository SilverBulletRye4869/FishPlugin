package silverassist.fishplugin.system;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import silverassist.fishplugin.FishPlugin;
import silverassist.fishplugin.system.calc.Calc;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainSystem implements Listener{
    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {

        ItemStack item = null;
        Item FishItem = (Item) e.getCaught();
        if(FishItem==null)return;

        double power = new Calc().CalcMain(e);

        //釣りパワーが負の時はハズレアイテムにする
        if(power < 0){
            List<String> lore = new ArrayList<>();
            lore.set(0,"§f釣りパワーが不足しているようだ");
            item = CreateItem(Material.KELP, "§a§l水草", lore,0);
            FishItem.setItemStack(item);
            return;
        }
        item = DecideItem(power, e.getPlayer().getWorld().getBiome(e.getPlayer().getLocation()));

    }

    private ItemStack CreateItem(Material material, String name, List<String> lore, int model){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack DecideItem(double power, Biome biome){
        FileConfiguration config = FishPlugin.plugin.getConfig();
        Map<String,Integer> fishData = new HashMap<>();
        AtomicInteger totalWeight = new AtomicInteger(0);

        String[] paths = {"ANY",biome.name()};
        for(String path: paths) {
            ConfigurationSection keyP = config.getConfigurationSection(path);
            if (keyP != null) {
                keyP.getKeys(false).forEach(key -> {
                    int min_weight = config.getInt(path+"." + key + ".min_weight");
                    if (min_weight > power) return;
                    int weight = config.getInt(path+"." + key + ".weight");
                    if (Objects.isNull(weight) || weight == 0) return;
                    fishData.put(key, totalWeight.get());
                    totalWeight.addAndGet(weight);
                });
            }
        }
        FishPlugin.plugin.getServer().broadcastMessage("->"+totalWeight.get());
        ItemStack item = null;
        return item;
    }



}
