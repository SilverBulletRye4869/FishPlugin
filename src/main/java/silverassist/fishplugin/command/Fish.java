package silverassist.fishplugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import silverassist.fishplugin.FishPlugin;

public class Fish implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1){
            /*helpの処理*/
            return true;
        }

        switch (args[0]){
            case "reload":
                new FishPlugin().saveDefaultConfig();
                sender.sendMessage("§a§lconfigをreloadしました");
                break;
        }
        return false;
    }

}
