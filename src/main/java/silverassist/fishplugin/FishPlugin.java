package silverassist.fishplugin;

import org.bukkit.plugin.java.JavaPlugin;
import silverassist.fishplugin.system.MainSystem;

public final class FishPlugin extends JavaPlugin {
    public static JavaPlugin plugin = null;
    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new MainSystem(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
