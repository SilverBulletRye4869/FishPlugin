package silverassist.fishplugin.system.calc;


import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import silverassist.fishplugin.FishPlugin;

public class Calc {
    private final double BASE_POWER = 10;
    public double CalcMain(PlayerFishEvent e){

        Player p = e.getPlayer();
        World w = p.getWorld();

        double power = this.BASE_POWER; //基礎パワー
        power += CalcRod(p); //竿パワー　（基本パワー + 宝釣りパワー）
        power += CalcExp(p); //経験値パワー
        power += CalcLuck(p);//幸運パワー
        power += CalcWhether(w);//天気パワー
        power += CalcTime(w);//時間パワー
        power += CalcMoon(w);//月齢パワー

        //釣り場の大きさペナルティ( ×0.25 ~ ×1.0)
        FishPlugin.plugin.getServer().broadcastMessage(String.valueOf(power));
        CalcWater calcWater = new CalcWater(e.getHook().getWorld());
        Location loc = e.getHook().getLocation();
        int dy = 0;
        while (true){
            if(loc.add(0,dy+1,0).getBlock().getType() == Material.WATER)dy++;
            else break;

            if(dy>10)break;
        }
        int waterSize = (int) Math.floor((calcWater.SizeCheck(e.getHook().getLocation().add(0,dy,0), new int[]{0,0}) - 1)*3/50);
        power *= calcWater.CalcPenaByWater(waterSize);

        FishPlugin.plugin.getServer().broadcastMessage("水辺の大きさ: " + waterSize);

        return power;
    }
    //ロッドの計算
    private double CalcRod(Player p){
        ItemStack item = p.getInventory().getItemInMainHand();
        double RodRank = 0;
        if(item.getType() == Material.AIR)return 0;
        if(new NBTItem(item).hasKey("fishpower"))RodRank += new NBTItem(item).getInteger("fishpower");
        RodRank += item.getEnchantmentLevel(Enchantment.LUCK) * 10.0; // + レベル*10
        return RodRank;

    }

    //経験値レベルの計算
    private double CalcExp(Player p){
        double lv = p.getExpToLevel();
        double power = 4.0 * Math.sqrt(lv);
        return Math.min(40,power);
    }

    //幸運の計算
    private double CalcLuck(Player p){
        double luck = p.getAttribute(Attribute.GENERIC_LUCK).getValue();
        double maxluck = 5;
        double power = Math.max( (30.0 / maxluck) * Math.min(maxluck, luck) , -30.0);
        return power;
    }

    //宝釣りの計算
    private double CalcEnch(ItemStack item){

        return 0;
    }

    //天気の計算
    private double CalcWhether(World w){
        double power = 0;
        if(w.hasStorm())power +=20;
        if(w.isThundering())power +=10;
        return power;
    }


    //時間の計算
    private double CalcTime(World w){
        double time = (w.getTime()%24000)/1000 + 6.0;
        if(time>=24) time-=24;

        if((time>=4.5&&time<6.0) || (time>=18&&time<=19.5))return 30; //4:30-6:00, 18:00-19:30
        else if((time>=9&&time<=15)||time>=21.3||time<=2.7)return -20;//9:00-15:00, 21:18-2:42
        return 0; //その他
    }

    private double CalcMoon(World w){
        int moonType = (int) (Math.floor(w.getFullTime() / 24000) % 8);
        switch (moonType){
            //満月
            case 0:
                return 30;
            //十三夜,十八夜
            case 1:
            case 7:
                return 10;
            //三日月, 二十五夜
            case 3:
            case 5:
                return -5;
            //新月
            case 4:
                return -20;
            //その他
            default:
                return 0;
        }
    }
}
