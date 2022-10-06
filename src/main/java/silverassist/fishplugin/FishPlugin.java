package silverassist.fishplugin;

import org.bukkit.block.Biome;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.fishplugin.command.Rod;
import silverassist.fishplugin.system.MainSystem;

import java.util.List;
import java.util.Map;

public final class FishPlugin extends JavaPlugin {

    public static JavaPlugin plugin = null;
    public static Map<String, Map<String,List<Map<?, ?>> > > fishData;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        plugin = this;
        // Plugin startup logic
        for(Biome biome: Biome.values()){
            if(getConfig().get(biome.name()) !=null)continue;
            getConfig().set(biome.name(), "");
        }
        saveConfig();

        getServer().getPluginManager().registerEvents(new MainSystem(), this);
        PluginCommand command = getCommand("fishpower");
        if (command != null) command.setExecutor(new Rod());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
