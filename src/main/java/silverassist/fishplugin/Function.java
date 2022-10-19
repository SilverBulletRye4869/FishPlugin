package silverassist.fishplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class Function {
    public final String prefix = "§b§l[FishPlugin§b§l]";
    public static void consoleCommand(String command){
        Bukkit.dispatchCommand(FishPlugin.plugin.getServer().getConsoleSender(), command);
    }
    public static void broadCast(String msg){
        FishPlugin.plugin.getServer().broadcastMessage(msg);
    }

    public static String stringReplace(String target, Player p, Location loc){
        Map<String,String> replace = new LinkedHashMap<>();
        double[] pos = {loc.getX(),loc.getY(),loc.getZ()};

        replace.put("{w}", p.getWorld().getName());
        replace.put("{lx}",pos[0]+"");
        replace.put("{ly}",pos[1]+"");
        replace.put("{lz}",pos[2]+"");
        replace.put("{l}", pos[0]+" "+pos[1]+" "+pos[2]);
        replace.put("{p}",p.getName());

        for(String s: replace.keySet())target = target.replace(s,replace.get(s));
        return target;
    }


}
