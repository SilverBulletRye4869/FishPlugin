package silverassist.fishplugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import silverassist.fishplugin.FishPlugin;
import silverassist.fishplugin.system.MainSystem;


public class Fish implements CommandExecutor {
    private final String prefix = "§b§l[§e§lFishPlugin§b§l]§r";
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player)sender;
        if(p==null)return true;
        if(args.length<1){
            sender.sendMessage(prefix+"§a§l現在の釣りモードは§d§l"+MainSystem.fishModeTrue.contains(p)+"§a§lです");
            return true;
        }

        switch (args[0]){
            case "modechange":
                if(MainSystem.fishModeTrue.contains(p)){
                    MainSystem.fishModeTrue.remove(p);
                    p.sendMessage(prefix+"§a§l釣りモードを§d§lfalse§a§lにしました");
                }
                else {
                    MainSystem.fishModeTrue.add(p);
                    p.sendMessage(prefix+"§a§l釣りモードを§d§ltrue§a§lにしました");
                }
                break;

            case "reload":
                FishPlugin.plugin.reloadConfig();
                p.sendMessage(prefix+"§a§lconfigをreloadしました");
                break;

        }
        return true;
    }

}
