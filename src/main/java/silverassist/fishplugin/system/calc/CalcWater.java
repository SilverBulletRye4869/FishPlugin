package silverassist.fishplugin.system.calc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class CalcWater {
    private Map<String, Boolean> SearchFlag;
    private World world;
    private int count;
    public CalcWater(World world){
        this.SearchFlag = new HashMap<>();
        this.world = world;
        this.count = 0;
    }
    int SizeCheck(Location loc, int[] delta_before/*{dx,dz}*/){

        if(world.getBlockAt(loc).getType() != Material.WATER)return 0;
        SearchFlag.put(CoordRound(loc),true);
        int[] delta = new int[4];
        System.arraycopy(delta_before, 0, delta,0 , 2);

        Location loc2 = LocationCopy(loc);
        if(world.getBlockAt(loc2.add(0,-1,0)).getType()== Material.WATER &&
                world.getBlockAt(loc2.add(0,-1,0)).getType()== Material.WATER)count++;

        //xマイナス方向
        if(Math.abs(delta[0])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(-1,0,0);
            if(SearchFlag.get(CoordRound(loc2)) == null){
                delta[0]--;
                SizeCheck(loc2,delta);
                delta[0]++;
            }
        }
        //xプラス方向
        if(Math.abs(delta[0])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(1,0,0);
            if(SearchFlag.get(CoordRound(loc2)) == null){
                delta[0]++;
                SizeCheck(loc2,delta);
                delta[0]--;
            }
        }
        //zマイナス方向
        if(Math.abs(delta[1])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(0,0,-1);
            if(SearchFlag.get(CoordRound(loc2)) == null){
                delta[1]--;
                SizeCheck(loc2,delta);
                delta[1]++;
            }
        }
        //zプラス方向
        if(Math.abs(delta[1])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(0,0,1);
            if(SearchFlag.get(CoordRound(loc2)) == null){
                delta[1]++;
                SizeCheck(loc2,delta);
                delta[1]--;
            }
        }
        return count;
    }

    private Location LocationCopy(Location loc){
        return new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ());
    }

    //丸め誤差防止用
    private String CoordRound(Location loc){
        return loc.getWorld()+"-"+Math.round(loc.getX())+"-"+Math.round(loc.getZ());
    }



}
