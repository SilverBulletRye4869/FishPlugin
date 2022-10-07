package silverassist.fishplugin.command;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Fishpower implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args[0]==null)return false;
        if(!(sender instanceof Player))return false;
        Player p = (Player) sender;
        ItemStack item = p.getInventory().getItemInMainHand();
        if(item.getType()!= Material.FISHING_ROD)return false;

        NBTItem nbt = new NBTItem(item);
        switch (args[0]){

            case "set":
                if(args.length!=2)return false;
                if(!args[1].matches("-?\\d+"))return false;
                nbt.setInteger("fishpower", Integer.valueOf(args[1]));
                item = nbt.getItem();
                p.getInventory().setItemInMainHand(item);
                p.sendMessage("§a§l手に持っている竿の釣りパワーを§d§l"+args[1]+"§a§lに設定しました。");
                return  true;
            case "get":
                if(args.length!=1)return false;
                if(nbt.hasKey("fishpower")) p.sendMessage("§a§l釣りパワー: §d§l"+nbt.getInteger("fishpower"));
                else p.sendMessage("§c§l釣りパワーが設定されていません");
                return true;
        }
        return false;
    }
}
