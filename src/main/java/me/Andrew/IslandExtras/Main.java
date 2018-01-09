package me.Andrew.IslandExtras;

/**
 * Created by Andrew on 18/04/2017.
 */

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.Andrew.IslandExtras.EnderChest.IslandInventoryStorage;
import me.Andrew.IslandExtras.IslandBorder.Functions;
import me.Andrew.IslandExtras.Managers.EventManager;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Main extends JavaPlugin {
    static Main MA;
    public EventManager EM;
    public ASkyBlockAPI api;
    public ArrayList<Player> bordersOpen;
    public ProtocolManager protocolManager;
    public void onEnable() {
        MA = this;
        EM = new EventManager();
        EM.setupEvents();
        api = ASkyBlockAPI.getInstance();
        saveDefaultConfig();
        new IslandInventoryStorage();
        bordersOpen = new ArrayList<>();
        protocolManager = ProtocolLibrary.getProtocolManager();

    }
    public void onDisable(){
        IslandInventoryStorage.getInstance().saveToConfig();
    }
    public static Main getInst(){
        return MA;
    }
    public ProtocolManager getProtocolManager(){return protocolManager;}
    public WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(cc("&cYou must be a player to run this command"));
        }
        Player p = (Player) sender;
        if(label.equalsIgnoreCase("border")||label.equalsIgnoreCase("b")){
        if(args.length == 1){
                String subcommand = args[0];
            if(subcommand.equalsIgnoreCase("show")){
                if(!bordersOpen.contains(p)){
                    new Functions().sendIslandBorder(p,10);
                }else{
                    p.sendRawMessage(cc("&cYour border is still showing"));
                }

            }else if(subcommand.equalsIgnoreCase("hide")){
                if(bordersOpen.contains(p)){
                    new Functions().removeBorder(p);
                }else{
                    p.sendRawMessage(cc("&cYour border is not currently showing"));
                }
            }else if(subcommand.equalsIgnoreCase("amount")){

            }else{
                sendBorderCommandHelp(p, label);
            }
            }else{
            sendBorderCommandHelp(p, label);
        }
        }else if(label.equalsIgnoreCase("islandenderchest")||label.equalsIgnoreCase("iec")){
            if(p.hasPermission("islandextras.islandenderchest")){
                p.openInventory(IslandInventoryStorage.getInstance().getInventory(p));
                p.playSound(p.getLocation(), Sound.BLOCK_ENDERCHEST_OPEN, 100, 100);
            }else{
                p.sendMessage(cc("&cYou don't have permission to do this"));
            }
        }
        return false;
    }
    public String cc(String msg){
        return ChatColor.translateAlternateColorCodes('&',msg);
    }
    public void sendBorderCommandHelp(Player p, String label){
        p.sendMessage(cc("&a-===[IVB]===-"));
        p.sendMessage(cc("&aUsage:"));
        p.sendMessage(cc("&a/"+label+" show - Shows the island border for 10 seconds"));
        p.sendMessage(cc("&a/"+label+" hide - Removes the island border"));
        p.sendMessage(cc("&a-===[IVB]===-"));
    }
}

