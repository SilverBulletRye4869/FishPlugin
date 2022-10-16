package silverassist.fishplugin;

import org.bukkit.Bukkit;

public class Function {
    public final String prefix = "§b§l[FishPlugin§b§l]";
    public static void consoleCommand(String command){
        Bukkit.dispatchCommand(FishPlugin.plugin.getServer().getConsoleSender(), command);
    }
}
