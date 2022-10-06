package silverassist.fishplugin;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.fishplugin.command.Rod;
import silverassist.fishplugin.system.MainSystem;

import java.util.Map;

public final class FishPlugin extends JavaPlugin {

    public static JavaPlugin plugin = null;
    public static Map<String, Map<String, Map<String, String>>> fishData;

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new MainSystem(), this);
        PluginCommand command = getCommand("fishpower");
        if (command != null) command.setExecutor(new Rod());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void FishRegister() {

    }
}
