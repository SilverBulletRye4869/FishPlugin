package silverassist.fishplugin.system.calc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class CalcWater {
    private Map<Location, Boolean> SearchFlag;
    private World world;
    public CalcWater(World world){
        this.SearchFlag = new HashMap<>();
        this.world = world;
    }
    int SizeCheck(Location loc, int delta_before[]/*{dx,dz}*/){

        if(world.getBlockAt(loc).getType() != Material.WATER)return 0;
        SearchFlag.put(loc,true);
        int[] delta = new int[4];
        System.arraycopy(delta_before, 0, delta,0 , 2);

        int count = 0;
        Location loc2 = LocationCopy(loc);
        if(world.getBlockAt(loc2.add(0,-1,0)).getType()== Material.WATER &&
                world.getBlockAt(loc2.add(0,-1,0)).getType()== Material.WATER)count++;

        //xマイナス方向
        if(Math.abs(delta[0])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(-1,0,0);
            if(SearchFlag.get(loc2) == null){
                delta[0]--;
                count += SizeCheck(loc2,delta);
                delta[0]++;
            }
        }
        //xプラス方向
        if(Math.abs(delta[0])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(1,0,0);
            if(SearchFlag.get(loc2) == null){
                delta[0]++;
                count += SizeCheck(loc2,delta);
                delta[0]--;
            }
        }
        //zマイナス方向
        if(Math.abs(delta[1])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(0,0,-1);
            if(SearchFlag.get(loc2) == null){
                delta[1]--;
                count += SizeCheck(loc2,delta);
                delta[1]++;
            }
        }
        //zプラス方向
        if(Math.abs(delta[1])<9&&count<100){
            loc2 = LocationCopy(loc);
            loc2.add(0,0,1);
            if(SearchFlag.get(loc2) == null){
                delta[1]++;
                count += SizeCheck(loc2,delta);
                delta[1]--;
            }
        }
        return count;
    }

    private Location LocationCopy(Location loc){
        Location loc2 = new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ());
        return loc2;
    }

    double CalcPenaByWater(int waterSize){
        switch (waterSize){
            case 5:
                return 0.75;
            case 4:
                return 2/3;
            case 3:
                return 0.5;
            case 2:
                return 1/3;
            case 1:
            case 0:
                return 0.25;
            default:
                return 1.0;
        }
    }

}
