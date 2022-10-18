package silverassist.fishplugin.system;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
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

import static silverassist.fishplugin.Function.consoleCommand;

public final class MainSystem implements Listener{
    public static List<Player> fishModeTrue= new ArrayList<>();
    private final FileConfiguration config;
    private ItemStack item;
    private String command;

    public MainSystem(){
        this.config = FishPlugin.plugin.getConfig();
        this.command="";
        this.item = null;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        //if(!fishModeTrue.contains(e.getPlayer()))return; //デバック時コメアウト
        Item FishItem = (Item) e.getCaught();
        if(FishItem==null)return;

        //釣りパワー計算
        double power = new Calc().CalcMain(e);
        //釣りパワーが負の時はハズレアイテムにする
        if(power < 0){
            HazleItem();
            FishItem.setItemStack(this.item);
            return;
        }

        //アイテム,コマンド決定
        DecideItem(power, e.getPlayer());


        //consoleコマンドをRUN
        commandReplace(e);
        consoleCommand(this.command);
        //釣り糸の切れる設定
        if(CutLine(e.getPlayer())||this.item==null){
            e.setCancelled(true);
            e.getPlayer().getWorld().setGameRuleValue("sendCommandFeedback","false");
            consoleCommand("minecraft:kill " + e.getHook().getUniqueId());
            return;
        }

        FishItem.setItemStack(this.item);



    }

    private void DecideItem(double power, Player p){

        Map<String,Integer> fishData = new LinkedHashMap<>();
        AtomicInteger totalWeight = new AtomicInteger(0);
        Biome biome = p.getWorld().getBiome(p.getLocation());

        String[] paths = {"ANY",biome.name()};
        int border = 0; // ANYとbiomeの境
        for(String path: paths) {
            ConfigurationSection keyP = config.getConfigurationSection(path);
            if (keyP != null) {
                keyP.getKeys(false).forEach(key -> {
                    if (config.getInt(path+"." + key + ".min_power") > power) return; //最少パワー
                    if(config.get(path+"." + key + ".max_power")!=null){ // 最大パワー
                        if(config.getInt(path+"." + key + ".max_power") < power)return;
                    }

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
        if(totalWeight.get()==0) {
            HazleItem();
            return;
        }  //魚が見つからなければハズレを返す


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
        String command = config.getString(fishKey+".console_command");


        //itemセット
        if(fishTypeStr!=null){
            Material fishType = Material.getMaterial(fishTypeStr);
            if(fishType != null)this.item =CreateItem(Material.getMaterial(fishTypeStr),config.getString(fishKey+".name"), (List<String>) config.get(fishKey + ".lore"),config.getInt(fishKey));
        }
        //commandセット

        if(command != null)this.command=command;

        //コマンドもItemStackも設定されていなければErrorPaper
        if(this.item==null&&this.command ==null)ErrorPaper(power,biome,"「"+fishKey+"」に正しいitem-material又はコマンドがセットされていません");

    }

    private boolean CutLine(Player p){
        ItemStack item = p.getInventory().getItemInMainHand();
        if(item.getType() !=Material.FISHING_ROD)return true;
        double denominator = 100.0/7.0;
        NBTItem nbt = new NBTItem(item);
        if(nbt.hasKey("cutline"))denominator = nbt.getInteger("cutline");
        return Math.random() * 100 < denominator;
    }
    private void commandReplace(PlayerFishEvent e){
        Player p = e.getPlayer();
        Map<String,String> replace = new LinkedHashMap<>();
        Location loc = e.getHook().getLocation();
        double[] pos = {loc.getX(),loc.getY(),loc.getZ()};

        replace.put("{w}", p.getWorld().getName());
        replace.put("{lx}",pos[0]+"");
        replace.put("{ly}",pos[1]+"");
        replace.put("{lz}",pos[2]+"");
        replace.put("{l}", pos[0]+" "+pos[1]+" "+pos[2]);
        replace.put("{p}",p.getName());

        for(String s: replace.keySet())this.command = this.command.replace(s,replace.get(s));
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
    private void ErrorPaper(double power, Biome biome, String reason){
        List<String> lore = new ArrayList<>();
        lore.add("§f§lエラーが発生しました。");
        lore.add("§f§lこの紙を運営に渡してください。");
        lore.add("§6内容: "+ reason);
        lore.add("§6釣りパワー: "+power + "§6 ,バイオーム: "+biome.name());
        this.item = CreateItem(Material.PAPER, "§c§lエラーペーパー", lore, 0);
    }

    private void HazleItem(){
        List<String> lore = new ArrayList<>();
        lore.add("§f釣りパワーが不足しているようだ");
        this.item = CreateItem(Material.KELP, "§a§l水草", lore,0);
    }


}
