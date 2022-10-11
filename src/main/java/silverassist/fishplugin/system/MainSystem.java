package silverassist.fishplugin.system;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import silverassist.fishplugin.FishPlugin;
import silverassist.fishplugin.system.calc.Calc;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainSystem implements Listener{
    public static List<Player> fishModeTrue= new ArrayList<>();

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        //if(!fishModeTrue.contains(e.getPlayer()))return;
        ItemStack item;
        Item FishItem = (Item) e.getCaught();
        if(FishItem==null)return;

        double power = new Calc().CalcMain(e);
        //釣りパワーが負の時はハズレアイテムにする
        if(power < 0){
            FishItem.setItemStack(HazleItem());
            return;
        }
        item = DecideItem(power, e.getPlayer());


        if(CutLine(e.getPlayer()))FishItem.setItemStack(item);
        else{
            e.setCancelled(true);
            e.getPlayer().getWorld().setGameRuleValue("sendCommandFeedback","false");
            Bukkit.dispatchCommand(FishPlugin.plugin.getServer().getConsoleSender(), "minecraft:kill " + e.getHook().getUniqueId());

        }

    }

    private ItemStack DecideItem(double power, Player p){
        FileConfiguration config = FishPlugin.plugin.getConfig();
        Map<String,Integer> fishData = new LinkedHashMap<>();
        AtomicInteger totalWeight = new AtomicInteger(0);
        Biome biome = p.getWorld().getBiome(p.getLocation());

        String[] paths = {"ANY",biome.name()};
        int border = 0; // ANYとbiomeの境
        for(String path: paths) {
            ConfigurationSection keyP = config.getConfigurationSection(path);
            if (keyP != null) {
                keyP.getKeys(false).forEach(key -> {
                    if (config.getInt(path+"." + key + ".min_weight") > power) return;

                    int weight = config.getInt(path+"." + key + ".weight");
                    if (weight == 0) return;

                    String perm = config.getString(path+"."+key+".permission");
                    if(perm!=null){
                        if(!p.hasPermission(perm))return;
                    }

                    String world = config.getString(path+"."+key+".world");
                    if(world !=null){
                        if(!world.equals(p.getWorld().getName()))return;
                    }

                    totalWeight.addAndGet(weight);
                    fishData.put(key, totalWeight.get());
                });
            }
            border = totalWeight.get();
        }
        if(totalWeight.get()==0)return HazleItem(); //魚が見つからなければハズレを返す


        //魚決定
        String fish = null;
        int num = (int)Math.floor(Math.random() * totalWeight.get());
        for(String fishType: fishData.keySet()){
            if(fishData.get(fishType)>num){
                fish = fishType;
                break;
            }
        }

        //魚のキー
        String fishKey;
        if(num < border)fishKey= "ANY."+fish;
        else fishKey = biome.name() +"."+fish;

        //item-type
        String fishTypeStr = config.getString(fishKey + ".item-material");
        if(fishTypeStr==null)return ErrorPaper(power,biome,"「"+fishKey+"」にitem-materialがセットされていません");
        Material fishType = Material.getMaterial(fishTypeStr);
        if(fishType == null)return ErrorPaper(power,biome,"アイテム「"+fishTypeStr+"」は存在しません. ("+fishKey+")");

        return CreateItem(Material.getMaterial(fishTypeStr),config.getString(fishKey+".name"), (List<String>) config.get(fishKey + ".lore"),config.getInt(fishKey));
    }

    private boolean CutLine(Player p){
        ItemStack item = p.getInventory().getItemInMainHand();
        if(item.getType() !=Material.FISHING_ROD)return true;
        double denominator = 7;
        NBTItem nbt = new NBTItem(item);
        if(nbt.hasKey("CutLine"))denominator = nbt.getInteger("CutLine");

        if(Math.random() * 100 < denominator)return true;
        else return false;
    }

    private ItemStack CreateItem(Material material, String name, List<String> lore, int model){
        ItemStack item = new ItemStack(material);

        ItemMeta meta = item.getItemMeta();
        if(name!=null)meta.setDisplayName(name);
        if(lore!=null)meta.setLore(lore);
        meta.setCustomModelData(model);
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack ErrorPaper(double power, Biome biome, String reason){
        List<String> lore = new ArrayList<>();
        lore.add("§f§lエラーが発生しました。");
        lore.add("§f§lこの紙を運営に渡してください。");
        lore.add("§6内容: "+ reason);
        lore.add("§6釣りパワー: "+power + "§6 ,バイオーム: "+biome.name());
        return CreateItem(Material.PAPER, "§c§lエラーペーパー", lore, 0);
    }

    private ItemStack HazleItem(){
        List<String> lore = new ArrayList<>();
        lore.add("§f釣りパワーが不足しているようだ");
        return CreateItem(Material.KELP, "§a§l水草", lore,0);
    }

}
