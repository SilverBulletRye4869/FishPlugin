package silverassist.fishplugin;

import org.bukkit.block.Biome;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.fishplugin.command.Fish;
import silverassist.fishplugin.command.RodData;
import silverassist.fishplugin.system.MainSystem;

public final class FishPlugin extends JavaPlugin {
    public static JavaPlugin plugin = null;
    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        this.saveDefaultConfig();
        FileConfiguration config = getConfig();
        for(Biome biome: Biome.values()){
            if(config.get(biome.name()) !=null)continue;
            config.set(biome.name(), "");
        }
        saveConfig();

        getServer().getPluginManager().registerEvents(new MainSystem(), this);

        PluginCommand command;
        command = getCommand("rod");
        if (command != null) command.setExecutor(new RodData());
        command = getCommand("fish");
        if (command != null) command.setExecutor(new Fish());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
