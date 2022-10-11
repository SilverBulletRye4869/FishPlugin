package silverassist.fishplugin.command;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RodData implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<2)return false;
        if(!(sender instanceof Player))return false;
        Player p = (Player) sender;
        ItemStack item = p.getInventory().getItemInMainHand();
        if(item.getType()!= Material.FISHING_ROD)return false;
        NBTItem nbt = new NBTItem(item);
        switch (args[0]){
            case "set":
                if(args.length!=3)return false;
                if(!args[2].matches("-?\\d+"))return false;
                switch (args[1]){
                    case "power":
                        nbt.setInteger("fishpower", Integer.valueOf(args[2]));
                        p.sendMessage("§a§l手に持っている竿の釣りパワーを§d§l"+args[2]+"§a§lに設定しました。");
                        break;
                    case "cutline":
                        nbt.setDouble("cutline", Double.valueOf(args[2]));
                        p.sendMessage("§a§l手に持っている竿の糸の耐久値を§d§l"+args[2]+"§a§lに設定しました。");
                }
                item = nbt.getItem();
                p.getInventory().setItemInMainHand(item);
                return  true;

            case "get":
                switch (args[1]){
                    case "power":
                        if(nbt.hasKey("fishpower")) p.sendMessage("§a§l釣りパワー: §d§l"+nbt.getInteger("fishpower"));
                        else p.sendMessage("§c§l釣りパワーが設定されていません");
                        break;
                    case "cutline":
                        if(nbt.hasKey("cutline")) p.sendMessage("§a§l釣り糸耐久値ー: §d§l"+nbt.getInteger("cutline"));
                        else p.sendMessage("§c§l釣り糸耐久値が設定されていません");
                        break;
                }

                return true;
        }
        return false;
    }
}
